package com.ecnucrowdsourcing.croudsourcingbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Setter
@Getter
public class SecurityUserDetail extends User {

  private String phone;

  private String id;

  public SecurityUserDetail(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }
}
