package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.GroupComment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface GroupCommentRepo extends ElasticsearchRepository<GroupComment, String> {

  void deleteAllByJobIdAndUserId(String jobId, String userId);

  Optional<GroupComment> findByJobIdAndUserIdAndPrevRuleId(String jobId, String userId, String prevRuleId);
}
