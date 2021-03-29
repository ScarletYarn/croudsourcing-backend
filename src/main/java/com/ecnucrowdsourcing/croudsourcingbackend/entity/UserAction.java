package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Getter
@Setter
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}user_action")
public class UserAction {

  @Id
  private String id;

  private String userId;

  private Date date;

  private String clickedId;

  private String jobId;

  private String ruleDataId;

  private String aux;
}
