package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}questionnaire")
@Getter
@Setter
public class Questionnaire {

  @Id
  private String id;

  private String userId;

  private String name;

  private String gender;

  private String age;

  private String education;

  private String kg;

  private String logic;

  private List<String> ratings;

  private List<Boolean> cognitionStyle;

  private List<String> field;
}
