package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.config.SecurityConfiguration;
import com.ecnucrowdsourcing.croudsourcingbackend.entity.MyUser;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.MyUserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  @Resource
  private MyUserRepo myUserRepo;

  @GetMapping("/findById")
  MyUser findById(@RequestParam String id) {
    return myUserRepo.findById(id).orElse(null);
  }

  @GetMapping("/findByName")
  List<MyUser> findByName(@RequestParam String name) {
    return myUserRepo.findAllByUsername(name);
  }

  @PutMapping("/insert")
  String insert(@RequestParam String username,
              @RequestParam String password) {
    MyUser myUser = new MyUser();
    myUser.setUsername(username);
    myUser.setPassword(new BCryptPasswordEncoder().encode(password));
    myUser.setRoles(new ArrayList<>(){{
      add("ROLE_" + SecurityConfiguration.ROLE_USER);
    }});
    myUserRepo.save(myUser);
    return myUser.getId();
  }

  @GetMapping("/all")
  List<MyUser> getAll() {
    return myUserRepo.findAll();
  }

  @GetMapping("/dropAll")
  void deleteAll() {
    myUserRepo.deleteAll();
  }
}
