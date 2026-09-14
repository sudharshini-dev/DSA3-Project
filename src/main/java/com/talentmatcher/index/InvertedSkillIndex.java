package com.talentmatcher.index;

import com.talentmatcher.model.Candidate;
import com.talentmatcher.normalization.SkillNormalizer;
import java.util.*;

/** Posting sets map each normalized skill to unique candidate IDs. */
public final class InvertedSkillIndex {
  private final HashMap<String, HashSet<String>> postings = new HashMap<>();

  public void add(Candidate c) {
    for (String s : c.skills()) postings.computeIfAbsent(s, k -> new HashSet<>()).add(c.id());
  }

  public void remove(Candidate c) {
    for (String s : c.skills()) {
      Set<String> ids = postings.get(s);
      if (ids != null) {
        ids.remove(c.id());
        if (ids.isEmpty()) postings.remove(s);
      }
    }
  }

  public Set<String> candidatesFor(Collection<String> skills) {
    Set<String> ids = new HashSet<>();
    for (String s : new SkillNormalizer().normalizeAll(skills))
      ids.addAll(postings.getOrDefault(s, new HashSet<>()));
    return ids;
  }

  public Map<String, Set<String>> snapshot() {
    Map<String, Set<String>> copy = new TreeMap<>();
    postings.forEach((s, ids) -> copy.put(s, Collections.unmodifiableSet(new TreeSet<>(ids))));
    return Collections.unmodifiableMap(copy);
  }
}
