package com.talentmatcher.model;

import java.util.*;

public record TeamResult(
    List<Candidate> members,
    Map<String, Set<String>> contributions,
    Set<String> coveredSkills,
    Set<String> uncoveredSkills,
    boolean fullCoverage) {
  public TeamResult {
    members = List.copyOf(members);
    Map<String, Set<String>> copy = new LinkedHashMap<>();
    contributions.forEach((k, v) -> copy.put(k, Set.copyOf(v)));
    contributions = Collections.unmodifiableMap(copy);
    coveredSkills = Set.copyOf(coveredSkills);
    uncoveredSkills = Set.copyOf(uncoveredSkills);
  }
}
