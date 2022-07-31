package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.ScaleDimP2;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.TripleCommentType;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.dto.ScaleDimP1;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.kb.Triple;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.kb.TripleComment;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.ScaleDimP2Repo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.TripleCommentRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.service.CKQAService;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.CompletionResult;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Result;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Scale;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Tuple;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kb")
public class KBController {

  @Resource
  private CKQAService ckqaService;

  @Resource
  private TripleCommentRepo tripleCommentRepo;

  @Resource
  private ResponseUtil responseUtil;

  @Resource(name = "elasticsearchClient")
  private RestHighLevelClient highLevelClient;

  @Resource
  private UploadFile uploadFile;

  public static String esIndex = "cskg_combined";

  @Resource
  private ScaleDimP2Repo scaleDimP2Repo;

  private final String uniqueScaleId = "iScaleId";

  private List<Triple> searchTriples(SearchRequest request, RequestOptions options) throws IOException {
    SearchResponse searchResponse = highLevelClient.search(request, options);
    return Arrays.stream(searchResponse.getHits().getHits()).map(searchHit -> {
      Triple triple = new Triple();
      triple.setId(String.valueOf(searchHit.getId()));
      triple.setSubject(String.valueOf(searchHit.getSourceAsMap().get("subject")));
      triple.setRelation(String.valueOf(searchHit.getSourceAsMap().get("relation")));
      triple.setObject(String.valueOf(searchHit.getSourceAsMap().get("object")));
      Object score = searchHit.getSourceAsMap().get("score");
      if (score != null) {
        triple.setScore(Double.valueOf(String.valueOf(score)));
      }
      return triple;
    }).collect(Collectors.toList());
  }

  @GetMapping("/q")
  Response<List<Triple>> search(
      @RequestParam(required = false) String subject,
      @RequestParam(required = false) String object,
      @RequestParam(required = false) String relation) throws IOException {
    SearchRequest request = new SearchRequest(esIndex);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    if (subject == null && object == null) {
      return new Response<>(null, new ArrayList<>());
    }

    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    if (subject != null) {
      boolQueryBuilder.must(QueryBuilders.matchQuery("subject", subject));
    }
    if (object != null) {
      boolQueryBuilder.must(QueryBuilders.matchQuery("object", object));
    }
    if (relation != null) {
      boolQueryBuilder.must(QueryBuilders.matchQuery("relation", relation));
    }
    searchSourceBuilder.query(boolQueryBuilder);

    searchSourceBuilder.size(3000);
    request.source(searchSourceBuilder);
    return new Response<>(null, searchTriples(request, RequestOptions.DEFAULT));
  }

  @GetMapping("/extraction")
  Response<List<Tuple>> getExtraction(
      @RequestParam String query) {
    return new Response<>(null, ckqaService.getExtraction(query));
  }

  @GetMapping("/triple/comment")
  Response<List<TripleComment>> getComment(@RequestParam String tripleId) {
    return new Response<>(null, tripleCommentRepo.findAllByTripleId(tripleId));
  }

  @PutMapping("/triple/comment")
  Response<Boolean> putComment(
      @RequestParam String tripleId,
      @RequestParam String type,
      @RequestParam(required = false) String data) {
    TripleComment tripleComment = new TripleComment();
    tripleComment.setTripleId(tripleId);
    tripleComment.setType(TripleCommentType.valueOf(type).name());
    tripleComment.setData(data);
    tripleComment.setUpvote(0);
    tripleComment.setDownvote(0);
    tripleCommentRepo.save(tripleComment);
    return responseUtil.success();
  }

  @PostMapping("/triple/comment/upOrDown")
  Response<Boolean> commentVote(@RequestParam String id, @RequestParam boolean isUpvote, @RequestParam boolean isCancel) {
    TripleComment tripleComment = tripleCommentRepo.findById(id).get();
    if (isUpvote) {
      tripleComment.setUpvote(tripleComment.getUpvote() + (isCancel ? -1 : 1));
    } else {
      tripleComment.setDownvote(tripleComment.getDownvote() + (isCancel ? -1 : 1));
    }

    tripleCommentRepo.save(tripleComment);
    return responseUtil.success();
  }

  @GetMapping("/similar/bm25")
  Response<List<Triple>> getSimilarBm25(@RequestParam String query) throws IOException {
    SearchRequest request = new SearchRequest(esIndex);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(20);
    request.source(searchSourceBuilder);
    searchSourceBuilder.query(QueryBuilders.matchQuery("query", query));

    return new Response<>(null, searchTriples(request, RequestOptions.DEFAULT));
  }

