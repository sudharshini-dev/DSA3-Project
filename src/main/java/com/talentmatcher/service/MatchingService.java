package com.talentmatcher.service;

import com.talentmatcher.algorithm.*;
import com.talentmatcher.model.*;
import java.util.*;

public final class MatchingService {
  private final CandidateService candidates;
  private final CompatibilityScorer scorer;

  public MatchingService(CandidateService candidates, CompatibilityScorer scorer) {
    this.candidates = candidates;
    this.scorer = scorer;
  }

  /** Indexed mode considers shared-skill candidates; all mode includes zero-overlap matches. */
  public List<MatchResult> rank(Job job, int topN, boolean sharedSkillsOnly) {
    if (topN < 0) throw new IllegalArgumentException("Top N must be nonnegative");
    List<Candidate> pool =
        sharedSkillsOnly && !job.requiredSkills().isEmpty()
            ? candidates.relevant(job.requiredSkills())
            : candidates.all();
    PriorityQueue<MatchResult> heap =
        new PriorityQueue<>(
            Comparator.comparingDouble(MatchResult::finalScore)
                .reversed()
                .thenComparing(m -> m.candidate().id()));
    for (Candidate c : pool) heap.add(scorer.score(c, job));
    List<MatchResult> result = new ArrayList<>();
    while (!heap.isEmpty() && result.size() < topN) result.add(heap.remove());
    return result;
  }

  public AssignmentResult assign(List<Job> jobs) {
    CompatibilityGraph graph = new CompatibilityGraph(candidates.all(), jobs, scorer);
    int[] allocation = new HungarianAlgorithm().maximize(graph.weights());
    List<MatchResult> matches = new ArrayList<>();
    List<Candidate> unassigned = new ArrayList<>();
    Set<String> filled = new HashSet<>();
    double total = 0;
    for (int i = 0; i < allocation.length; i++) {
      Candidate c = graph.candidates().get(i);
      if (allocation[i] < 0) unassigned.add(c);
      else {
        MatchResult m = scorer.score(c, graph.jobs().get(allocation[i]));
        matches.add(m);
        total += m.finalScore();
        filled.add(m.job().id());
      }
    }
    return new AssignmentResult(
        matches, total, unassigned, jobs.stream().filter(j -> !filled.contains(j.id())).toList());
  }
}
