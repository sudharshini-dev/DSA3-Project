package com.talentmatcher.util;

import com.talentmatcher.model.*;
import java.util.*;

public final class ConsolePrinter {
  private ConsolePrinter() {}

  public static String sorted(Collection<String> skills) {
    return new TreeSet<>(skills).toString();
  }

  public static void candidate(Candidate c) {
    System.out.printf(
        "%s | %s | %s | %.1f years | %s%n",
        c.id(), c.name(), sorted(c.skills()), c.yearsOfExperience(), c.qualification());
  }

  public static void job(Job j) {
    System.out.printf(
        "%s | %s | %s | %.1f years | %s%n",
        j.id(),
        j.title(),
        sorted(j.requiredSkills()),
        j.minimumExperience(),
        j.requiredQualification());
  }

  public static void match(MatchResult m) {
    System.out.printf(
        Locale.ROOT,
        "%nCandidate: %s (%s)%nJob: %s (%s)%nMatched Skills: %s%nMissing Skills: %s%nSkill Score:"
            + " %.2f%%%nExperience Score: %.2f%%%nQualification Score: %.2f%%%nFinal Compatibility:"
            + " %.2f%%%nExplanation: %s%n",
        m.candidate().name(),
        m.candidate().id(),
        m.job().title(),
        m.job().id(),
        sorted(m.matchedSkills()),
        sorted(m.missingSkills()),
        m.skillScore(),
        m.experienceScore(),
        m.qualificationScore(),
        m.finalScore(),
        m.explanation());
  }

  public static void assignment(AssignmentResult a) {
    for (MatchResult m : a.assignments())
      System.out.printf(
          Locale.ROOT, "%s -> %s -> %.2f%n", m.candidate().name(), m.job().title(), m.finalScore());
    System.out.printf(Locale.ROOT, "Total compatibility: %.2f%n", a.totalScore());
    System.out.println(
        "Unassigned candidates: "
            + a.unassignedCandidates().stream().map(Candidate::name).toList());
    System.out.println("Unfilled jobs: " + a.unfilledJobs().stream().map(Job::title).toList());
  }

  public static void team(TeamResult t) {
    System.out.println("Greedy set-cover team (approximation):");
    for (Candidate c : t.members())
      System.out.println(c.name() + " -> newly covered " + sorted(t.contributions().get(c.id())));
    System.out.println("Covered: " + sorted(t.coveredSkills()));
    System.out.println("Uncovered: " + sorted(t.uncoveredSkills()));
    System.out.println("Full coverage: " + t.fullCoverage());
  }
}
