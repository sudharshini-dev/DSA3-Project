package com.talentmatcher.form;

import com.talentmatcher.model.Qualification;
import jakarta.validation.constraints.*;

public class CandidateForm {
  @NotBlank(message = "Enter the candidate's name.")
  @Size(max = 120)
  private String name = "";

  @Size(max = 2000)
  private String skills = "";

  @NotNull(message = "Enter years of experience.")
  @DecimalMin(value = "0", message = "Experience cannot be negative.")
  @DecimalMax(value = "80", message = "Use 80 years or fewer.")
  private Double yearsOfExperience = 0.0;

  @NotNull(message = "Choose a qualification.")
  private Qualification qualification = Qualification.BACHELORS;

  @Size(max = 1000)
  private String certifications = "";

  public String getName() {
    return name;
  }

  public void setName(String v) {
    name = v;
  }

  public String getSkills() {
    return skills;
  }

  public void setSkills(String v) {
    skills = v;
  }

  public Double getYearsOfExperience() {
    return yearsOfExperience;
  }

  public void setYearsOfExperience(Double v) {
    yearsOfExperience = v;
  }

  public Qualification getQualification() {
    return qualification;
  }

  public void setQualification(Qualification v) {
    qualification = v;
  }

  public String getCertifications() {
    return certifications;
  }

  public void setCertifications(String v) {
    certifications = v;
  }
}
