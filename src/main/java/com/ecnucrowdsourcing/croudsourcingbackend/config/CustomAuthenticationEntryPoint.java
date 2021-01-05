package com.ecnucrowdsourcing.croudsourcingbackend.config;

import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * When an unauthorized user attempts to access a protected resource, return 401 unauthorized
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Resource
  private ResponseUtil responseUtil;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    PrintWriter writer = response.getWriter();
    writer.write(new ObjectMapper().writeValueAsString(responseUtil.fail("Unauthorized user! ")));
    writer.flush();
    writer.close();
  }
}
