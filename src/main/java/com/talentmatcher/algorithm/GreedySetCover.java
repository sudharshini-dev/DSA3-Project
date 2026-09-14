package com.talentmatcher.algorithm;

import com.talentmatcher.model.*;
import com.talentmatcher.normalization.SkillNormalizer;
import java.util.*;

/** Greedy approximation: never claims to guarantee the mathematically smallest team. */
public final class GreedySetCover {
  public TeamResult cover(Collection<String> requirements, List<Candidate> candidates) {
    Set<String> remaining = new SkillNormalizer().normalizeAll(requirements),
        covered = new HashSet<>();
    List<Candidate> pool = new ArrayList<>(candidates);
    pool.sort(Comparator.comparing(Candidate::id));
    List<Candidate> team = new ArrayList<>();
    Map<String, Set<String>> contributions = new LinkedHashMap<>();
    while (!remaining.isEmpty()) {
      Candidate best = null;
      Set<String> gain = new HashSet<>();
      for (Candidate c : pool) {
        Set<String> current = c.skills();
        current.retainAll(remaining);
        if (current.size() > gain.size()) {
          best = c;
          gain = current;
        }
      }
      if (best == null) break;
      team.add(best);
      contributions.put(best.id(), gain);
      covered.addAll(gain);
      remaining.removeAll(gain);
      pool.remove(best);
    }
    return new TeamResult(team, contributions, covered, remaining, remaining.isEmpty());
  }
}
