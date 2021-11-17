package com.ecnucrowdsourcing.croudsourcingbackend.entity.constant;

public enum Answer {
  TRUE("true"), FALSE("false");

  private final String desc;

  private Answer(String s) {
    desc = s;
  }

  public String getDesc() {
    return desc;
  }
}
