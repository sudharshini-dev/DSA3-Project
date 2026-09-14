package com.talentmatcher.model;

import com.talentmatcher.normalization.SkillNormalizer;
import java.util.*;

public record Job(
    String id,
    String title,
    HashSet<String> requiredSkills,
    double minimumExperience,
    Qualification requiredQualification) {
  public Job {
    if (id == null || id.isBlank() || title == null || title.isBlank())
      throw new IllegalArgumentException("Job ID and title required");
    if (!Double.isFinite(minimumExperience) || minimumExperience < 0)
      throw new IllegalArgumentException("Invalid experience");
    Objects.requireNonNull(requiredQualification);
    requiredSkills = new SkillNormalizer().normalizeAll(requiredSkills);
  }

  @Override
  public HashSet<String> requiredSkills() {
    return new HashSet<>(requiredSkills);
  }
}
