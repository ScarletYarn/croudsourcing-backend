package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.CoinInfo;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.CoinInfoRepo;
import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.ecnucrowdsourcing.croudsourcingbackend.util.UserDetailUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/coinInfo")
public class CoinInfoController {

  @Resource
  private CoinInfoRepo coinInfoRepo;

  @Resource
  private UserDetailUtil userDetailUtil;

  @Resource
  private ResponseUtil responseUtil;

  private CoinInfo getCoinInfo(String userId) {
    Optional<CoinInfo> coinInfoOptional = coinInfoRepo.findByUserId(userId);
    CoinInfo coinInfo;
    if (coinInfoOptional.isPresent()) coinInfo = coinInfoOptional.get();
    else {
      coinInfo = new CoinInfo();
      coinInfo.setCount(40);
      coinInfo.setUserId(userId);
      coinInfo.setCurrentIndex(0);
    }
    return coinInfo;
  }

  @GetMapping("/get")
  Response<CoinInfo> get() {
    String userId = userDetailUtil.getUserDetail().getId();
    return new Response<>(null, getCoinInfo(userId));
  }

  @PostMapping("/switch")
  Response<Boolean> pageSwitch(Integer index) {
    String userId = userDetailUtil.getUserDetail().getId();
    CoinInfo coinInfo = getCoinInfo(userId);
    coinInfo.setCurrentIndex(index);
    int prevCount = coinInfo.getCount();
    coinInfo.setCount(prevCount - 1);
    coinInfoRepo.save(coinInfo);
    return responseUtil.success();
  }

  @PostMapping("/next")
  Response<Boolean> ruleNext() {
    String userId = userDetailUtil.getUserDetail().getId();
    CoinInfo coinInfo = getCoinInfo(userId);
    coinInfo.setCurrentIndex(0);
    coinInfoRepo.save(coinInfo);
    return responseUtil.success();
  }
}
