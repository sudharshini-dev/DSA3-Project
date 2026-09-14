package com.talentmatcher.index;

import com.talentmatcher.model.Candidate;
import java.util.*;

public final class CandidateIndex {
  private final HashMap<String, Candidate> candidates = new HashMap<>();
  private final InvertedSkillIndex skills = new InvertedSkillIndex();

  /** Delete both the lookup entry and all of its skill postings atomically. */
  public synchronized void remove(String id) {
    Candidate old = candidates.remove(id);
    if (old != null) skills.remove(old);
  }

  public synchronized void put(Candidate c) {
    Candidate old = candidates.put(c.id(), c);
    if (old != null) skills.remove(old);
    skills.add(c);
  }

  public synchronized Optional<Candidate> find(String id) {
    return Optional.ofNullable(candidates.get(id));
  }

  public synchronized List<Candidate> all() {
    return candidates.values().stream().sorted(Comparator.comparing(Candidate::id)).toList();
  }

  public synchronized List<Candidate> relevant(Collection<String> required) {
    return skills.candidatesFor(required).stream().sorted().map(candidates::get).toList();
  }

  public synchronized Map<String, Set<String>> skillIndex() {
    return skills.snapshot();
  }
}
