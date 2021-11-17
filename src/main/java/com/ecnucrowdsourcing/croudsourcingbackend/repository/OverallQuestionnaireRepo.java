package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.OverallQuestionnaire;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OverallQuestionnaireRepo extends ElasticsearchRepository<OverallQuestionnaire, String> {

  void deleteAllByUserIdAndJobId(String userId, String jobId);
}
