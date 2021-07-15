package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Reward;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface RewardRepo extends ElasticsearchRepository<Reward, String> {

    List<Reward> findAllByUserId(String userId);

    Optional<Reward> findByUserIdAndJobId(String userId, String jobId);
}
