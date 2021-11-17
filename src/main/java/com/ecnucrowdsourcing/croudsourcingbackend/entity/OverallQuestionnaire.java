package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}overall_questionnaire")
public class OverallQuestionnaire {

  @Id
  private String id;

  private String jobId;

  private String userId;

  private Integer q1;

  private Integer q2;

  private Integer q3s1;

  private Integer q3s2;

  private Integer q4s1;

  private Integer q4s2;

  private Integer q5s1;

  private Integer q5s2;

  private String advice;
}
