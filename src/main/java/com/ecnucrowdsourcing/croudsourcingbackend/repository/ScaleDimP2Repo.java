package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.ScaleDimP2;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface ScaleDimP2Repo extends ElasticsearchRepository<ScaleDimP2, String> {

  Optional<ScaleDimP2> findById(String id);
}
