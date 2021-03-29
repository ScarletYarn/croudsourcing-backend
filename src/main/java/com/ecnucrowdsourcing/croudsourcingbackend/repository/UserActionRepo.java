package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.UserAction;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserActionRepo extends ElasticsearchRepository<UserAction, String> {
}
