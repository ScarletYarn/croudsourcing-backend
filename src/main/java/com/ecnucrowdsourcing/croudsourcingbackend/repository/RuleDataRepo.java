package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.RuleData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface RuleDataRepo extends ElasticsearchRepository<RuleData, String> {

  List<RuleData> findAllByJobId(String jobId);

  Optional<RuleData> findById(String id);
}
