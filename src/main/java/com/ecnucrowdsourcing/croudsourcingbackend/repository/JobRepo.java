package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Job;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepo extends ElasticsearchRepository<Job, String> {

    List<Job> findAll();

    Optional<Job> findById(String jobId);

    Optional<Job> findBySeq(Integer seq);
}
