package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Reward;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.RewardStatus;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RewardRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/reward")
public class RewardController {

  @Resource
  private RewardRepo rewardRepo;

  @Resource
  private ResponseUtil responseUtil;

  @Resource
  private UserDetailUtil userDetailUtil;

  @ApiOperation("Get reward of current user")
  @GetMapping("/me")
  Response<List<Reward>> getAll() {
    String userId = userDetailUtil.getUserDetail().getId();
    return new Response<>(null, rewardRepo.findAllByUserId(userId));
  }

  @PutMapping("/insert")
  Response<Boolean> insert(
      @RequestParam String jobId,
      @RequestParam String userId,
      @RequestParam String status,
      @RequestParam Integer value
  ) {
    Reward reward = new Reward();
    reward.setJobId(jobId);
    reward.setCompleteTime(new Date());
    reward.setStatus(RewardStatus.valueOf(status).name());
    reward.setValue(value);
    reward.setUserId(userId);
    rewardRepo.save(reward);
    return responseUtil.success();
  }
}