  @PostMapping("/similar/knn")
  Response<List<Triple>> getSimilarKNN(@RequestBody String vector) throws IOException {
    SearchRequest request = new SearchRequest(esIndex);
    String parsedVector = vector.substring(0, vector.length() - 1).replaceAll("%2C", ",");
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(120000)
        .setSocketTimeout(120000)
        .build();
    RequestOptions options = RequestOptions.DEFAULT.toBuilder()
        .setRequestConfig(requestConfig)
        .build();
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(20);
    request.source(searchSourceBuilder);
    searchSourceBuilder.query(QueryBuilders.wrapperQuery(String.format(
        "{\n" +
        "  \"script_score\": {\n" +
        "    \"query\": {\n" +
        "      \"exists\": {" +
        "        \"field\": \"vector\"" +
        "      }\n" +
        "    },\n" +
        "    \"script\": {\n" +
        "      \"source\": \"cosineSimilarity(params.queryVector, 'vector') + 1.0\",\n" +
        "      \"params\": {\n" +
        "        \"queryVector\": [%s]\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}",
        parsedVector)));

    return new Response<>(null, searchTriples(request, options));
  }

  @GetMapping("/qimg")
  Response<String> searchImg(@RequestParam String qimg) {
    // String rootPath = System.getProperty("user.dir");
    // String imagePath = rootPath.replace('\\', '/') +
    // "/src/main/resources/static/images/kbImage";
    String imagePath = "/home/ubuntu/cs-platform/static/images/kbImage";
    File jpg_file = new File(imagePath + '/' + qimg, "google_0001.jpg");
    File png_file = new File(imagePath + '/' + qimg, "google_0001.png");
    if (jpg_file.exists())
      return new Response<>(null, "images/kbImage/" + qimg + "/google_0001.jpg");
    if (png_file.exists())
      return new Response<>(null, "images/kbImage/" + qimg + "/google_0001.png");
    return new Response<>(null, "NA");
  }

  @GetMapping("/qa/mask")
  Response<List<Result>> qaMask(
      @RequestParam String q,
      @RequestParam Boolean includeNone,
      @RequestParam Boolean includeCSKG) {
    return new Response<>(null, ckqaService.getMaskResult(q, includeNone, includeCSKG));
  }

  @GetMapping("/v2c/generate")
  Response<Map<String, String>> get_cms(
      @RequestParam String caption,
      @RequestParam int video) {
    return new Response<>(null, ckqaService.get_cms(caption, video));
  }

