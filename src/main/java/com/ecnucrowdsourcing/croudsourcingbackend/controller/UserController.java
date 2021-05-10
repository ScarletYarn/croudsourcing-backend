package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.config.SecurityConfiguration;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.MyUser;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.Questionnaire;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.MyUserRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.QuestionnaireRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

  @Resource
  private MyUserRepo myUserRepo;

  @Resource
  private ResponseUtil responseUtil;

  @Resource
  private UserDetailUtil userDetailUtil;

  @Resource
  private QuestionnaireRepo questionnaireRepo;

  @ApiOperation("Used for signup")
  @PutMapping("/signup")
  Response<Boolean> signup(@RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String username,
                           @RequestParam String alipay) {
    List<MyUser> users = myUserRepo.findAllByPhone(phone);
    if (!users.isEmpty()) return responseUtil.fail("手机号已注册");
    MyUser myUser = new MyUser();
    myUser.setPhone(phone);
    myUser.setPassword(new BCryptPasswordEncoder().encode(password));
    myUser.setSignupDate(new Date());
    myUser.setUsername(username);
    myUser.setAlipay(alipay);
    myUser.setRoles(new ArrayList<>(){{
      add("ROLE_" + SecurityConfiguration.ROLE_USER);
    }});
    myUserRepo.save(myUser);
    return responseUtil.success();
  }

  @ApiOperation("Empty list before login, [\"ROLE_USER\"] after login")
  @GetMapping("/roles")
  Response<List<String>> roles() {
    return new Response<>(null, new ArrayList<>(){{
      userDetailUtil.getUserDetail().getAuthorities().forEach(e -> add(e.toString()));
    }});
  }

  @ApiOperation("Get information about the current user")
  @GetMapping("/me")
  Response<MyUser> me() {
    String userId = userDetailUtil.getUserDetail().getId();
    MyUser myUser = myUserRepo.findById(userId).orElse(null);
    if (myUser != null) myUser.setPassword(null);
    return new Response<>(null, myUser);
  }

  @ApiOperation("Update user information")
  @PostMapping("/update")
  Response<Boolean> update(
          @RequestParam(required = false) String aliPay,
          @RequestParam(required = false) String username
  ) throws Exception {
    String userId = userDetailUtil.getUserDetail().getId();
    MyUser myUser = myUserRepo.findById(userId).orElse(null);
    if (myUser == null) throw new Exception("用户不存在");
    if (aliPay != null) myUser.setAlipay(aliPay);
    if (username != null) myUser.setUsername(username);
    myUserRepo.save(myUser);
    return responseUtil.success();
  }

  @ApiOperation("Get the questionnaire for the current user")
  @GetMapping("/questionnaire/q")
  Response<Questionnaire> getQuestionnaire() {
    String userId = userDetailUtil.getUserDetail().getId();
    return new Response<>(null, questionnaireRepo.findByUserId(userId).orElse(null));
  }

  @ApiOperation("Upload the questionnaire of the current user")
  @PutMapping("/questionnaire/p")
  Response<Boolean> putQuestionnaire(
      @RequestParam String name,
      @RequestParam String age,
      @RequestParam String gender,
      @RequestParam String kg,
      @RequestParam String logic,
      @RequestParam String education,
      @RequestParam List<String> rating,
      @RequestParam List<Boolean> cognitionStyle,
      @RequestParam String field
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    Optional<Questionnaire> questionnaireOptional = questionnaireRepo.findByUserId(userId);
    if (questionnaireOptional.isPresent()) return responseUtil.success();
    Questionnaire q = new Questionnaire();
    q.setUserId(userId);
    q.setName(name);
    q.setAge(age);
    q.setGender(gender);
    q.setKg(kg);
    q.setLogic(logic);
    q.setEducation(education);
    q.setRatings(rating);
    q.setCognitionStyle(cognitionStyle);
    q.setField(field);
    questionnaireRepo.save(q);
    return responseUtil.success();
  }
}
