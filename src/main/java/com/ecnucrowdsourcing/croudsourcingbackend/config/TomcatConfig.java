package com.ecnucrowdsourcing.croudsourcingbackend.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TomcatConfig {

  @Bean
  public ErrorPageRegistrar errorPageRegistrar() {
    return new MyErrorPageRegistrar();
  }

  private static class MyErrorPageRegistrar implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
      registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/index.html"));
    }
  }
}
