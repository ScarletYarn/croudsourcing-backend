package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.AnswerRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRecordRepo extends ElasticsearchRepository<AnswerRecord, String> {

  List<AnswerRecord> findAllByJobIdAndUserId(String jobId, String userId);

  Optional<AnswerRecord> findByJobIdAndRuleDataIdAndUserId(String jobId, String ruleDataId, String userId);

  List<AnswerRecord> findAllByUserId(String userId);

  void deleteAllByJobId(String jobId);

  void deleteAllByJobIdAndUserId(String jobId, String userId);

  Iterable<AnswerRecord> findAllByJobId(String jobId);
}
