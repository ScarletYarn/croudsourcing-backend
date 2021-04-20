package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Reward;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface RewardRepo extends ElasticsearchRepository<Reward, String> {

    List<Reward> findAllByUserId(String userId);

    List<Reward> findAllByUserIdAndJobId(String userId, String jobId);
}
