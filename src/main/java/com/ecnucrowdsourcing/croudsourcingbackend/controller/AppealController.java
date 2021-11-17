package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Appeal;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.AppealRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/appeal")
public class AppealController {

  @Resource
  private ResponseUtil responseUtil;

  @Resource
  private UserDetailUtil userDetailUtil;

  @Resource
  private AppealRepo appealRepo;

  @ApiOperation("Appeal a certain rule data")
  @PutMapping("/add")
  Response<Boolean> appeal(
      @RequestParam String ruleDataId,
      @RequestParam String content
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    Appeal appeal = new Appeal();
    appeal.setContent(content);
    appeal.setPublishDate(new Date());
    appeal.setRuleDataId(ruleDataId);
    appeal.setUserId(userId);
    appealRepo.save(appeal);
    return responseUtil.success();
  }
}
