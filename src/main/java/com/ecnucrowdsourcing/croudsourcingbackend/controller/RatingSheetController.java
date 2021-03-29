package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.RatingSheet;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.ExpType;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RatingSheetRepo;
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
@RequestMapping("/ratingSheet")
public class RatingSheetController {

  @Resource
  private RatingSheetRepo ratingSheetRepo;

  @Resource
  private UserDetailUtil userDetailUtil;

  @Resource
  private ResponseUtil responseUtil;

  @PutMapping("/add")
  Response<Boolean> add(
      @RequestParam String jobId,
      @RequestParam String prevRuleId,
      @RequestParam String best,
      @RequestParam String worst,
      @RequestParam(required = false) String comment
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    RatingSheet ratingSheet = new RatingSheet();
    ratingSheet.setUserId(userId);
    ratingSheet.setDate(new Date());
    ratingSheet.setJobId(jobId);
    ratingSheet.setPrevRuleId(prevRuleId);
    ratingSheet.setBest(ExpType.valueOf(best).name());
    ratingSheet.setWorst(ExpType.valueOf(worst).name());
    ratingSheet.setComment(comment != null ? comment : "");
    ratingSheetRepo.save(ratingSheet);
    return responseUtil.success();
  }
}
