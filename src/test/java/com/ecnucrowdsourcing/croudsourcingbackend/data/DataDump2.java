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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Disabled
@SpringBootTest
public class DataDump2 extends DataInjector {

  @Resource
  private RestHighLevelClient elasticsearchClient;

  @Resource
  private TestUtility testUtility;

  private Set<String> getCompletedUsers() {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();

    List<MyUser> userList = myUserRepo.findAll();
    Set<String> resList = new HashSet<>();
    for (MyUser user : userList) {
      List<AnswerRecord> answerRecordList = answerRecordRepo.findAllByJobIdAndUserId(
          job.get().getId(), user.getId()
      );
      if (answerRecordList.size() == 20) resList.add(user.getId());
    }
    resList.add("MSEix3kBVvpk1kz1vM30");
    resList.add("YSHm0XkBVvpk1kz1kP_b");
    return resList;
  }

  private Map<String, Integer> ruleId2Seq() {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();

    List<RuleData> ruleDataList = ruleDataRepo.findAllByJobIdOrderBySeqAsc(job.get().getId());
    Map<String, Integer> id2seq = new HashMap<>();
    for (RuleData r : ruleDataList) id2seq.put(r.getId(), r.getSeq());
    return id2seq;
  }

  @Test
  void dumpAction() throws IOException {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    Set<String> completedUsers = getCompletedUsers();
    Map<String, Integer> id2seq = ruleId2Seq();

    File switchingFile = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "切换记录.csv")));
    File operatingFile = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "操作记录.csv")));
    BufferedWriter switchingWriter = new BufferedWriter(new FileWriter(switchingFile));
    BufferedWriter operatingWriter = new BufferedWriter(new FileWriter(operatingFile));
    String[] fields = { "题号", "用户id", "记录内容", "时间" };
    switchingWriter.write(String.join(TestUtility.SEP, fields) + "\n");
    operatingWriter.write(String.join(TestUtility.SEP, fields) + "\n");

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
      if (!completedUsers.contains(source.get("userId"))) continue;
      List<String> values = new ArrayList<>();
      values.add(String.valueOf(id2seq.get(source.get("ruleDataId"))));
      values.add(String.valueOf(source.get("userId")));
      values.add(String.valueOf(source.get("clickedId")));
      values.add(String.valueOf(source.get("date")));
      operatingWriter.write(String.join(TestUtility.SEP, values) + "\n");
      if (source.get("clickedId").toString().startsWith("carousel")) {
        switchingWriter.write(String.join(TestUtility.SEP, values) + "\n");
      }
    }

    while (hits != null && hits.length > 0) {
      SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
      scrollRequest.scroll(TimeValue.timeValueMinutes(1L));
      response = elasticsearchClient.scroll(scrollRequest, RequestOptions.DEFAULT);
      scrollId = response.getScrollId();
      hits = response.getHits().getHits();
      for (SearchHit hit : hits) {
        Map<String, Object> source = hit.getSourceAsMap();
        if (!completedUsers.contains(source.get("userId"))) continue;
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(id2seq.get(source.get("ruleDataId"))));
        values.add(String.valueOf(source.get("userId")));
        values.add(String.valueOf(source.get("clickedId")));
        values.add(String.valueOf(source.get("date")));
        operatingWriter.write(String.join(TestUtility.SEP, values) + "\n");
        if (source.get("clickedId").toString().startsWith("carousel")) {
          switchingWriter.write(String.join(TestUtility.SEP, values) + "\n");
        }
      }
    }
    switchingWriter.close();
    operatingWriter.close();

    ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
    clearScrollRequest.addScrollId(scrollId);
    ClearScrollResponse clearScrollResponse = elasticsearchClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
    assert (clearScrollResponse.isSucceeded());
  }

  @Test
  void dumpAnswersJob2() throws Exception {
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "任务完成情况.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    Iterable<MyUser> users = myUserRepo.findAll();
    String[] fields = { "用户id", "规则id", "完成正误", "完成时间", "花费时间" };
    bw.write(String.join(TestUtility.SEP, fields) + "\n");
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    Set<String> completedUsers = getCompletedUsers();
    for (MyUser user : users) {
      if (!completedUsers.contains(user.getId())) continue;
      List<AnswerRecord> records = answerRecordRepo.findAllByJobIdAndUserId(
          job.get().getId(), user.getId()
      );
      System.out.println(user);
      for (AnswerRecord answerRecord : records) {
        List<UserAction> userActionList = userActionRepo
            .findAllByJobIdAndUserIdAndRuleDataIdOrderByDateAsc(
            job.get().getId(), user.getId(), answerRecord.getRuleDataId()
        );
        long time = userActionList.get(userActionList.size() - 1).getDate().getTime()
                - userActionList.get(0).getDate().getTime();
        List<String> values = new ArrayList<>();
        values.add(user.getId());
        values.add(answerRecord.getRuleDataId());
        Optional<RuleData> ruleData = ruleDataRepo.findById(answerRecord.getRuleDataId());
        assert ruleData.isPresent();
        if (ruleData.get().getGoldenAnswer().equalsIgnoreCase(answerRecord.getAnswer())) values.add("正确");
        else values.add("错误");
        values.add(answerRecord.getDate().toString());
        values.add(Long.toString(time));
        bw.write(String.join(TestUtility.SEP, values) + "\n");
      }
    }
    bw.close();
  }

  @Test
  void dumpGroupComment() throws Exception {
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();
    testUtility.dumpData(GroupComment.class, groupCommentRepo, new TestUtility.Filter<GroupComment>(){
      boolean filter(GroupComment groupComment) {
        return groupComment.getJobId().equals(job.get().getId());
      }
    });
  }

  @Test
  void dumpOverall() throws Exception {
    testUtility.dumpData(OverallQuestionnaire.class, overallQuestionnaireRepo, null);
  }

  @Test
  void dumpCoinInfo() throws Exception {
    testUtility.dumpData(CoinInfo.class, coinInfoRepo, null);
  }

  @Test
  void dumpAppeal() throws Exception {
    File file = new File(String.valueOf(Paths.get(TestUtility.DUMP_DIR, "申诉情况.csv")));
    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    String[] fields = { "用户id", "规则id", "申诉内容", "时间" };
    bw.write(String.join(TestUtility.SEP, fields) + "\n");
    Optional<Job> job = jobRepo.findBySeq(2);
    assert job.isPresent();

    Iterable<Appeal> appeals = appealRepo.findAll();
    for (Appeal appeal : appeals) {
      Optional<RuleData> ruleDataOptional = ruleDataRepo.findById(appeal.getRuleDataId());
      if (ruleDataOptional.isEmpty()) {
        System.out.println(appeal);
        continue;
      }
      if (!ruleDataOptional.get().getJobId().equals(job.get().getId())) continue;
      List<String> values = new ArrayList<>();
      values.add(appeal.getUserId());
      values.add(appeal.getRuleDataId());
      values.add(appeal.getContent());
      values.add(appeal.getPublishDate().toString());
      bw.write(String.join(TestUtility.SEP, values) + "\n");
    }
    bw.close();
  }
}
