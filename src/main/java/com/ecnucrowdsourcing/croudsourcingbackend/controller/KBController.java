package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.kb.Triple;
import com.ecnucrowdsourcing.croudsourcingbackend.service.CKQAService;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Result;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kb")
public class KBController {

  @Resource
  private CKQAService ckqaService;

  @Resource(name="elasticsearchClient")
  private RestHighLevelClient highLevelClient;

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
    SearchResponse searchResponse = highLevelClient.search(request, RequestOptions.DEFAULT);
    List<Triple> triples = Arrays.stream(searchResponse.getHits().getHits()).map(searchHit -> {
      Triple triple = new Triple();
      triple.setSubject(String.valueOf(searchHit.getSourceAsMap().get("subject")));
      triple.setRelation(String.valueOf(searchHit.getSourceAsMap().get("relation")));
      triple.setObject(String.valueOf(searchHit.getSourceAsMap().get("object")));
      return triple;
    }).collect(Collectors.toList());
    return new Response<>(null, triples);
  }

  @GetMapping("/extraction")
  Response<List<com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Tuple>> getExtraction(
      @RequestParam String query
  ) {
    return new Response<>(null, ckqaService.getExtraction(query));
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
