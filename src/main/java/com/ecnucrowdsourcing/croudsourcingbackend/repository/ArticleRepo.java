package com.ecnucrowdsourcing.croudsourcingbackend.repository;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepo extends ElasticsearchRepository<Article, String> {

  Optional<Article> findById(String id);

  List<Article> findAll();

  List<Article> findAllByTitle(String name);
}
