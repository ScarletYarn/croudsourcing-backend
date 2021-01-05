package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DemoController {

  @GetMapping("/greet")
  String greet() {
    return "Hello";
  }

  @GetMapping("/any")
  String any() {
    return "Anyone!";
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/user")
  String user() {
    return "user";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin")
  String admin() {
    return "admin";
  }

  @GetMapping("/roles")
  List<String> roles() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return new ArrayList<>();
    } else {
      return new ArrayList<>(){{
        authentication.getAuthorities().forEach(e -> add(e.toString()));
      }};
    }
  }
}
