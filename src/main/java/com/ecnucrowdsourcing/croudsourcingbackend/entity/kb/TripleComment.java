package com.ecnucrowdsourcing.croudsourcingbackend.entity.kb;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}triple_comment")
public class TripleComment {
  @Id
  private String id;

  private String tripleId;

  private String type;

  private String data;

  private Integer upvote;

  private Integer downvote;
}
