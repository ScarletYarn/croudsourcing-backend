package com.ecnucrowdsourcing.croudsourcingbackend.data;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.*;
import com.ecnucrowdsourcing.croudsourcingbackend.util.TestUtility;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONArray;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.*;

@SpringBootTest
public class DataDump extends DataInjector {

  @Resource
  private RestHighLevelClient elasticsearchClient;

  @Resource
  private TestUtility testUtility;

  @Test
  void dumpAll() throws Exception {
    testUtility.dumpData(AnswerRecord.class, answerRecordRepo, null);
    testUtility.dumpData(Appeal.class, appealRepo, null);
    testUtility.dumpData(Job.class, jobRepo, null);
    testUtility.dumpData(MyUser.class, myUserRepo, null);
    testUtility.dumpData(Questionnaire.class, questionnaireRepo, null);
    testUtility.dumpData(RatingSheet.class, ratingSheetRepo, null);
    testUtility.dumpData(Reward.class, rewardRepo, null);
    testUtility.dumpData(RuleData.class, ruleDataRepo, null);
    testUtility.dumpData(UserAction.class, userActionRepo, null);
    testUtility.dumpData(JobStatus.class, jobStatusRepo, null);
  }

  @Test
  void dumpQ() throws Exception {
    testUtility.dumpData(Reward.class, rewardRepo, null);
  }

  @Test
  void dumpAction() throws IOException {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();

    Field[] declaredFields = UserAction.class.getDeclaredFields();
    String[] fields = new String[declaredFields.length];
    for (int i = 0; i < fields.length; i++) fields[i] = declaredFields[i].getName();

    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "UserAction.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    System.out.println(String.join(TestUtility.SEP, fields));
    bw.write(String.join(TestUtility.SEP, fields) + "\n");

    SearchRequest searchRequest = new SearchRequest("demo_prod_user_action");
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.matchQuery("jobId", job.get().getId()));
    searchSourceBuilder.size(3000);
    searchRequest.source(searchSourceBuilder);
    searchRequest.scroll(TimeValue.timeValueMinutes(1L));
    SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
    String scrollId = response.getScrollId();
    SearchHit[] hits = response.getHits().getHits();

    for (SearchHit hit : hits) {
      Map<String, Object> source = hit.getSourceAsMap();
      List<String> values = new ArrayList<>();
      for (String field : fields) {
        values.add(String.valueOf(source.get(field)));
      }
      bw.write(String.join(TestUtility.SEP, values) + "\n");
    }

    while (hits != null && hits.length > 0) {
      SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
      scrollRequest.scroll(TimeValue.timeValueMinutes(1L));
      response = elasticsearchClient.scroll(scrollRequest, RequestOptions.DEFAULT);
      scrollId = response.getScrollId();
      hits = response.getHits().getHits();
      for (SearchHit hit : hits) {
        Map<String, Object> source = hit.getSourceAsMap();
        List<String> values = new ArrayList<>();
        for (String field : fields) {
          values.add(String.valueOf(source.get(field)));
        }
        bw.write(String.join(TestUtility.SEP, values) + "\n");
      }
    }
    bw.close();

    ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
    clearScrollRequest.addScrollId(scrollId);
    ClearScrollResponse clearScrollResponse = elasticsearchClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
    assert (clearScrollResponse.isSucceeded());
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
    int totalCnt = 0;
    for (MyUser myUser : users) {
      totalCnt += 1;
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
      values.add(Double.toString((ratings.get(0) + 8 - ratings.get(5)) / 2.));
      values.add(Double.toString((ratings.get(6) + 8 - ratings.get(1)) / 2.));
      values.add(Double.toString((ratings.get(2) + 8 - ratings.get(7)) / 2.));
      values.add(Double.toString((ratings.get(8) + 8 - ratings.get(3)) / 2.));
      values.add(Double.toString((ratings.get(4) + 8 - ratings.get(9)) / 2.));
      values.add(Integer.toString(cs));
      bw.write(String.join(TestUtility.SEP, values) + "\n");
//      System.out.printf("%s %s\n", q.getName(), myUser.getUsername());
    }
    bw.close();
    System.out.println(totalCnt);
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
      if (cnt >= 20) {
        completeCnt += 1;
        System.out.println(myUser.getUsername());
      }
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
    String[] fields = { "用户id", "题号", "界面类型", "界面评分", "界面停留时间", "打分时间" };
    bw.write(String.join(TestUtility.SEP, fields) + "\n");
    int userSeq = 0;
    for (MyUser myUser : users) {
      int cnt = 0;
      Iterable<AnswerRecord> answerRecords = answerRecordRepo.findAllByUserId(myUser.getId());
      for (AnswerRecord answerRecord : answerRecords) cnt += 1;
      if (cnt < 20) continue;
      userSeq++;
      Iterable<UserAction> actions = userActionRepo.findAllByUserIdOrderByDateAsc(myUser.getId());
      String[] types = { "ins", "nl", "noexp", "kg" };
      Map<String, Long> timeMap = new HashMap<>(){{
        for (String i : types) put(i, 0L);
      }};
      Map<String, Integer> scoreMap = new HashMap<>();
      int seq = 1;
      Date last = new Date();
      String lastType = "noexp";
      Map<String, Date> ratingTime = new HashMap<>();
      for (UserAction action : actions) {
        if (action.getClickedId().equals("start")) last = action.getDate();
        if (action.getClickedId().equals("next-rule")) {
          long delta = action.getDate().getTime() - last.getTime();
          timeMap.put(lastType, timeMap.get(lastType) + delta);
          last = action.getDate();

//          if (scoreMap.size() < 4) {
//            Optional<AnswerRecord> answerRecord = answerRecordRepo.
//                findByJobIdAndRuleDataIdAndUserId(action.getJobId(), action.getRuleDataId(), myUser.getId());
//            System.out.printf("Query %d\n", answerRecord.get().getNoExpScore());
//            System.out.printf("User %d\n", userSeq);
//            System.out.printf("Q %d\n", seq);
//            for (String s : scoreMap.keySet()) System.out.println(s);
//            for (UserAction innerAction : actions) {
//              Optional<RuleData> ruleDataOptional = ruleDataRepo.findById(innerAction.getRuleDataId());
//              System.out.printf("Action %s in seq %d\n", innerAction.getClickedId(), ruleDataOptional.get().getSeq());
//            }
//          }

          Optional<AnswerRecord> answerRecord = answerRecordRepo.
              findByJobIdAndRuleDataIdAndUserId(action.getJobId(), action.getRuleDataId(), myUser.getId());
          assert (answerRecord.isPresent());
          scoreMap.put(types[0], answerRecord.get().getInstanceScore());
          scoreMap.put(types[1], answerRecord.get().getNlScore());
          scoreMap.put(types[2], answerRecord.get().getNoExpScore());
          scoreMap.put(types[3], answerRecord.get().getGraphScore());

          for (String type : types) {
            List<String> values = new ArrayList<>();
            values.add(myUser.getId());
            values.add(Integer.toString(seq));
            values.add(type);
            values.add(Integer.toString(scoreMap.get(type)));
            values.add(Long.toString(timeMap.get(type)));
            if (ratingTime.get(type) == null) {
              values.add("Time missing!");
            } else {
              values.add(ratingTime.get(type).toString());
            }
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
          String type = action.getClickedId().split("-")[1];
          ratingTime.put(type, action.getDate());
        }
      }
    }
    bw.close();
  }

