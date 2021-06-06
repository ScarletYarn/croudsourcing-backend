package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.UserAction;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserActionRepo extends ElasticsearchRepository<UserAction, String> {

  List<UserAction> findAllByUserIdOrderByDateAsc(String userId);
}
