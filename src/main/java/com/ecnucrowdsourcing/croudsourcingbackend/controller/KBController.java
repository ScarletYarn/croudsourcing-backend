package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.TripleCommentType;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.kb.Triple;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.kb.TripleComment;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.TripleCommentRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.service.CKQAService;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Result;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Tuple;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.Optional;
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

  @Resource(name="elasticsearchClient")
  private RestHighLevelClient highLevelClient;

  private List<Triple> searchTriples(SearchRequest request) throws IOException {
    SearchResponse searchResponse = highLevelClient.search(request, RequestOptions.DEFAULT);
    return Arrays.stream(searchResponse.getHits().getHits()).map(searchHit -> {
      Triple triple = new Triple();
      triple.setId(String.valueOf(searchHit.getId()));
      triple.setSubject(String.valueOf(searchHit.getSourceAsMap().get("subject")));
      triple.setRelation(String.valueOf(searchHit.getSourceAsMap().get("relation")));
      triple.setObject(String.valueOf(searchHit.getSourceAsMap().get("object")));
      return triple;
    }).collect(Collectors.toList());
  }

  @GetMapping("/q")
  Response<List<Triple>> search(
      @RequestParam(required = false) String subject,
      @RequestParam(required = false) String object,
      @RequestParam(required = false) String relation
  ) throws IOException {
    SearchRequest request = new SearchRequest("cskg");
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
    return new Response<>(null, searchTriples(request));
  }

  @GetMapping("/extraction")
  Response<List<Tuple>> getExtraction(
      @RequestParam String query
  ) {
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
      @RequestParam(required = false) String data
  ) {
    TripleComment tripleComment = new TripleComment();
    tripleComment.setTripleId(tripleId);
    tripleComment.setType(TripleCommentType.valueOf(type).name());
    tripleComment.setData(data);
    tripleComment.setUpvote(0);
    tripleComment.setDownvote(0);
    tripleCommentRepo.save(tripleComment);
    return responseUtil.success();
  }

  @PostMapping("/triple/comment/up")
  Response<Boolean> upvote(@RequestParam String id) {
    Optional<TripleComment> tripleCommentOptional = tripleCommentRepo.findById(id);
    if (tripleCommentOptional.isPresent()) {
      TripleComment tripleComment = tripleCommentOptional.get();
      tripleComment.setUpvote(tripleComment.getUpvote() + 1);
      tripleCommentRepo.save(tripleComment);
      return responseUtil.success();
    } else {
      return responseUtil.fail("No comment found");
    }
  }

  @PostMapping("/triple/comment/down")
  Response<Boolean> downvote(@RequestParam String id) {
    Optional<TripleComment> tripleCommentOptional = tripleCommentRepo.findById(id);
    if (tripleCommentOptional.isPresent()) {
      TripleComment tripleComment = tripleCommentOptional.get();
      tripleComment.setDownvote(tripleComment.getDownvote() + 1);
      tripleCommentRepo.save(tripleComment);
      return responseUtil.success();
    } else {
      return responseUtil.fail("No comment found");
    }
  }

  @GetMapping("/similar/bm25")
  Response<List<Triple>> getSimilarBm25(@RequestParam String query) throws IOException {
    SearchRequest request = new SearchRequest("cskg_vector");
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(20);
    request.source(searchSourceBuilder);
    searchSourceBuilder.query(QueryBuilders.matchQuery("query", query));

    return new Response<>(null, searchTriples(request));
  }

  @GetMapping("/similar/knn")
  Response<List<String>> getSimilarKNN(@RequestParam List<Double> vector) throws IOException {
    SearchRequest request = new SearchRequest("cskg_vector");
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(20);
    request.source(searchSourceBuilder);
    searchSourceBuilder.query(QueryBuilders.wrapperQuery(String.format(
        "\"script_score\": {\n" +
            "      \"match_all\": {},\n" +
            "      \"script\": {\n" +
            "        \"source\": \"cosineSimilarity(params.queryVector, 'vector') + 1.0\",\n" +
            "        \"params\": {\n" +
            "          \"queryVector\": %s\n" +
            "        }\n" +
            "      }\n" +
            "    }",
        vector.toString()
    )));

    SearchResponse searchResponse = highLevelClient.search(request, RequestOptions.DEFAULT);
    List<String> hitList = Arrays.stream(searchResponse.getHits().getHits()).map(SearchHit::toString).collect(Collectors.toList());
    return new Response<>(null, hitList);
  }

  @GetMapping("/qimg")
  Response<String> searchImg(@RequestParam String qimg) {
    //String rootPath = System.getProperty("user.dir");
    //String imagePath = rootPath.replace('\\', '/') + "/src/main/resources/static/images/kbImage";
    String imagePath = "/home/ubuntu/cs-platform/static/images/kbImage";
    File jpg_file = new File(imagePath + '/' + qimg, "google_0001.jpg");
    File png_file = new File(imagePath + '/' + qimg, "google_0001.png");
    if(jpg_file.exists())return new Response<>(null, "images/kbImage/" + qimg + "/google_0001.jpg");
    if(png_file.exists())return new Response<>(null, "images/kbImage/" + qimg + "/google_0001.png");
    return new Response<>(null, "NA");
  }

  @GetMapping("/qa/mask")
  Response<List<Result>> qaMask(@RequestParam String q) {
    return new Response<>(null, ckqaService.getMaskResult(q));
  }

  @GetMapping("/qa/span")
  Response<List<Result>> qaSpan(@RequestParam String q) {
    return new Response<>(null, ckqaService.getSpanResult(q));
  }

  @GetMapping("/qa/mask/word")
  Response<List<Result>> qaMaskWord(@RequestParam String q) {
    return new Response<>(null, ckqaService.getMaskWordResult(q));
  }
}