  private Integer parseRewardValue(String value) {
    return (int) (Double.parseDouble(value) * 100);
  }

  @Test
  void updateReward() throws Exception {
    /*
      Five users didnt click the finish button:
        7CHsx3kBVvpk1kz1HNgG
        8yKt33kBVvpk1kz1ISYV
        HCGvy3kBVvpk1kz1Zeu3
        IiK22XkBVvpk1kz1VRu7
        eCHnx3kBVvpk1kz1hNgO
     */
    BufferedReader reader = testUtility.readCSV("/reward.csv");
    reader.readLine();
    String str;
    while ((str = reader.readLine()) != null) {
      List<String> items = Arrays.asList(str.split(","));
      Optional<Reward> reward = rewardRepo.findByUserIdAndJobId(items.get(0), "wyEJZnkBVvpk1kz1ir15");
//      assert reward.isPresent();
      if (reward.isEmpty()) {
        Reward r = new Reward();
        r.setJobId("wyEJZnkBVvpk1kz1ir15");
        r.setStatus("UNPAID");
        List<RatingSheet> ratingSheetList = ratingSheetRepo.findAllByJobIdAndUserIdOrderByDateDesc(
            "wyEJZnkBVvpk1kz1ir15", items.get(0)
        );
        r.setCompleteTime(ratingSheetList.get(0).getDate());
        r.setUserId(items.get(0));
        r.setBasic(parseRewardValue(items.get(1)));
        r.setBonus(parseRewardValue(items.get(2)));
        r.setValue(parseRewardValue(items.get(3)));
        rewardRepo.save(r);
        continue;
      }
      reward.get().setBasic(parseRewardValue(items.get(1)));
      reward.get().setBonus(parseRewardValue(items.get(2)));
      reward.get().setValue(parseRewardValue(items.get(3)));
      rewardRepo.save(reward.get());
    }
  }

  @Test
  void dumpAnswersJob2() throws Exception {
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "任务完成情况.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    Iterable<MyUser> users = myUserRepo.findAll();
    String[] fields = { "用户id", "规则id", "完成正误", "完成时间" };
    bw.write(String.join(TestUtility.SEP, fields) + "\n");
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    for (MyUser user : users) {
      List<AnswerRecord> records = answerRecordRepo.findAllByJobIdAndUserId(
          job.get().getId(), user.getId()
      );
      if (records.size() < 20) continue;
      System.out.println(user);
      for (AnswerRecord answerRecord : records) {
        List<String> values = new ArrayList<>();
        values.add(user.getId());
        values.add(answerRecord.getRuleDataId());
        Optional<RuleData> ruleData = ruleDataRepo.findById(answerRecord.getRuleDataId());
        assert ruleData.isPresent();
        if (ruleData.get().getGoldenAnswer().equalsIgnoreCase(answerRecord.getAnswer())) values.add("正确");
        else values.add("错误");
        values.add(answerRecord.getDate().toString());
        bw.write(String.join(TestUtility.SEP, values) + "\n");
      }
    }
  }

  @Test
  void dumpUserAction2() throws Exception {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    testUtility.dumpData(UserAction.class, userActionRepo, new TestUtility.Filter<UserAction>(){
      boolean filter(UserAction action) {
        return action.getJobId().equals(job.get().getId());
      }
    });
  }
}
