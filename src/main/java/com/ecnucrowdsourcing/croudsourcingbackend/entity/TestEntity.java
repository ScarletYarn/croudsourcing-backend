package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}test_entity")
public class TestEntity {

  @Id
  private String id;

  private String content;

  private Integer seq;
}
