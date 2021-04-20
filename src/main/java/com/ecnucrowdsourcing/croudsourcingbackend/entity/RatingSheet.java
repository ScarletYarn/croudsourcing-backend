package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Getter
@Setter
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}rating_sheet")
public class RatingSheet {

  @Id
  private String id;

  private String userId;

  private String jobId;

  private String prevRuleId;

  private String bcomment;

  private String wcomment;

  private String best;

  private String worst;

  private Date date;
}
