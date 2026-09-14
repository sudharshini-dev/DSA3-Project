package com.talentmatcher.controller;

import com.talentmatcher.form.SelectionForm;
import com.talentmatcher.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RankingController {
  private final MatchingService matching;
  private final JobService jobs;

  public RankingController(MatchingService m, JobService j) {
    matching = m;
    jobs = j;
  }

  @GetMapping("/ranking")
  public String page(Model model) {
    model.addAttribute("selectionForm", new SelectionForm());
    model.addAttribute("jobs", jobs.all());
    return "ranking";
  }

  @PostMapping("/ranking")
  public String rank(
      @Valid @ModelAttribute SelectionForm selectionForm, BindingResult errors, Model model) {
    model.addAttribute("jobs", jobs.all());
    if (!errors.hasErrors())
      try {
        var job = jobs.get(selectionForm.getSelectedId());
        model.addAttribute("selectedJob", job);
        model.addAttribute(
            "results",
            matching.rank(job, selectionForm.getTopN(), selectionForm.isSharedSkillsOnly()));
      } catch (IllegalArgumentException e) {
        errors.rejectValue("selectedId", "missing", "That job is no longer available.");
      }
    return "ranking";
  }
}
