package com.ecnucrowdsourcing.croudsourcingbackend.data;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
public class DataClean extends DataInjector {

  @Test
  void clearAnswerRecord() {
    answerRecordRepo.deleteAll();
  }

  @Test
  void clearAppeal() {
    appealRepo.deleteAll();
  }

  @Test
  void clearJob() {
    jobRepo.deleteAll();
  }

  @Test
  void clearMyUser() {
    myUserRepo.deleteAll();
  }

  @Test
  void clearQuestionnaire() {
    questionnaireRepo.deleteAll();
  }

  @Test
  void clearRatingSheet() {
    ratingSheetRepo.deleteAll();
  }

  @Test
  void clearReward() {
    rewardRepo.deleteAll();
  }

  @Test
  void clearRuleData() {
    ruleDataRepo.deleteAll();
  }

  @Test
  void clearUserAction() {
    userActionRepo.deleteAll();
  }

  @Test
  void clearJobStatus() {
    jobStatusRepo.deleteAll();
  }

  @Test
  void clearTestEntity() {
    testEntityRepo.deleteAll();
  }

  @Test
  void clearExpSeq() {
    expSeqRepo.deleteAll();
  }
}
