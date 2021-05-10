package com.ecnucrowdsourcing.croudsourcingbackend.data;

import com.ecnucrowdsourcing.croudsourcingbackend.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class DataClean {

  @Resource
  private AnswerRecordRepo answerRecordRepo;

  @Resource
  private AppealRepo appealRepo;

  @Resource
  private JobRepo jobRepo;

  @Resource
  private MyUserRepo myUserRepo;

  @Resource
  private QuestionnaireRepo questionnaireRepo;

  @Resource
  private RatingSheetRepo ratingSheetRepo;

  @Resource
  private RewardRepo rewardRepo;

  @Resource
  private RuleDataRepo ruleDataRepo;

  @Resource
  private UserActionRepo userActionRepo;

  @Resource
  private JobStatusRepo jobStatusRepo;

  @Test
  void clearAnswerRecord() throws Exception {
    answerRecordRepo.deleteAll();
  }

  @Test
  void clearAppeal() throws Exception {
    appealRepo.deleteAll();
  }

  @Test
  void clearJob() throws Exception {
    jobRepo.deleteAll();
  }

  @Test
  void clearMyUser() throws Exception {
    myUserRepo.deleteAll();
  }

  @Test
  void clearQuestionnaire() throws Exception {
    questionnaireRepo.deleteAll();
  }

  @Test
  void clearRatingSheet() throws Exception {
    ratingSheetRepo.deleteAll();
  }

  @Test
  void clearReward() throws Exception {
    rewardRepo.deleteAll();
  }

  @Test
  void clearRuleData() throws Exception {
    ruleDataRepo.deleteAll();
  }

  @Test
  void clearUserAction() throws Exception {
    userActionRepo.deleteAll();
  }

  @Test
  void clearJobStatus() throws Exception {
    jobStatusRepo.deleteAll();
  }
}
