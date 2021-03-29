package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.UserAction;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.UserActionRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/userAction")
public class UserActionController {

  @Resource
  private UserActionRepo userActionRepo;

  @Resource
  private UserDetailUtil userDetailUtil;

  @Resource
  private ResponseUtil responseUtil;

  @PutMapping("/add")
  Response<Boolean> add(
      @RequestParam String jobId,
      @RequestParam String ruleDataId,
      @RequestParam(required = false) String aux,
      @RequestParam String clickedId
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    UserAction userAction = new UserAction();
    userAction.setUserId(userId);
    userAction.setDate(new Date());
    userAction.setJobId(jobId);
    userAction.setRuleDataId(ruleDataId);
    userAction.setClickedId(clickedId);
    if (aux != null) userAction.setAux(aux);
    userActionRepo.save(userAction);
    return responseUtil.success();
  }
}
