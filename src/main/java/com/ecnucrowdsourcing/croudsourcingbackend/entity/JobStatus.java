package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}job_status")
@Setter
@Getter
public class JobStatus {

  @Id
  private String id;

  private String jobId;

  private String userId;

  private Integer currentIndex;
}
