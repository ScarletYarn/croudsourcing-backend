package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}appeal")
public class Appeal {

  @Id
  private String id;

  private String ruleDataId;

  private String userId;

  private String content;

  private Date publishDate;
}
