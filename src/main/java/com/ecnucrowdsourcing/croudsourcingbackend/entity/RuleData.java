package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}rule_data")
public class RuleData {

  @Id
  private String id;

  private String jobId;

  private Integer seq;

  private String content;

  private String graph;

  private String nl;

  private String instance;

  private String goldenAnswer;

  private Integer difficulty;
}
