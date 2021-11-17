package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}exp_seq")
public class ExpSeq {

  @Id
  private String id;

  private List<String> seq;

  private String ruleId;

  private String userId;
}
