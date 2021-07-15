package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.*;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.constant.ExpType;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.dto.PostInfo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.*;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/result")
public class ResultController {

  @Resource
  private QuestionnaireRepo questionnaireRepo;

  @Resource
  private MyUserRepo myUserRepo;

  @Resource
  private AnswerRecordRepo answerRecordRepo;

  @Resource
  private RuleDataRepo ruleDataRepo;

  @Resource
  private RewardRepo rewardRepo;

  @GetMapping("/info")
  Response<PostInfo> getInfo(@RequestParam String id) {
    PostInfo postInfo = new PostInfo();
    Optional<Questionnaire> questionnaireOptional = questionnaireRepo.findByUserId(id);
    postInfo.setName(questionnaireOptional.get().getName());
    postInfo.setQuestionAmount(20);
    Iterable<MyUser> users = myUserRepo.findAll();
    Map<String, Integer> correctAmountMap = new HashMap<>();
    int noexpStars = 0, insStars = 0, nlStars = 0, kgStars = 0;
    for (MyUser myUser : users) {
      int totalAmount = 0, correctAmount = 0;
      Iterable<AnswerRecord> answerRecords = answerRecordRepo.findAllByUserId(myUser.getId());
      for (AnswerRecord answerRecord : answerRecords) {
        totalAmount++;
        if (myUser.getId().equals(id)) {
          noexpStars += answerRecord.getNoExpScore();
          insStars += answerRecord.getInstanceScore();
          nlStars += answerRecord.getNlScore();
          kgStars += answerRecord.getGraphScore();
        }
        RuleData ruleData = ruleDataRepo.findById(answerRecord.getRuleDataId()).get();
        if (ruleData.getGoldenAnswer().equalsIgnoreCase(answerRecord.getAnswer())) correctAmount++;
      }
      if (totalAmount < 20) continue;
      correctAmountMap.put(myUser.getId(), correctAmount);
    }
    Optional<Reward> rewardOptional = rewardRepo.findByUserIdAndJobId(id, "wyEJZnkBVvpk1kz1ir15");
    postInfo.setReward(rewardOptional.get().getValue());
    postInfo.setCorrectAmount(correctAmountMap.get(id));
    postInfo.setCompletionTime(rewardOptional.get().getCompleteTime());
    postInfo.setCorrectList(new ArrayList<>(){{
      addAll(correctAmountMap.values());
    }});

    List<Integer> ratings = new ArrayList<>(){{
      for (String i : questionnaireOptional.get().getRatings()) add(Integer.parseInt(i));
    }};
    List<Integer> characterRating = new ArrayList<>(){{
      add(ratings.get(0) + 8 - ratings.get(5));
      add(ratings.get(6) + 8 - ratings.get(1));
      add(ratings.get(2) + 8 - ratings.get(7));
      add(ratings.get(8) + 8 - ratings.get(3));
      add(ratings.get(4) + 8 - ratings.get(9));
    }};
    postInfo.setCharacterRating(characterRating);

    int cs = 0;
    for (boolean i : questionnaireOptional.get().getCognitionStyle()) if (i) cs++;
    postInfo.setCognitionStyle(cs);

    int maxStars = Math.max(Math.max(noexpStars, insStars), Math.max(kgStars, nlStars));
    postInfo.setFavoriteExp(new ArrayList<>());
    if (noexpStars == maxStars) postInfo.getFavoriteExp().add(ExpType.NO.name());
    if (insStars == maxStars) postInfo.getFavoriteExp().add(ExpType.INS.name());
    if (kgStars == maxStars) postInfo.getFavoriteExp().add(ExpType.KG.name());
    if (nlStars == maxStars) postInfo.getFavoriteExp().add(ExpType.NL.name());

    return new Response<>(null, postInfo);
  }
}
