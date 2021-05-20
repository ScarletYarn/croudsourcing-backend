package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}rule")
@Setter
@Getter
public class Rule {

  @Id
  private String id;

  private Integer seq;

  private String body;
}
