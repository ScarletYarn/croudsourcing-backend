package com.ecnucrowdsourcing.croudsourcingbackend.data;

import com.ecnucrowdsourcing.croudsourcingbackend.repository.*;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.*;
import com.ecnucrowdsourcing.croudsourcingbackend.util.TestUtility;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DataDump {

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

  @Resource
  private TestUtility testUtility;

  @Test
  void dumpAll() throws Exception {
    testUtility.dumpData(AnswerRecord.class, answerRecordRepo);
    testUtility.dumpData(Appeal.class, appealRepo);
    testUtility.dumpData(Job.class, jobRepo);
    testUtility.dumpData(MyUser.class, myUserRepo);
    testUtility.dumpData(Questionnaire.class, questionnaireRepo);
    testUtility.dumpData(RatingSheet.class, ratingSheetRepo);
    testUtility.dumpData(Reward.class, rewardRepo);
    testUtility.dumpData(RuleData.class, ruleDataRepo);
    testUtility.dumpData(UserAction.class, userActionRepo);
    testUtility.dumpData(JobStatus.class, jobStatusRepo);
  }

  @Test
  void dumpUserAction() throws Exception {
    Field[] declaredFields = UserAction.class.getDeclaredFields();
    String[] fields = new String[declaredFields.length];
    for (int i = 0; i < fields.length; i++) fields[i] = declaredFields[i].getName();
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "UserActionWithUser.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    String[] aux = { "", "username", "questionSeq" };
    bw.write(String.join(",", fields) + String.join(",", aux) + "\n");
    Iterable<UserAction> actions = userActionRepo.findAll(Sort.by(Sort.Direction.ASC, "date"));
    for (UserAction userAction : actions) {
      List<String> values = new ArrayList<>();
      for (Field field : declaredFields) {
        field.setAccessible(true);
        values.add(String.valueOf(field.get(userAction)));
      }
      String username = myUserRepo.findById(userAction.getUserId()).get().getUsername();
      String seq = String.valueOf(ruleDataRepo.findById(userAction.getRuleDataId()).get().getSeq());
      List<String> auxValues = List.of("", username, seq);
      bw.write(String.join(",", values) + String.join(",", auxValues) + "\n");
    }
    bw.close();
  }
}