  @RequestMapping(value = "/v2c/video_upload", method = RequestMethod.POST)
  Response<Boolean> videoUpload(
      @RequestParam("video_file") MultipartFile file,
      @RequestParam String name) {

    String dir = "/mnt/ssd/wyt/ckqa-rpc/HybridNet/utils/video_feats/test/";
    // String dir = "/home/ubuntu/cs-platform/static/tmpvideo";
    Boolean res;
    try {
      byte[] bytes = file.getBytes();

      String pat = "video[0-9]*.mp4";
      System.out.println(name.matches(pat));
      String newFileName = "video30001.mp4";
      if (name.matches(pat)) {
        name = newFileName;
      }
      boolean flag = UploadFile.uploadFile(bytes, dir, name);
      System.out.println(name + "======");
      res = flag;

      return new Response<>(null, res);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e);

      res = false;
      return new Response<>(null, res);
    }

  }

  @GetMapping("/qa/span")
  Response<List<Result>> qaSpan(@RequestParam String q) {
    return new Response<>(null, ckqaService.getSpanResult(q));
  }

  @GetMapping("/qa/mask/word")
  Response<List<Result>> qaMaskWord(@RequestParam String q) {
    return new Response<>(null, ckqaService.getMaskWordResult(q));
  }

  @GetMapping("/qa/textQa")
  Response<List<Result>> getTextQaResult(@RequestParam String query, @RequestParam String text) {
    return new Response<>(null, ckqaService.getTextQaResult(query, text));
  }

  @GetMapping("/entailment")
  Response<List<Double>> getEntailment(@RequestParam String premise, @RequestParam String hypothesises) {
    return new Response<>(null, ckqaService.getEntailment(premise, List.of(hypothesises.split(";"))));
  }

  @PostMapping("/modifyTriple")
  Response<Boolean> modifyTriple(
      @RequestParam String id,
      @RequestParam String subject,
      @RequestParam String relation,
      @RequestParam String object) throws IOException {
    UpdateRequest updateRequest = new UpdateRequest(esIndex, id);
    System.out.printf("Attempting to modify triple: id=%s%n", id);
    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("subject", subject);
    jsonMap.put("relation", relation);
    jsonMap.put("object", object);
    updateRequest.doc(jsonMap);

    highLevelClient.update(updateRequest, RequestOptions.DEFAULT);
    return responseUtil.success();
  }

  @GetMapping("/scale/p1")
  Response<ScaleDimP1> queryScaleP1() throws IOException {
    ScaleDimP1 scaleDimP1 = new ScaleDimP1();

    CountRequest countAllTripleRequest = new CountRequest(esIndex);
    countAllTripleRequest.query(QueryBuilders.matchAllQuery());
    long allTriple = highLevelClient.count(countAllTripleRequest, RequestOptions.DEFAULT).getCount();

    CountRequest countAllCnTripleRequest = new CountRequest(esIndex);
    countAllCnTripleRequest.query(QueryBuilders.matchQuery("lang", "zh"));
    long allTripleZh = highLevelClient.count(countAllCnTripleRequest, RequestOptions.DEFAULT).getCount();

    scaleDimP1.setTripleCount(allTriple);
    scaleDimP1.setTripleCountZh(allTripleZh);

    return new Response<>(null, scaleDimP1);
  }

  @GetMapping("/scale/p2")
  Response<ScaleDimP2> queryScaleP2() {
    Optional<ScaleDimP2> scaleDimP2Optional = scaleDimP2Repo.findById(uniqueScaleId);
    return new Response<>(null, scaleDimP2Optional.orElse(null));
  }

  @PostMapping("/scale/refresh")
  Response<Boolean> refreshScale() {
    ScaleDimP2 scaleDimP2 = scaleDimP2Repo.findById(uniqueScaleId).get();
    if (!scaleDimP2.getIsRefreshing()) {
      scaleDimP2.setIsRefreshing(true);
      scaleDimP2Repo.save(scaleDimP2);
      CompletableFuture.supplyAsync(() -> {
        Scale scale = ckqaService.getScale();
        ScaleDimP2 asyncScaleDimP2 = scaleDimP2Repo.findById(uniqueScaleId).get();
        asyncScaleDimP2.setIsRefreshing(false);
        asyncScaleDimP2.setLastRefreshDate(new Date());
        asyncScaleDimP2.setEntityCount((long) scale.getEntityCount());
        asyncScaleDimP2.setEntityCountZh((long) scale.getEntityCountCn());
        scaleDimP2Repo.save(asyncScaleDimP2);
        return null;
      });
    }
    return responseUtil.success();
  }

  @PostMapping("/scale/refresh/fast")
  Response<Boolean> refreshScaleFast() {
    ScaleDimP2 scaleDimP2 = scaleDimP2Repo.findById(uniqueScaleId).get();
    if (!scaleDimP2.getIsRefreshing()) {
      scaleDimP2.setIsRefreshing(true);
      scaleDimP2Repo.save(scaleDimP2);
      CompletableFuture.supplyAsync(() -> {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(600000)
            .setSocketTimeout(600000)
            .build();
        RequestOptions options = RequestOptions.DEFAULT.toBuilder()
            .setRequestConfig(requestConfig)
            .build();

        Script script = new Script("return [doc['subject'], doc['object']]");
        AggregationBuilder aggregationBuilder = AggregationBuilders.cardinality("cardinality").script(script);

        SearchRequest enSearchRequest = new SearchRequest(esIndex);
        SearchSourceBuilder enSearchSourceBuilder = new SearchSourceBuilder();
        enSearchSourceBuilder.size(0);
        enSearchSourceBuilder.query(QueryBuilders.matchQuery("lang", "en"));
        enSearchSourceBuilder.aggregation(aggregationBuilder);
        enSearchRequest.source(enSearchSourceBuilder);

        SearchRequest zhSearchRequest = new SearchRequest(esIndex);
        SearchSourceBuilder zhSearchSourceBuilder = new SearchSourceBuilder();
        zhSearchSourceBuilder.size(0);
        zhSearchSourceBuilder.query(QueryBuilders.matchQuery("lang", "zh"));
        zhSearchSourceBuilder.aggregation(aggregationBuilder);
        zhSearchRequest.source(zhSearchSourceBuilder);

        ScaleDimP2 asyncScaleDimP2 = scaleDimP2Repo.findById(uniqueScaleId).get();
        asyncScaleDimP2.setIsRefreshing(false);

        try {
          SearchResponse enSearchResponse = highLevelClient.search(enSearchRequest, options);
          SearchResponse zhSearchResponse = highLevelClient.search(zhSearchRequest, options);

          long enCount = ((Cardinality) enSearchResponse.getAggregations().get("cardinality")).getValue();
          long zhCount = ((Cardinality) zhSearchResponse.getAggregations().get("cardinality")).getValue();

          asyncScaleDimP2.setLastRefreshDate(new Date());
          asyncScaleDimP2.setEntityCount(enCount + zhCount);
          asyncScaleDimP2.setEntityCountZh(zhCount);

        } catch (IOException e) {
          e.printStackTrace();
        }

        scaleDimP2Repo.save(asyncScaleDimP2);

        return null;
      });
    }
    return responseUtil.success();
  }

  @PostMapping("/scale/refresh/reset")
  Response<Boolean> resetRefresh() {
    ScaleDimP2 scaleDimP2 = scaleDimP2Repo.findById(uniqueScaleId).get();
    scaleDimP2.setIsRefreshing(false);
    scaleDimP2Repo.save(scaleDimP2);
    return responseUtil.success();
  }

  @GetMapping("/completion")
  Response<List<CompletionResult>> tailPrediction(
      @RequestParam String head,
      @RequestParam String rel,
      @RequestParam Boolean isInv
  ) {
    return new Response<>(null, ckqaService.getCompletion(head, rel, isInv));
  }

  @PostMapping("/populate")
  Response<Boolean> populateKB(
      @RequestParam String subject,
      @RequestParam String relation,
      @RequestParam String object
  ) {
    ckqaService.upsert(null, subject, relation, object);
    return responseUtil.success();
  }
}
