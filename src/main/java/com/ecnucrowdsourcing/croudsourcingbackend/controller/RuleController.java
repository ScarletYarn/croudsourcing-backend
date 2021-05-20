package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Rule;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.RuleRepo;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rule")
public class RuleController {

  @Resource
  private RuleRepo ruleRepo;

  @GetMapping("/get")
  List<Rule> get() {
    List<Rule> rules = new ArrayList<>();
    ruleRepo.findAll(Sort.by(Sort.Direction.ASC, "seq")).forEach(rules::add);
    return rules;
  }
}
