package com.ecnucrowdsourcing.croudsourcingbackend.service;

import com.ecnucrowdsourcing.croudsourcingbackend.config.SecurityUserDetail;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.MyUser;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.MyUserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SecurityUserDetailService implements UserDetailsService {

  @Resource
  private MyUserRepo myUserRepo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    List<MyUser> users = myUserRepo.findAllByPhone(username);
    if (users.isEmpty()) throw new UsernameNotFoundException("User not found! ");
    MyUser myUser = users.get(0);
    if (myUser == null) {
      throw new UsernameNotFoundException("User not found! ");
    }
    Collection<GrantedAuthority> authorities = new ArrayList<>() {{
      for (String role : myUser.getRoles()) add(new SimpleGrantedAuthority(role));
    }};
    SecurityUserDetail securityUserDetail = new SecurityUserDetail(myUser.getUsername(), myUser.getPassword(), authorities);
    securityUserDetail.setPhone(myUser.getPhone());
    return securityUserDetail;
  }
}
