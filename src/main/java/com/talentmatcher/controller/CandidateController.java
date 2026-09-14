package com.talentmatcher.controller;

import com.talentmatcher.form.CandidateForm;
import com.talentmatcher.model.Candidate;
import com.talentmatcher.service.CandidateService;
import com.talentmatcher.util.WebDisplay;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/candidates")
public class CandidateController {
  private final CandidateService candidates;

  public CandidateController(CandidateService c) {
    candidates = c;
  }

  private Candidate find(String id) {
    try {
      return candidates.get(id);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found");
    }
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("candidates", candidates.all());
    return "candidates";
  }

  @GetMapping("/add")
  public String add(Model model) {
    model.addAttribute("candidateForm", new CandidateForm());
    model.addAttribute("formAction", "/candidates/add");
    return "candidate-form";
  }

  @PostMapping("/add")
  public String create(
      @Valid @ModelAttribute CandidateForm candidateForm,
      BindingResult errors,
      Model model,
      RedirectAttributes flash) {
    if (errors.hasErrors()) {
      model.addAttribute("formAction", "/candidates/add");
      return "candidate-form";
    }
    String id =
        candidates
            .create(
                candidateForm.getName().trim(),
                WebDisplay.split(candidateForm.getSkills()),
                candidateForm.getYearsOfExperience(),
                candidateForm.getQualification(),
                WebDisplay.split(candidateForm.getCertifications()))
            .id();
    flash.addFlashAttribute("success", "Candidate added. The skill index is up to date.");
    return "redirect:/candidates/" + id;
  }

  @GetMapping("/{id}")
  public String view(@PathVariable String id, Model model) {
    model.addAttribute("candidate", find(id));
    return "candidate-detail";
  }

  @GetMapping("/{id}/edit")
  public String edit(@PathVariable String id, Model model) {
    Candidate c = find(id);
    CandidateForm form = new CandidateForm();
    form.setName(c.name());
    form.setSkills(String.join(", ", new java.util.TreeSet<>(c.skills())));
    form.setYearsOfExperience(c.yearsOfExperience());
    form.setQualification(c.qualification());
    form.setCertifications(String.join(", ", new java.util.TreeSet<>(c.certifications())));
    model.addAttribute("candidateForm", form);
    model.addAttribute("formAction", "/candidates/" + id + "/edit");
    model.addAttribute("editing", true);
    return "candidate-form";
  }

  @PostMapping("/{id}/edit")
  public String update(
      @PathVariable String id,
      @Valid @ModelAttribute CandidateForm candidateForm,
      BindingResult errors,
      Model model,
      RedirectAttributes flash) {
    find(id);
    if (errors.hasErrors()) {
      model.addAttribute("formAction", "/candidates/" + id + "/edit");
      model.addAttribute("editing", true);
      return "candidate-form";
    }
    candidates.save(
        new Candidate(
            id,
            candidateForm.getName().trim(),
            WebDisplay.split(candidateForm.getSkills()),
            candidateForm.getYearsOfExperience(),
            candidateForm.getQualification(),
            WebDisplay.split(candidateForm.getCertifications())));
    flash.addFlashAttribute("success", "Candidate updated. Skill postings have been refreshed.");
    return "redirect:/candidates/" + id;
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable String id, RedirectAttributes flash) {
    find(id);
    candidates.delete(id);
    flash.addFlashAttribute("success", "Candidate deleted and removed from the skill index.");
    return "redirect:/candidates";
  }
}
