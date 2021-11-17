package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.ExpSeq;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.RuleData;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.ExpType;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.ExpSeqRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RuleDataRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ruleData")
public class RuleDataController {

  @Resource
  private RuleDataRepo ruleDataRepo;

  @Resource
  private ExpSeqRepo expSeqRepo;

  @Resource
  private UserDetailUtil userDetailUtil;

  @GetMapping("/byJobId")
  @ApiOperation("Get the list of rule data by job id")
  Response<List<String>> getByJob(@RequestParam String jobId) {
    return new Response<>(null, new ArrayList<>(){{
      ruleDataRepo.findAllByJobIdOrderBySeqAsc(jobId).forEach(e -> add(e.getId()));
    }});
  }

  @GetMapping("/q")
  @ApiOperation("Query for a specific rule")
  Response<RuleData> q(@RequestParam String id) {
    return new Response<>(null, ruleDataRepo.findById(id).orElse(null));
  }

  @GetMapping("/pageSeq")
  @ApiOperation("Sequence of the explanation")
  Response<List<String>> getPageSeq(@RequestParam String id) {
    String userId = userDetailUtil.getUserDetail().getId();
    Optional<ExpSeq> expSeqOptional = expSeqRepo.findByUserIdAndRuleId(userId, id);
    if (expSeqOptional.isPresent()) {
      return new Response<>(null, expSeqOptional.get().getSeq());
    } else {
      List<String> expList = new ArrayList<>(){{
        add(ExpType.INS.name());
        add(ExpType.NL.name());
        add(ExpType.KG.name());
      }};
      Collections.shuffle(expList);
      return new Response<>(null, expList);
    }
  }
}
