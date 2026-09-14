package com.talentmatcher.form;

import com.talentmatcher.model.Qualification;
import jakarta.validation.constraints.*;

public class JobForm {
  @NotBlank(message = "Enter a job title.")
  @Size(max = 120)
  private String title = "";

  @Size(max = 2000)
  private String requiredSkills = "";

  @NotNull(message = "Enter minimum experience.")
  @DecimalMin(value = "0", message = "Experience cannot be negative.")
  @DecimalMax(value = "80", message = "Use 80 years or fewer.")
  private Double minimumExperience = 0.0;

  @NotNull(message = "Choose a qualification.")
  private Qualification requiredQualification = Qualification.BACHELORS;

  public String getTitle() {
    return title;
  }

  public void setTitle(String v) {
    title = v;
  }

  public String getRequiredSkills() {
    return requiredSkills;
  }

  public void setRequiredSkills(String v) {
    requiredSkills = v;
  }

  public Double getMinimumExperience() {
    return minimumExperience;
  }

  public void setMinimumExperience(Double v) {
    minimumExperience = v;
  }

  public Qualification getRequiredQualification() {
    return requiredQualification;
  }

  public void setRequiredQualification(Qualification v) {
    requiredQualification = v;
  }
}
