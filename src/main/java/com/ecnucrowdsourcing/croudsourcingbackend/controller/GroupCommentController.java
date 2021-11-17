package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.GroupComment;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.GroupCommentRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RuleDataRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/groupComment")
public class GroupCommentController {

  @Resource
  private GroupCommentRepo groupCommentRepo;

  @Resource
  private RuleDataRepo ruleDataRepo;

  @Resource
  private UserDetailUtil userDetailUtil;

  @Resource
  private ResponseUtil responseUtil;

  @PutMapping("/add")
  @ApiOperation("Add group comment")
  Response<Boolean> add(
      @RequestParam String jobId,
      @RequestParam String prevRuleId,
      @RequestParam Integer q1,
      @RequestParam Integer q2,
      @RequestParam String whyChange
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    GroupComment groupComment = new GroupComment();
    groupComment.setJobId(jobId);
    groupComment.setPrevRuleId(prevRuleId);
    groupComment.setWhyChange(whyChange);
    groupComment.setQ1(q1);
    groupComment.setQ2(q2);
    groupComment.setUserId(userId);
    groupCommentRepo.save(groupComment);
    return responseUtil.success();
  }

  @GetMapping("/isComplete")
  Response<Boolean> isComplete(
      @RequestParam String jobId,
      @RequestParam Integer prevIndex
  ) {
    String userId = userDetailUtil.getUserDetail().getId();
    String prevRuleId = ruleDataRepo.findByJobIdAndSeq(jobId, prevIndex).get().getId();
    Optional<GroupComment> groupCommentOptional = groupCommentRepo.findByJobIdAndUserIdAndPrevRuleId(
        jobId, userId, prevRuleId
    );
    if (groupCommentOptional.isPresent()) return responseUtil.success();
    else return responseUtil.fail(null);
  }
}
