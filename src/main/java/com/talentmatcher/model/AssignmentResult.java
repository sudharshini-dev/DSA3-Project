package com.talentmatcher.model;

import java.util.*;

public record AssignmentResult(
    List<MatchResult> assignments,
    double totalScore,
    List<Candidate> unassignedCandidates,
    List<Job> unfilledJobs) {
  public AssignmentResult {
    assignments = List.copyOf(assignments);
    unassignedCandidates = List.copyOf(unassignedCandidates);
    unfilledJobs = List.copyOf(unfilledJobs);
  }
}
