package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Appeal;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AppealRepo extends ElasticsearchRepository<Appeal, String> {
}
