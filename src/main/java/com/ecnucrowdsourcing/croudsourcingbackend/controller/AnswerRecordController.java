package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.AnswerRecord;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.RuleData;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.Answer;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.dto.AnswerResult;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.AnswerRecordRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RuleDataRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/answerRecord")
public class AnswerRecordController {

  @Resource
  private ResponseUtil responseUtil;

  @Resource
  private AnswerRecordRepo answerRecordRepo;

  @Resource
  private RuleDataRepo ruleDataRepo;

  @Resource
  private UserDetailUtil userDetailUtil;

  @ApiOperation("Answer a question")
  @PutMapping("/answer")
  Response<Boolean> answer(
      @RequestParam String jobId,
      @RequestParam String ruleDataId,
      @RequestParam String answer,
      @RequestParam Integer noExpScore,
      @RequestParam Integer graphScore,
      @RequestParam Integer nlScore,
      @RequestParam Integer instanceScore
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    AnswerRecord answerRecord = new AnswerRecord();
    answerRecord.setUserId(userId);
    answerRecord.setJobId(jobId);
    answerRecord.setRuleDataId(ruleDataId);
    answerRecord.setAnswer(Answer.valueOf(answer).name());
    answerRecord.setNoExpScore(noExpScore);
    answerRecord.setDate(new Date());
    answerRecord.setGraphScore(graphScore);
    answerRecord.setNlScore(nlScore);
    answerRecord.setInstanceScore(instanceScore);
    answerRecordRepo.save(answerRecord);
    return responseUtil.success();
  }

  @ApiOperation("Get the result for a job")
  @GetMapping("/result")
  Response<List<AnswerResult>> result(@RequestParam String jobId) {
    String userId = userDetailUtil.getUserDetail().getId();
    List<RuleData> ruleDataList = ruleDataRepo.findAllByJobIdOrderBySeqAsc(jobId);
    List<AnswerRecord> answerRecordList = answerRecordRepo.findAllByJobIdAndUserId(jobId, userId);
    List<AnswerResult> answerResultList = new ArrayList<>();
    for (RuleData ruleData : ruleDataList) {
      for (AnswerRecord answerRecord : answerRecordList) {
        if (answerRecord.getRuleDataId().equals(ruleData.getId())) {
          answerResultList.add(new AnswerResult(){{
            setRuleDataId(ruleData.getId());
            setAnswer(answerRecord.getAnswer());
            setGoldenAnswer(ruleData.getGoldenAnswer());
          }});
        }
      }
    }
    return new Response<>(null, answerResultList);
  }
}
