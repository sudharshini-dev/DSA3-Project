package com.talentmatcher.controller;

import com.talentmatcher.algorithm.CompatibilityScorer;
import com.talentmatcher.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  private final CandidateService candidates;
  private final JobService jobs;
  private final CompatibilityScorer scorer;

  public HomeController(CandidateService c, JobService j, CompatibilityScorer s) {
    candidates = c;
    jobs = j;
    scorer = s;
  }

  @GetMapping("/")
  public String home(Model model) {
    var allCandidates = candidates.all();
    var allJobs = jobs.all();
    double sum = 0;
    for (var c : allCandidates) for (var j : allJobs) sum += scorer.score(c, j).finalScore();
    long pairs = (long) allCandidates.size() * allJobs.size();
    model.addAttribute("candidateCount", allCandidates.size());
    model.addAttribute("jobCount", allJobs.size());
    model.addAttribute("skillCount", candidates.skillIndex().size());
    model.addAttribute("averageScore", pairs == 0 ? 0 : sum / pairs);
    model.addAttribute("jobs", allJobs.stream().limit(4).toList());
    return "index";
  }
}
