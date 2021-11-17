package com.ecnucrowdsourcing.croudsourcingbackend.config;

import com.ecnucrowdsourcing.croudsourcingbackend.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * When an authorized user attempts to access a resource which the user has no permission, return 403 forbidden
 */
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {

  @Resource
  private ResponseUtil responseUtil;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");
    PrintWriter writer = response.getWriter();
    writer.write(new ObjectMapper().writeValueAsString(responseUtil.fail("Access denied! ")));
    writer.flush();
    writer.close();
  }
}
