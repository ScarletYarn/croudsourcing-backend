package com.ecnucrowdsourcing.croudsourcingbackend.entity.kb;

import lombok.Data;

@Data
public class Triple {
  private String id;

  private String subject;

  private String relation;

  private String object;

  private Double score;
}
