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
import java.util.*;

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
    bw.write(String.join(TestUtility.SEP, fields) + String.join(TestUtility.SEP, aux) + "\n");
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
      bw.write(String.join(TestUtility.SEP, values) + String.join(TestUtility.SEP, auxValues) + "\n");
    }
    bw.close();
  }

  @Test
  void dumpRuleDetail() throws Exception {
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "规则.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    Iterable<RuleData> ruleDataIterable = ruleDataRepo.findAll();
    String[] fields = { "规则id", "规则难度", "规则具体内容" };
    bw.write(String.join(TestUtility.SEP, fields) + "\n");
    for (RuleData ruleData : ruleDataIterable) {
      List<String> values = new ArrayList<>();
      values.add(ruleData.getId());
      values.add(ruleData.getDifficulty().toString());
      values.add(ruleData.getContent());
      bw.write(String.join(TestUtility.SEP, values) + "\n");
    }
    bw.close();
  }

  @Test
  void dumpUserDetail() throws Exception {
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "基本信息.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    Iterable<MyUser> users = myUserRepo.findAll();
    String[] fields = {
        "id",
        "姓名",
        "性别",
        "年龄",
        "学历",
        "联系方式",
        "知识领域",
        "Extraversion",
        "Agreeableness",
        "Conscientiousness",
        "Emotional Stability",
        "Openness to Experiences",
        "认知风格"
    };
    bw.write(String.join(TestUtility.SEP, fields) + "\n");
    for (MyUser myUser : users) {

      int cnt = 0;
      Iterable<AnswerRecord> answerRecords = answerRecordRepo.findAllByUserId(myUser.getId());
      for (AnswerRecord answerRecord : answerRecords) cnt += 1;
      if (cnt < 20) continue;

      Optional<Questionnaire> qOptional = questionnaireRepo.findByUserId(myUser.getId());
      if (qOptional.isEmpty()) continue;
      Questionnaire q = qOptional.get();
      int cs = 0;
      for (boolean i : q.getCognitionStyle()) if (i) cs += 1;
      List<String> values = new ArrayList<>();
      values.add(myUser.getId());
      values.add(q.getName());
      values.add(q.getGender());
      values.add(q.getAge());
      values.add(q.getEducation());
      values.add(myUser.getPhone());
      values.add(q.getField().toString());
      List<Integer> ratings = new ArrayList<>(){{
        for (String i : q.getRatings()) add(Integer.parseInt(i));
      }};
      values.add(Double.toString((ratings.get(0) + 7 - ratings.get(5)) / 2.));
      values.add(Double.toString((ratings.get(6) + 7 - ratings.get(1)) / 2.));
      values.add(Double.toString((ratings.get(2) + 7 - ratings.get(7)) / 2.));
      values.add(Double.toString((ratings.get(8) + 7 - ratings.get(3)) / 2.));
      values.add(Double.toString((ratings.get(4) + 7 - ratings.get(9)) / 2.));
      values.add(Integer.toString(cs));
      bw.write(String.join(TestUtility.SEP, values) + "\n");
//      System.out.printf("%s %s\n", q.getName(), myUser.getUsername());
    }
    bw.close();
  }

  @Test
  void dumpAnswerDetail() throws Exception {
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "任务完成情况.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    Iterable<MyUser> users = myUserRepo.findAll();
    String[] fields = { "用户id", "规则id", "完成正误", "完成时间" };
    bw.write(String.join(TestUtility.SEP, fields) + "\n");
    int completeCnt = 0;
    for (MyUser myUser : users) {
      int cnt = 0;
      Iterable<AnswerRecord> answerRecords = answerRecordRepo.findAllByUserId(myUser.getId());
      for (AnswerRecord answerRecord : answerRecords) {
        cnt += 1;
        List<String> values = new ArrayList<>();
        values.add(myUser.getId());
        values.add(answerRecord.getRuleDataId());
        RuleData ruleData = ruleDataRepo.findById(answerRecord.getRuleDataId()).get();
        if (ruleData.getGoldenAnswer().equalsIgnoreCase(answerRecord.getAnswer())) values.add("正确");
        else values.add("错误");
        values.add(answerRecord.getDate().toString());
        bw.write(String.join(TestUtility.SEP, values) + "\n");
      }
      if (cnt >= 20) completeCnt += 1;
      if (cnt > 20) System.out.println("PANIC!\n");
    }
    System.out.println(completeCnt);
    bw.close();
  }

  @Test
  void dumpPageInfo() throws Exception {
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "界面评分.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    Iterable<MyUser> users = myUserRepo.findAll();
    String[] fields = { "用户id", "题号", "界面类型", "界面评分", "界面停留时间" };
    bw.write(String.join(TestUtility.SEP, fields) + "\n");
    for (MyUser myUser : users) {
      Iterable<UserAction> actions = userActionRepo.findAllByUserIdOrderByDateAsc(myUser.getId());
      String[] types = { "ins", "nl", "noexp", "kg" };
      Map<String, Long> timeMap = new HashMap<>(){{
        for (String i : types) put(i, 0L);
      }};
      Map<String, Integer> scoreMap = new HashMap<>();
      int seq = 1;
      Date last = new Date();
      String lastType = "noexp";
      for (UserAction action : actions) {
        if (action.getClickedId().equals("start")) last = action.getDate();
        if (action.getClickedId().equals("next-rule")) {
          long delta = action.getDate().getTime() - last.getTime();
          timeMap.put(lastType, timeMap.get(lastType) + delta);
          last = action.getDate();
          for (String type : types) {
            List<String> values = new ArrayList<>();
            values.add(myUser.getId());
            values.add(Integer.toString(seq));
            values.add(type);
            values.add(Integer.toString(scoreMap.get(type)));
            values.add(Long.toString(timeMap.get(type)));
            bw.write(String.join(TestUtility.SEP, values) + "\n");
          }
          for (String i : types) timeMap.put(i, 0L);
          seq += 1;
          lastType = "noexp";
        }
        if (action.getClickedId().startsWith("carousel-switch")) {
          String[] _types = action.getClickedId().split("-");
          String type = _types[_types.length - 1];
          long delta = action.getDate().getTime() - last.getTime();
          last = action.getDate();
          timeMap.put(lastType, timeMap.get(lastType) + delta);
          lastType = type;
        }
        if (action.getClickedId().startsWith("rating")) {
          String[] _types = action.getClickedId().split("-");
          int score = Integer.parseInt(_types[_types.length - 1]);
          String type = _types[_types.length - 2];
          scoreMap.put(type, score);
        }
      }
    }
    bw.close();
  }

  @Test
  void updateField() throws Exception {
    for (Questionnaire q : questionnaireRepo.findAll()) {
//      q.setField(String.format("[%s]", q.getField()));
      questionnaireRepo.save(q);
    }
  }

  @Test
  void updatePayment() {
    for (Job j : jobRepo.findAll()) {
      j.setReward(1000);
      jobRepo.save(j);
    }
  }
}
