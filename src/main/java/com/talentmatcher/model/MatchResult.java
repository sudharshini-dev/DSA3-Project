package com.talentmatcher.model;

import java.util.*;

public record MatchResult(
    Candidate candidate,
    Job job,
    double finalScore,
    double skillScore,
    double experienceScore,
    double qualificationScore,
    Set<String> matchedSkills,
    Set<String> missingSkills,
    String explanation) {
  public MatchResult {
    matchedSkills = Set.copyOf(matchedSkills);
    missingSkills = Set.copyOf(missingSkills);
  }
}
