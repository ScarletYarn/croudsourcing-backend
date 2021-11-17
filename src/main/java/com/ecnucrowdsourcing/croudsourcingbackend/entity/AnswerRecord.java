package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}answer_record")
public class AnswerRecord {

  @Id
  private String id;

  private String userId;

  private String jobId;

  private String ruleDataId;

  private String answer;

  private Date date;

  private Integer noExpScore;

  private Integer graphScore;

  private Integer nlScore;

  private Integer instanceScore;
}
