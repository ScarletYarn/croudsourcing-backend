package com.ecnucrowdsourcing.croudsourcingbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Document(indexName = "#{@IndexPrefixProvider.indexPrefix()}my_user")
@Data
public class MyUser implements UserDetails {

  @Id
  private String id;

  private String username;

  private String password;

  private List<String> roles;

  private String alipay;

  private String phone;

  private Date signupDate;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return new ArrayList<SimpleGrantedAuthority>() {{
      for (String role : roles) add(new SimpleGrantedAuthority(role));
    }};
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
