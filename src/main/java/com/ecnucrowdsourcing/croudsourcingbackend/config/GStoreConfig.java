package com.ecnucrowdsourcing.croudsourcingbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GStoreConfig {
  @Bean
  public GstoreConnector gstoreConnector() {
    return new GstoreConnector("192.168.10.174", 9000, "root", "123456");
  }
}
