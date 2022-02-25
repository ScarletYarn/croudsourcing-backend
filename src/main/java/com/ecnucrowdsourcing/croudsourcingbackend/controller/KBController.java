package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.config.GstoreConnector;
import com.ecnucrowdsourcing.croudsourcingbackend.service.CKQAService;
import com.ecnucrowdsourcing.croudsourcingbackend.service.thrift.Result;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.io.File;

@RestController
@RequestMapping("/kb")
public class KBController {

  @Resource
  private GstoreConnector gstoreConnector;

  @Resource
  private CKQAService ckqaService;

  @GetMapping("/q")
  Response<String> search(@RequestParam String q) {
    System.out.println(q);
    return new Response<>(null, gstoreConnector.query("cskg", "json", q));
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
