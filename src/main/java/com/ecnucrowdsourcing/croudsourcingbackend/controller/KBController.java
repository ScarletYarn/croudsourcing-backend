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

@RestController
@RequestMapping("/kb")
public class KBController {

  @Resource
  private GstoreConnector gstoreConnector;

  @Resource
  private CKQAService ckqaService;

  @GetMapping("/q")
  Response<String> search(@RequestParam String q) {
    return new Response<>(null, gstoreConnector.query("cskg", "json", q));
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
