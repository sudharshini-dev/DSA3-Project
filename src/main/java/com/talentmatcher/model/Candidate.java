package com.talentmatcher.model;

import com.talentmatcher.normalization.SkillNormalizer;
import java.util.*;

public record Candidate(
    String id,
    String name,
    HashSet<String> skills,
    double yearsOfExperience,
    Qualification qualification,
    Set<String> certifications) {
  public Candidate(
      String id,
      String name,
      HashSet<String> skills,
      double yearsOfExperience,
      Qualification qualification) {
    this(id, name, skills, yearsOfExperience, qualification, Set.of());
  }

  public Candidate {
    if (id == null || id.isBlank() || name == null || name.isBlank())
      throw new IllegalArgumentException("Candidate ID and name required");
    if (!Double.isFinite(yearsOfExperience) || yearsOfExperience < 0)
      throw new IllegalArgumentException("Invalid experience");
    Objects.requireNonNull(qualification);
    certifications = Set.copyOf(certifications);
    skills = new SkillNormalizer().normalizeAll(skills);
  }

  /** Defensive copies prevent callers from silently invalidating the inverted index. */
  @Override
  public HashSet<String> skills() {
    return new HashSet<>(skills);
  }
}
