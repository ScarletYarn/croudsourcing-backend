package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.OverallQuestionnaire;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.OverallQuestionnaireRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/overallQuestionnaire")
public class OverallQuestionnaireController {

  @Resource
  private OverallQuestionnaireRepo overallQuestionnaireRepo;

  @Resource
  private UserDetailUtil userDetailUtil;

  @Resource
  private ResponseUtil responseUtil;

  @PutMapping("/add")
  Response<Boolean> add(
      @RequestParam Integer q1,
      @RequestParam Integer q2,
      @RequestParam Integer q3s1,
      @RequestParam Integer q3s2,
      @RequestParam Integer q4s1,
      @RequestParam Integer q4s2,
      @RequestParam Integer q5s1,
      @RequestParam Integer q5s2,
      @RequestParam String advice,
      @RequestParam String jobId
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    OverallQuestionnaire questionnaire = new OverallQuestionnaire();
    questionnaire.setJobId(jobId);
    questionnaire.setAdvice(advice);
    questionnaire.setQ1(q1);
    questionnaire.setQ2(q2);
    questionnaire.setQ3s1(q3s1);
    questionnaire.setQ3s2(q3s2);
    questionnaire.setQ4s1(q4s1);
    questionnaire.setQ4s2(q4s2);
    questionnaire.setQ5s1(q5s1);
    questionnaire.setQ5s2(q5s2);
    questionnaire.setUserId(userId);
    overallQuestionnaireRepo.save(questionnaire);
    return responseUtil.success();
  }
}
