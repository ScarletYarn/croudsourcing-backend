package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.RuleData;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RuleDataRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ruleData")
public class RuleDataController {

  @Resource
  private RuleDataRepo ruleDataRepo;

  @GetMapping("/byJobId")
  @ApiOperation("Get the list of rule data by job id")
  Response<List<String>> getByJob(@RequestParam String jobId) {
    return new Response<>(null, new ArrayList<>(){{
      ruleDataRepo.findAllByJobId(jobId).forEach(e -> add(e.getId()));
    }});
  }

  @GetMapping("/q")
  @ApiOperation("Query for a specific rule")
  Response<RuleData> q(@RequestParam String id) {
    return new Response<>(null, ruleDataRepo.findById(id).orElse(null));
  }
}
