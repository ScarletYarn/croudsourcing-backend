package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}rule_data")
public class RuleData {

  @Id
  private String id;

  private String jobId;

  private String content;

  private String graph;

  private String nl;

  private String instance;

  private String goldenAnswer;
}
