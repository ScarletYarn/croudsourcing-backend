package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}job")
@Data
public class Job {

    @Id
    private String id;

    private Integer seq;

    private String name;

    private Date publishDate;

    private String desc;

    private Integer reward;
}
