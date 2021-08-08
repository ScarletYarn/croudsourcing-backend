package com.ecnucrowdsourcing.croudsourcingbackend.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PostInfo {

  private String name;

  private Date completionTime;

  private List<String> favoriteExp;

  private Integer questionAmount;

  private Integer correctAmount;

  private List<Integer> correctList;

  private Integer reward;

  private Integer basic;

  private Integer bonus;

  private List<Integer> characterRating;

  private Integer cognitionStyle;
}
