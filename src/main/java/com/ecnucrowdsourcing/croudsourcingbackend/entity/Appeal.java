package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Setter
@Getter
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}appeal")
public class Appeal {

  @Id
  private String id;

  private String ruleDataId;

  private String userId;

  private String content;

  private Date publishDate;
}
