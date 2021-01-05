package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

@Getter
@Setter
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}article")
public class Article {

  @Id
  @Field(name = "_id")
  private String id;

  private String title;

  private Date date;
}
