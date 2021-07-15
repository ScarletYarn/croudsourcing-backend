package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.config.GstoreConnector;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/kb")
public class KBController {

  @Resource
  private GstoreConnector gstoreConnector;

  @GetMapping("/q")
  Response<String> search(@RequestParam String q) {
    return new Response<String>(null, gstoreConnector.query("cskg", "json", q));
  }
}
