package com.ecnucrowdsourcing.croudsourcingbackend.data;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.*;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.*;
import com.ecnucrowdsourcing.croudsourcingbackend.util.TestUtility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;

@SpringBootTest
public class DataLoader {

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
  void loadJob() throws Exception {
    JSONArray jsonArray = testUtility.readJSON("/job.json");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      Job job = new Job();
      job.setName(jsonObject.getString("name"));
      job.setReward(jsonObject.getInt("reward"));
      job.setDesc(jsonObject.getString("desc"));
      job.setSeq(jsonObject.getInt("seq"));
      job.setPublishDate(new Date(jsonObject.getLong("publishDate")));
      jobRepo.save(job);
    }
  }

  @Test
  void loadRuleData() throws Exception {
    JSONArray jsonArray = testUtility.readJSON("/rule_data.json");
    String jobId = jobRepo.findAll().get(0).getId();
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
}
