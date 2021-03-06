package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Questionnaire;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface QuestionnaireRepo extends ElasticsearchRepository<Questionnaire, String> {

  Optional<Questionnaire> findByUserId(String userId);
}
