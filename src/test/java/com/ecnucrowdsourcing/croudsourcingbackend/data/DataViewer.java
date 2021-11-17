package com.ecnucrowdsourcing.croudsourcingbackend.data;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@Disabled
@SpringBootTest
public class DataViewer extends DataInjector {

  @Test
  public void viewUsers() {
    List<MyUser> users = myUserRepo.findAll();
    for (MyUser user : users) {
      System.out.println(user);
    }
  }

  @Test
  public void viewExpSeq() {
    int cnt = 0;
    Iterable<ExpSeq> seqIterable = expSeqRepo.findAll();
    for (ExpSeq seq : seqIterable) {
      if (cnt++ > 30) break;
      System.out.println(seq);
    }
  }

  @Test
  public void viewRules() {
    Iterable<RuleData> ruleDataList = ruleDataRepo.findAll();
    for (RuleData ruleData : ruleDataList) {
      System.out.println(ruleData);
    }
  }

  @Test
  public void viewJobs() {
    Iterable<Job> jobIterable = jobRepo.findAll();
    for (Job job : jobIterable) {
      System.out.println(job);
    }
  }

  @Test
  public void viewAnswer() {
    Set<String> userSet = new HashSet<>();
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    Iterable<AnswerRecord> answerRecords = answerRecordRepo.findAllByJobId(job.get().getId());
    for (AnswerRecord answerRecord : answerRecords) {
      Optional<MyUser> userOptional = myUserRepo.findById(answerRecord.getUserId());
      assert userOptional.isPresent();
      userSet.add(userOptional.get().getUsername());
    }
    System.out.println(userSet);
  }

  @Test
  public void viewUserAnswer() {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    String userId = "YSHm0XkBVvpk1kz1kP_b";

    Iterable<AnswerRecord> answerRecords = answerRecordRepo.findAllByJobIdAndUserId(job.get().getId(), userId);
    for (AnswerRecord answerRecord : answerRecords) {
      Optional<RuleData> ruleDataOptional = ruleDataRepo.findById(answerRecord.getRuleDataId());
      assert ruleDataOptional.isPresent();
      System.out.println(ruleDataOptional.get().getSeq());
//      System.out.println(answerRecord);
    }
  }
}
