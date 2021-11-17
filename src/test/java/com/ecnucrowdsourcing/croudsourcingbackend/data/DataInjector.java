package com.ecnucrowdsourcing.croudsourcingbackend.data;

import com.ecnucrowdsourcing.croudsourcingbackend.repository.*;

import javax.annotation.Resource;

public abstract class DataInjector {

  @Resource
  protected AnswerRecordRepo answerRecordRepo;

  @Resource
  protected AppealRepo appealRepo;

  @Resource
  protected JobRepo jobRepo;

  @Resource
  protected MyUserRepo myUserRepo;

  @Resource
  protected QuestionnaireRepo questionnaireRepo;

  @Resource
  protected RatingSheetRepo ratingSheetRepo;

  @Resource
  protected RewardRepo rewardRepo;

  @Resource
  protected RuleDataRepo ruleDataRepo;

  @Resource
  protected UserActionRepo userActionRepo;

  @Resource
  protected JobStatusRepo jobStatusRepo;

  @Resource
  protected ExpSeqRepo expSeqRepo;

  @Resource
  protected GroupCommentRepo groupCommentRepo;

  @Resource
  protected TestEntityRepo testEntityRepo;

  @Resource
  protected OverallQuestionnaireRepo overallQuestionnaireRepo;

  @Resource
  protected CoinInfoRepo coinInfoRepo;
}
