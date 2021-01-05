package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.Article;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.ArticleRepo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

  @Resource
  private ArticleRepo articleRepo;

  @GetMapping("/all")
  List<Article> findAll() {
    return articleRepo.findAll();
  }

  @PutMapping("/put")
  void insert(
          @RequestParam String title,
          @RequestParam(required = false) Date date
  ) {
    Article article = new Article();
    article.setDate(new Date());
    if (date != null)
      article.setDate(date);
    article.setTitle(title);
    articleRepo.save(article);
  }
}
