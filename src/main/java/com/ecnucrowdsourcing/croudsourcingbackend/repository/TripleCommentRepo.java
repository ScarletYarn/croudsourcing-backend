package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.kb.TripleComment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TripleCommentRepo extends ElasticsearchRepository<TripleComment, String> {
  List<TripleComment> findAllByTripleId(String tripleId);
}
