package com.ecnucrowdsourcing.croudsourcingbackend.util;

import com.ecnucrowdsourcing.croudsourcingbackend.config.SecurityUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserDetailUtil {

  public SecurityUserDetail getUserDetail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (SecurityUserDetail) authentication.getPrincipal();
  }
}
