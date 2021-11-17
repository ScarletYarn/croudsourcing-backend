package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.ExpSeq;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface ExpSeqRepo extends ElasticsearchRepository<ExpSeq, String> {

  Optional<ExpSeq> findByUserIdAndRuleId(String userId, String ruleId);
}
