package com.ecnucrowdsourcing.croudsourcingbackend.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerResult {

  private String ruleDataId;

  private String answer;

  private String goldenAnswer;
}
