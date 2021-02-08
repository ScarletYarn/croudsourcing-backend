package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.config.SecurityConfiguration;
import com.ecnucrowdsourcing.croudsourcingbackend.config.SecurityUserDetail;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.MyUser;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.MyUserRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  @Resource
  private MyUserRepo myUserRepo;

  @Resource
  private ResponseUtil responseUtil;

  @ApiOperation("Used for signup")
  @PutMapping("/signup")
  Response<Boolean> signup(@RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String username,
                           @RequestParam String alipay) {
    List<MyUser> users = myUserRepo.findAllByPhone(phone);
    if (!users.isEmpty()) return responseUtil.fail("手机号已注册");
    try {
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
    } catch (Exception e) {
      e.printStackTrace();
      return responseUtil.fail("登陆失败");
    }
  }

  @ApiOperation("Empty list before login, [\"ROLE_USER\"] after login")
  @GetMapping("/roles")
  Response<List<String>> roles() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return new Response<>("未登陆", new ArrayList<>());
    } else {
      return new Response<>(null, new ArrayList<>(){{
        authentication.getAuthorities().forEach(e -> add(e.toString()));
      }});
    }
  }

  @ApiOperation("Get information about the current user")
  @GetMapping("/me")
  Response<MyUser> me() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return new Response<>("未查询到信息", null);
    } else {
      try {
        SecurityUserDetail securityUserDetail = (SecurityUserDetail) authentication.getPrincipal();
        MyUser myUser = myUserRepo.findAllByPhone(securityUserDetail.getPhone()).get(0);
        myUser.setPassword(null);
        return new Response<>(null, myUser);
      } catch (Exception e) {
        e.printStackTrace();
        return new Response<>("未查询到信息", null);
      }
    }
  }

  @ApiOperation("Update user information")
  @PostMapping("/update")
  Response<Boolean> update(
          @RequestParam(required = false) String aliPay,
          @RequestParam(required = false) String username
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return responseUtil.fail("登陆信息错误");
    } else {
      try {
        SecurityUserDetail securityUserDetail = (SecurityUserDetail) authentication.getPrincipal();
        MyUser myUser = myUserRepo.findAllByPhone(securityUserDetail.getPhone()).get(0);
        if (aliPay != null) myUser.setAlipay(aliPay);
        if (username != null) myUser.setUsername(username);
        myUserRepo.save(myUser);
        return responseUtil.success();
      } catch (Exception e) {
        e.printStackTrace();
        return responseUtil.fail("更新失败");
      }
    }
  }
}
