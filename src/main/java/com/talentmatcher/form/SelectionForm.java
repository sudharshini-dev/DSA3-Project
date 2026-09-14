package com.talentmatcher.form;

import jakarta.validation.constraints.*;

public class SelectionForm {
  @NotBlank(message = "Choose a record to continue.")
  private String selectedId = "";

  @NotNull(message = "Enter a result limit.")
  @Min(value = 1, message = "Choose at least one result.")
  @Max(value = 100, message = "Choose 100 results or fewer.")
  private Integer topN = 10;

  private boolean sharedSkillsOnly = false;

  public String getSelectedId() {
    return selectedId;
  }

  public void setSelectedId(String v) {
    selectedId = v;
  }

  public Integer getTopN() {
    return topN;
  }

  public void setTopN(Integer v) {
    topN = v;
  }

  public boolean isSharedSkillsOnly() {
    return sharedSkillsOnly;
  }

  public void setSharedSkillsOnly(boolean v) {
    sharedSkillsOnly = v;
  }
}
