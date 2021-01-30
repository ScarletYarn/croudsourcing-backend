package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.MyUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface MyUserRepo extends ElasticsearchRepository<MyUser, String> {

  Optional<MyUser> findById(String id);

  List<MyUser> findAllByPhone(String phone);

  List<MyUser> findAll();

  List<MyUser> findAllByUsername(String name);

  void deleteAll();
}
