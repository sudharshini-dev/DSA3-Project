package com.talentmatcher.controller;

import com.talentmatcher.service.CandidateService;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SkillIndexController {
  private final CandidateService candidates;

  public SkillIndexController(CandidateService c) {
    candidates = c;
  }

  @GetMapping("/skill-index")
  public String index(Model model) {
    model.addAttribute("skillIndex", candidates.skillIndex());
    Map<String, String> names = new HashMap<>();
    candidates.all().forEach(c -> names.put(c.id(), c.name()));
    model.addAttribute("candidateNames", names);
    return "skill-index";
  }
}
