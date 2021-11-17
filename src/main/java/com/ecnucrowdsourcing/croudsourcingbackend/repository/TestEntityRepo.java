package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.TestEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TestEntityRepo extends ElasticsearchRepository<TestEntity, String> {

  List<TestEntity> findAllBySeq(Integer seq);
}
