package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.JobStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface JobStatusRepo extends ElasticsearchRepository<JobStatus, String> {

  Optional<JobStatus> findByJobIdAndUserId(String jobId, String userId);

  void deleteAllByJobId(String jobId);

  void deleteAllByUserIdAndJobId(String userId, String jobId);
}
