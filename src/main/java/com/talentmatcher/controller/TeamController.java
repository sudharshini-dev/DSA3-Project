package com.talentmatcher.controller;

import com.talentmatcher.form.TeamForm;
import com.talentmatcher.normalization.SkillNormalizer;
import com.talentmatcher.service.TeamFormationService;
import com.talentmatcher.util.WebDisplay;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class TeamController {
  private final TeamFormationService teams;

  public TeamController(TeamFormationService t) {
    teams = t;
  }

  @GetMapping("/team")
  public String page(Model model) {
    model.addAttribute("teamForm", new TeamForm());
    return "team";
  }

  @PostMapping("/team")
  public String form(@Valid @ModelAttribute TeamForm teamForm, BindingResult errors, Model model) {
    if (!errors.hasErrors()) {
      var required = new SkillNormalizer().normalizeAll(WebDisplay.split(teamForm.getSkills()));
      model.addAttribute("requiredSkills", required);
      model.addAttribute("result", teams.form(required));
    }
    return "team";
  }
}
