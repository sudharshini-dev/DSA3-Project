package com.talentmatcher.controller;

import com.talentmatcher.form.JobForm;
import com.talentmatcher.service.JobService;
import com.talentmatcher.util.WebDisplay;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/jobs")
public class JobController {
  private final JobService jobs;

  public JobController(JobService j) {
    jobs = j;
  }

  @GetMapping
  public String list(Model model) {
    model.addAttribute("jobs", jobs.all());
    return "jobs";
  }

  @GetMapping("/add")
  public String add(Model model) {
    model.addAttribute("jobForm", new JobForm());
    return "job-form";
  }

  @PostMapping("/add")
  public String create(
      @Valid @ModelAttribute JobForm jobForm, BindingResult errors, RedirectAttributes flash) {
    if (errors.hasErrors()) return "job-form";
    jobs.create(
        jobForm.getTitle().trim(),
        WebDisplay.split(jobForm.getRequiredSkills()),
        jobForm.getMinimumExperience(),
        jobForm.getRequiredQualification());
    flash.addFlashAttribute("success", "Job added. It is ready for ranking and assignment.");
    return "redirect:/jobs";
  }
}
