package com.talentmatcher.config;

import com.talentmatcher.algorithm.CompatibilityScorer;
import com.talentmatcher.data.SampleDataLoader;
import com.talentmatcher.service.*;
import org.springframework.context.annotation.*;

@Configuration
public class CoreConfiguration {
  @Bean
  CandidateService candidateService() {
    return new CandidateService();
  }

  @Bean
  JobService jobService() {
    return new JobService();
  }

  @Bean
  CompatibilityScorer compatibilityScorer() {
    return new CompatibilityScorer();
  }

  @Bean
  MatchingService matchingService(CandidateService c, CompatibilityScorer s) {
    return new MatchingService(c, s);
  }

  @Bean
  RecommendationService recommendationService(JobService j, CompatibilityScorer s) {
    return new RecommendationService(j, s);
  }

  @Bean
  TeamFormationService teamFormationService(CandidateService c) {
    return new TeamFormationService(c);
  }

  @Bean
  org.springframework.boot.ApplicationRunner sampleData(CandidateService c, JobService j) {
    return args -> SampleDataLoader.load(c, j);
  }
}
