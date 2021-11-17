package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.RatingSheet;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.ExpType;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RatingSheetRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RuleDataRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/ratingSheet")
public class RatingSheetController {

  @Resource
  private RatingSheetRepo ratingSheetRepo;

  @Resource
  private RuleDataRepo ruleDataRepo;

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
      @RequestParam(required = false) String bcomment,
      @RequestParam(required = false) String wcomment
  ) throws Exception {
    String userId = userDetailUtil.getUserDetail().getId();
    RatingSheet ratingSheet = new RatingSheet();
    ratingSheet.setUserId(userId);
    ratingSheet.setDate(new Date());
    ratingSheet.setJobId(jobId);
    ratingSheet.setPrevRuleId(prevRuleId);
    ratingSheet.setBest(ExpType.valueOf(best).name());
    ratingSheet.setWorst(ExpType.valueOf(worst).name());
    ratingSheet.setBcomment(bcomment);
    ratingSheet.setWcomment(wcomment);
    ratingSheetRepo.save(ratingSheet);
    return responseUtil.success();
  }

  @GetMapping("/isComplete")
  Response<Boolean> isComplete(
      @RequestParam String jobId,
      @RequestParam Integer prevIndex
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    String prevRuleId = ruleDataRepo.findByJobIdAndSeq(jobId, prevIndex).get().getId();
    Optional<RatingSheet> ratingSheetOptional = ratingSheetRepo.findByJobIdAndUserIdAndPrevRuleId(
        jobId, userId, prevRuleId
    );
    if (ratingSheetOptional.isPresent()) return responseUtil.success();
    else return responseUtil.fail(null);
  }
}
