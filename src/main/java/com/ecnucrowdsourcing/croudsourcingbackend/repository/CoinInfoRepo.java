package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.CoinInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface CoinInfoRepo extends ElasticsearchRepository<CoinInfo, String> {

  Optional<CoinInfo> findByUserId(String userId);
}
