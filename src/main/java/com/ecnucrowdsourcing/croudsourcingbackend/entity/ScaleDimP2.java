package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}scale_dim_p2")
@Getter
@Setter
public class ScaleDimP2 {

  @Id
  private String id;

  private Long entityCount;

  private Long entityCountZh;

  private Boolean isRefreshing;

  private Date lastRefreshDate;
}
