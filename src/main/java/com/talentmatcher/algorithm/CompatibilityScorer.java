package com.talentmatcher.algorithm;

import com.talentmatcher.model.*;
import java.util.*;

public final class CompatibilityScorer {
  private final double skillsWeight, experienceWeight, qualificationWeight;

  public CompatibilityScorer() {
    this(.7, .2, .1);
  }

  public CompatibilityScorer(double s, double e, double q) {
    if (!Double.isFinite(s + e + q) || s < 0 || e < 0 || q < 0 || Math.abs(s + e + q - 1) > 1e-9)
      throw new IllegalArgumentException("Weights must be nonnegative and sum to 1");
    skillsWeight = s;
    experienceWeight = e;
    qualificationWeight = q;
  }

  public MatchResult score(Candidate c, Job j) {
    HashSet<String> required = j.requiredSkills(), possessed = c.skills();
    Set<String> matched = new HashSet<>(), missing = new HashSet<>();
    for (String skill : required) {
      if (possessed.contains(skill)) matched.add(skill);
      else missing.add(skill);
    }
    double s = required.isEmpty() ? 100 : 100.0 * matched.size() / required.size();
    double e =
        j.minimumExperience() == 0
            ? 100
            : Math.min(100, 100 * c.yearsOfExperience() / j.minimumExperience());
    double q = c.qualification().ordinal() >= j.requiredQualification().ordinal() ? 100 : 0;
    double total = skillsWeight * s + experienceWeight * e + qualificationWeight * q;
    String explanation =
        String.format(
            Locale.ROOT,
            "Matches %d of %d required skills%s; %s experience requirement; %s qualification"
                + " requirement. Weighted score: %.2f*%.2f + %.2f*%.2f + %.2f*%.2f = %.2f.",
            matched.size(),
            required.size(),
            required.isEmpty() ? " (no skills required: 100%)" : "",
            e == 100 ? "meets" : "falls below",
            q == 100 ? "meets" : "falls below",
            skillsWeight,
            s,
            experienceWeight,
            e,
            qualificationWeight,
            q,
            total);
    return new MatchResult(c, j, total, s, e, q, matched, missing, explanation);
  }
}
