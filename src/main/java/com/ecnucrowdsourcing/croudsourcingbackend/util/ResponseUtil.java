package com.ecnucrowdsourcing.croudsourcingbackend.util;

import org.springframework.stereotype.Component;

@Component
public class ResponseUtil {

  public Response<Boolean> success() {
    return new Response<>(null, true);
  }

  public Response<Boolean> fail(String msg) {
    return new Response<>(msg, false);
  }
}
