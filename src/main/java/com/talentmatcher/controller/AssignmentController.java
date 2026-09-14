package com.talentmatcher.controller;

import com.talentmatcher.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AssignmentController {
  private final MatchingService matching;
  private final JobService jobs;

  public AssignmentController(MatchingService m, JobService j) {
    matching = m;
    jobs = j;
  }

  @GetMapping("/assignment")
  public String page() {
    return "assignment";
  }

  @PostMapping("/assignment")
  public String generate(Model model) {
    model.addAttribute("result", matching.assign(jobs.all()));
    return "assignment";
  }
}
