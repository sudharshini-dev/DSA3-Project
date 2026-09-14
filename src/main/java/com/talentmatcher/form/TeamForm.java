package com.talentmatcher.form;

import jakarta.validation.constraints.Size;

public class TeamForm {
  @Size(max = 2000, message = "Use 2,000 characters or fewer.")
  private String skills = "Java, Python, SQL, AWS, Docker, Cybersecurity";

  public String getSkills() {
    return skills;
  }

  public void setSkills(String v) {
    skills = v;
  }
}
