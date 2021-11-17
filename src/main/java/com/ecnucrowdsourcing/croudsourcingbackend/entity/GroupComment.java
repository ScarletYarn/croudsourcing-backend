package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Setter
@Getter
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}group_comment")
public class GroupComment {

  @Id
  private String id;

  private String userId;

  private String jobId;

  private String prevRuleId;

  private String whyChange;

  private Integer q1;

  private Integer q2;
}
