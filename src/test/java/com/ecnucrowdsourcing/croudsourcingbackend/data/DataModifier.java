package com.ecnucrowdsourcing.croudsourcingbackend.data;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Job;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.MyUser;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.RuleData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Disabled
@SpringBootTest
public class DataModifier extends DataInjector {

  @Test
  void updatePassword() {
    Iterable<MyUser> users = myUserRepo.findAll();
    for (MyUser user : users) {
      user.setPassword(new BCryptPasswordEncoder().encode("123123"));
      myUserRepo.save(user);
    }
  }

  @Test
  void updateJob2() {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    job.get().setName("个性化推荐策略测试");
    jobRepo.save(job.get());
  }

  @Test
  void deleteRuleJob1() {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    ruleDataRepo.deleteAllByJobId(job.get().getId());
    Iterable<RuleData> ruleDataIterable = ruleDataRepo.findAll();
    for (RuleData ruleData : ruleDataIterable) {
      System.out.println(ruleData);
    }
  }

  @Test
  void deleteJob2Records() {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    answerRecordRepo.deleteAllByJobId(job.get().getId());
    expSeqRepo.deleteAll();
    groupCommentRepo.deleteAll();
    rewardRepo.deleteAllByJobId(job.get().getId());
    userActionRepo.deleteAllByJobId(job.get().getId());
    jobStatusRepo.deleteAllByJobId(job.get().getId());
    coinInfoRepo.deleteAll();
    overallQuestionnaireRepo.deleteAll();
  }

  @Test
  void deleteAllByUserId() {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
//    String userId = "K3WjSHsBfotwkNLzBM5b";
    String userId = "-iF1x3kBVvpk1kz1kNEX";
    answerRecordRepo.deleteAllByJobIdAndUserId(job.get().getId(), userId);
    groupCommentRepo.deleteAllByJobIdAndUserId(job.get().getId(), userId);
    rewardRepo.deleteAllByJobIdAndUserId(job.get().getId(), userId);
    userActionRepo.deleteAllByJobIdAndUserId(job.get().getId(), userId);
    jobStatusRepo.deleteAllByUserIdAndJobId(userId, job.get().getId());
    overallQuestionnaireRepo.deleteAllByUserIdAndJobId(userId, job.get().getId());
  }
}
