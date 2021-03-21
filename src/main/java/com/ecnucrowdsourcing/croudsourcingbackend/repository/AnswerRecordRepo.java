package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.AnswerRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface AnswerRecordRepo extends ElasticsearchRepository<AnswerRecord, String> {

  List<AnswerRecord> findAllByJobIdAndUserId(String jobId, String userId);
}
