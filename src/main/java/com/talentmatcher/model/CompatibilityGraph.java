package com.talentmatcher.model;

import com.talentmatcher.algorithm.CompatibilityScorer;
import java.util.*;

/** Complete weighted bipartite graph; matrix cell (i,j) is a candidate-job edge. */
public final class CompatibilityGraph {
  private final List<Candidate> candidates;
  private final List<Job> jobs;
  private final double[][] weights;

  public CompatibilityGraph(
      List<Candidate> candidates, List<Job> jobs, CompatibilityScorer scorer) {
    this.candidates = List.copyOf(candidates);
    this.jobs = List.copyOf(jobs);
    weights = new double[candidates.size()][jobs.size()];
    for (int i = 0; i < candidates.size(); i++)
      for (int j = 0; j < jobs.size(); j++)
        weights[i][j] = scorer.score(candidates.get(i), jobs.get(j)).finalScore();
  }

  public List<Candidate> candidates() {
    return candidates;
  }

  public List<Job> jobs() {
    return jobs;
  }

  public double[][] weights() {
    return Arrays.stream(weights).map(double[]::clone).toArray(double[][]::new);
  }
}
