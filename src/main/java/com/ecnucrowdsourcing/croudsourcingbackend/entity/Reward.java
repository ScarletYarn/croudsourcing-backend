package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}reward")
@Setter
@Getter
public class Reward {

    @Id
    private String id;

    private String jobId;

    private Date completeTime;

    private Integer value;

    private String status;

    private String userId;
}
