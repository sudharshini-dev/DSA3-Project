package com.talentmatcher.controller;

import com.talentmatcher.form.SelectionForm;
import com.talentmatcher.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RecommendationController {
  private final RecommendationService recommendations;
  private final CandidateService candidates;

  public RecommendationController(RecommendationService r, CandidateService c) {
    recommendations = r;
    candidates = c;
  }

  @GetMapping("/recommendations")
  public String page(Model model) {
    model.addAttribute("selectionForm", new SelectionForm());
    model.addAttribute("candidates", candidates.all());
    return "recommendations";
  }

  @PostMapping("/recommendations")
  public String recommend(
      @Valid @ModelAttribute SelectionForm selectionForm, BindingResult errors, Model model) {
    model.addAttribute("candidates", candidates.all());
    if (!errors.hasErrors())
      try {
        var candidate = candidates.get(selectionForm.getSelectedId());
        model.addAttribute("selectedCandidate", candidate);
        model.addAttribute(
            "results", recommendations.recommend(candidate, selectionForm.getTopN()));
      } catch (IllegalArgumentException e) {
        errors.rejectValue("selectedId", "missing", "That candidate is no longer available.");
      }
    return "recommendations";
  }
}
