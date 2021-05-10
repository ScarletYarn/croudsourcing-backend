package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Job;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.JobStatus;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.Reward;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.RewardStatus;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.JobRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.JobStatusRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RewardRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/job")
public class JobController {

  @Resource
  private JobRepo jobRepo;

  @Resource
  private RewardRepo rewardRepo;

  @Resource
  private JobStatusRepo jobStatusRepo;

  @Resource
  private ResponseUtil responseUtil;

  @Resource
  private UserDetailUtil userDetailUtil;

  @ApiOperation("Get a list of all jobs")
  @GetMapping("/all")
  Response<List<Job>> getAll() {
    return new Response<>(null, jobRepo.findAll());
  }

  @ApiOperation("Complete a job")
  @PutMapping("/complete")
  Response<Boolean> complete(@RequestParam String jobId) throws Exception {
    Reward reward = new Reward();
    String userId = userDetailUtil.getUserDetail().getId();
    reward.setJobId(jobId);
    reward.setCompleteTime(new Date());
    reward.setStatus(RewardStatus.UNPAID.name());
    reward.setUserId(userId);
    Optional<Job> jobOptional = jobRepo.findById(jobId);
    if (jobOptional.isPresent()) reward.setValue(jobOptional.get().getReward());
    else throw new Exception("不存在的工作");
    rewardRepo.save(reward);
    return responseUtil.success();
  }

  @GetMapping("/isComplete")
  Response<Boolean> isComplete(@RequestParam String jobId) {
    String userId = userDetailUtil.getUserDetail().getId();
    List<Reward> rewards = rewardRepo.findAllByUserIdAndJobId(userId, jobId);
    return new Response<>(null, !rewards.isEmpty());
  }

  @ApiOperation("Query for details about a job")
  @GetMapping("/q")
  Response<Job> query(@RequestParam String jobId) {
    Optional<Job> jobOptional = jobRepo.findById(jobId);
    return new Response<>(null, jobOptional.orElse(null));
  }

  @PutMapping("/insert")
  Response<Boolean> insert(@RequestParam String jobName) {
    Job job = new Job();
    job.setName(jobName);
    job.setPublishDate(new Date());
    job.setDesc("A dummy job");
    job.setReward(10000);
    jobRepo.save(job);
    return responseUtil.success();
  }

  @GetMapping("/currentIndex")
  Response<Integer> currentIndex(@RequestParam String jobId) {
    String userId = userDetailUtil.getUserDetail().getId();
    Optional<JobStatus> jobStatusOptional = jobStatusRepo.findByJobIdAndUserId(jobId, userId);
    return jobStatusOptional.map(jobStatus -> new Response<>(null, jobStatus.getCurrentIndex())).orElseGet(() -> new Response<>(null, -1));
  }
}
