package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Job;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.JobRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {

  @Resource
  private JobRepo jobRepo;

  @Resource
  private ResponseUtil responseUtil;

  @ApiOperation("Get a list of all jobs")
  @GetMapping("/all")
  Response<List<Job>> getAll() {
    return new Response<>(null, jobRepo.findAll());
  }

  @PutMapping("/insert")
  Response<Boolean> insert(@RequestParam String jobName) {
    try {
      Job job = new Job();
      job.setName(jobName);
      job.setPublishDate(new Date());
      job.setDesc("A dummy job");
      job.setReward(10000);
      jobRepo.save(job);
      return responseUtil.success();
    } catch (Exception e) {
      e.printStackTrace();
      return responseUtil.fail("插入失败");
    }
  }
}
