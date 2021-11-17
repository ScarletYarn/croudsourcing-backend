package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.RatingSheet;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface RatingSheetRepo extends ElasticsearchRepository<RatingSheet, String> {

  Optional<RatingSheet> findByJobIdAndUserIdAndPrevRuleId(String jobId, String userId, String prevRuleId);

  List<RatingSheet> findAllByJobIdAndUserIdOrderByDateDesc(String jobId, String userId);
}
