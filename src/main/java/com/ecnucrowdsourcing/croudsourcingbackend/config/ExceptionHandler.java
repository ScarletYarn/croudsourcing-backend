package com.ecnucrowdsourcing.croudsourcingbackend.config;

import com.ecnucrowdsourcing.croudsourcingbackend.util.Response;
import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@ControllerAdvice
@ResponseBody
public class ExceptionHandler {

  @Resource
  private ResponseUtil responseUtil;

  @org.springframework.web.bind.annotation.ExceptionHandler
  public Response<Boolean> unknownException(Exception e) {
    e.printStackTrace();
    return responseUtil.fail(e.getMessage());
  }
}
