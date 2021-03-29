package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.RatingSheet;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RatingSheetRepo extends ElasticsearchRepository<RatingSheet, String> {
}
