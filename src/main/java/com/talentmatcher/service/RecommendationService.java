package com.talentmatcher.service;

import com.talentmatcher.algorithm.CompatibilityScorer;
import com.talentmatcher.model.*;
import java.util.*;

public final class RecommendationService {
  private final JobService jobs;
  private final CompatibilityScorer scorer;

  public RecommendationService(JobService jobs, CompatibilityScorer scorer) {
    this.jobs = jobs;
    this.scorer = scorer;
  }

  public List<MatchResult> recommend(Candidate candidate, int topN) {
    if (topN < 0) throw new IllegalArgumentException("Top N must be nonnegative");
    return jobs.all().stream()
        .map(j -> scorer.score(candidate, j))
        .sorted(
            Comparator.comparingDouble(MatchResult::finalScore)
                .reversed()
                .thenComparing(m -> m.job().id()))
        .limit(topN)
        .toList();
  }
}
