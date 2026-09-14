package com.talentmatcher.service;

import com.talentmatcher.index.CandidateIndex;
import com.talentmatcher.model.*;
import java.util.*;

public final class CandidateService {
  private final CandidateIndex index = new CandidateIndex();

  private int nextId = 9;

  public synchronized Candidate create(
      String name,
      HashSet<String> skills,
      double years,
      Qualification qualification,
      Set<String> certifications) {
    String id;
    do {
      id = "C" + nextId++;
    } while (index.find(id).isPresent());
    Candidate candidate = new Candidate(id, name, skills, years, qualification, certifications);
    save(candidate);
    return candidate;
  }

  public synchronized void delete(String id) {
    index.remove(id);
  }

  public synchronized void save(Candidate c) {
    index.put(c);
  }

  public Candidate get(String id) {
    return index
        .find(id)
        .orElseThrow(() -> new IllegalArgumentException("Unknown candidate: " + id));
  }

  public List<Candidate> all() {
    return index.all();
  }

  public List<Candidate> relevant(Set<String> skills) {
    return index.relevant(skills);
  }

  public Map<String, Set<String>> skillIndex() {
    return index.skillIndex();
  }
}
