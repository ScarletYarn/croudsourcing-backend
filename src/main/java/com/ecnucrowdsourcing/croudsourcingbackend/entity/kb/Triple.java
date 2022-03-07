package com.ecnucrowdsourcing.croudsourcingbackend.entity.kb;

import lombok.Data;

@Data
public class Triple {
  private String subject;

  private String relation;

  private String object;
}
