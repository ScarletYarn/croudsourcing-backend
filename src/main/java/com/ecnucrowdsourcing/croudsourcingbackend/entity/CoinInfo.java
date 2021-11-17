package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}coin_info")
public class CoinInfo {

  @Id
  private String id;

  private String userId;

  private Integer currentIndex;

  private Integer count;
}
