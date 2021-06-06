package com.ecnucrowdsourcing.croudsourcingbackend.controller;

import com.ecnucrowdsourcing.croudsourcingbackend.entity.TestEntity;
import com.ecnucrowdsourcing.croudsourcingbackend.repository.TestEntityRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/test")
public class TestController {

  @Resource
  private TestEntityRepo testEntityRepo;

  @GetMapping("/test")
  String crud() {
    Random random = new Random();
    int r = random.nextInt();
    TestEntity testEntity = new TestEntity();
    testEntity.setContent("Halo" + r);
    testEntity.setSeq(random.nextInt() % 10);
    testEntityRepo.save(testEntity);
    List<TestEntity> entities = testEntityRepo.findAllBySeq(r % 10);
    System.out.printf("Entity %s and %d", testEntity.getContent(), testEntity.getSeq());
    return String.format("Response%s", entities.size());
  }
}
