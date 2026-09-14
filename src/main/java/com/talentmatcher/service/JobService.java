package com.talentmatcher.service;

import com.talentmatcher.model.Job;
import java.util.*;

public final class JobService {
  private final HashMap<String, Job> jobs = new HashMap<>();

  private int nextId = 6;

  public synchronized Job create(
      String title,
      HashSet<String> skills,
      double years,
      com.talentmatcher.model.Qualification qualification) {
    String id;
    do {
      id = "J" + nextId++;
    } while (jobs.containsKey(id));
    Job job = new Job(id, title, skills, years, qualification);
    save(job);
    return job;
  }

  public synchronized void save(Job j) {
    jobs.put(j.id(), j);
  }

  public synchronized Job get(String id) {
    Job j = jobs.get(id);
    if (j == null) throw new IllegalArgumentException("Unknown job: " + id);
    return j;
  }

  public synchronized List<Job> all() {
    return jobs.values().stream().sorted(Comparator.comparing(Job::id)).toList();
  }
}
