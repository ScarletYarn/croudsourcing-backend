package com.ecnucrowdsourcing.croudsourcingbackend.data;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.*;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.ExpType;
import com.ecnucrowdsourcing.croudsourcingbackend.util.TestUtility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.util.*;

@Disabled
@SpringBootTest
public class DataLoader extends DataInjector {

  @Resource
  private TestUtility testUtility;

  @Test
  void loadUser() throws Exception {
    JSONArray jsonArray = testUtility.readJSON("/user.json");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      MyUser myUser = new MyUser();
      myUser.setUsername(jsonObject.getString("username"));
      myUser.setPhone(jsonObject.getString("phone"));
      myUser.setAlipay(jsonObject.getString("alipay"));
      myUser.setPassword(new BCryptPasswordEncoder().encode(jsonObject.getString("password")));
      myUser.setRoles(Arrays.asList(jsonObject.getString("roles").split(",")));
      myUser.setSignupDate(new Date(jsonObject.getLong("signupDate")));
      myUserRepo.save(myUser);
    }
  }

  @Test
  void loadJob1() {
    Job job = new Job();
    job.setName("规则正误判断1");
    job.setDesc("判断规则的正误");
    job.setPublishDate(new Date(1616999962352L));
    job.setSeq(1);
    job.setReward(1000);
    jobRepo.save(job);
  }

  @Test
  void loadJob2() {
    Job job = new Job();
    job.setName("规则正误判断2");
    job.setDesc("判断规则的正误");
    job.setPublishDate(new Date());
    job.setSeq(2);
    job.setReward(1000);
    jobRepo.save(job);
  }

  @Test
  void loadRuleData1() throws Exception {
    JSONArray jsonArray = testUtility.readJSON("/rule_data.json");
    Optional<Job> jobOptional = jobRepo.findBySeq(1);
    assert jobOptional.isPresent();
    String jobId = jobOptional.get().getId();
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      RuleData ruleData = new RuleData();
      ruleData.setContent(jsonObject.getString("content"));
      ruleData.setGoldenAnswer(jsonObject.getString("goldenAnswer"));
      ruleData.setNl(jsonObject.getString("nl"));
      ruleData.setInstance(jsonObject.getString("instance"));
      ruleData.setGraph(jsonObject.getString("graph"));
      ruleData.setJobId(jobId);
      ruleData.setSeq(jsonObject.getInt("seq"));
      ruleData.setDifficulty(jsonObject.getInt("difficulty"));
      ruleDataRepo.save(ruleData);
    }
  }

  @Test
  void loadRuleData2() throws Exception {
    Optional<Job> jobOptional = jobRepo.findBySeq(2);
    assert jobOptional.isPresent();
    BufferedReader reader = testUtility.readCSV("/rule_data_2.csv");
    String line;
    reader.readLine();
    while ((line = reader.readLine()) != null) {
      List<String> items = Arrays.asList(line.split("\t"));
      RuleData ruleData = new RuleData();
      ruleData.setContent(items.get(1));
      ruleData.setGraph(items.get(2));
      ruleData.setNl(items.get(3));
      ruleData.setInstance(items.get(4));
      ruleData.setGoldenAnswer(items.get(5));
      ruleData.setDifficulty(Integer.parseInt(items.get(6)));
      ruleData.setSeq(Integer.parseInt(items.get(7)));
      ruleData.setJobId(jobOptional.get().getId());
      ruleDataRepo.save(ruleData);
    }
  }

  @Test
  void loadExpSeq() throws Exception {
    BufferedReader reader = testUtility.readCSV("/exp_seq.csv");
    Optional<Job> jobOptional = jobRepo.findBySeq(2);
    assert jobOptional.isPresent();
    String line;
    reader.readLine();
    while ((line = reader.readLine()) != null) {
      List<String> items = Arrays.asList(line.split(","));
      ExpSeq expSeq = new ExpSeq();
      expSeq.setUserId(items.get(0));
      Optional<RuleData> ruleData = ruleDataRepo.findByJobIdAndSeq(
          jobOptional.get().getId(), Integer.parseInt(items.get(1))
      );
      assert ruleData.isPresent();
      expSeq.setRuleId(ruleData.get().getId());
      List<String> typeList = new ArrayList<>(){{
        add(null);
        add(null);
        add(null);
      }};
      typeList.set(3 - Integer.parseInt(items.get(2)), ExpType.INS.name());
      typeList.set(3 - Integer.parseInt(items.get(3)), ExpType.NL.name());
      typeList.set(3 - Integer.parseInt(items.get(4)), ExpType.KG.name());
      assert !typeList.contains(null);
      expSeq.setSeq(typeList);
      expSeqRepo.save(expSeq);
    }
  }
}
