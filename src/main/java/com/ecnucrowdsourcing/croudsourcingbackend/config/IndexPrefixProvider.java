package com.ecnucrowdsourcing.croudsourcingbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("IndexPrefixProvider")
public class IndexPrefixProvider {

  @Value("${spring.profiles.active}")
  public String profile;

  public String indexPrefix() {
    return String.format("%s_%s_", "demo", profile);
  }
}
