package com.talentmatcher.model;

/** Enum order implements the explicit educational ordering used by the scorer. */
public enum Qualification {
  HIGH_SCHOOL,
  DIPLOMA,
  BACHELORS,
  MASTERS,
  PHD;

  public static Qualification parse(String text) {
    String s = text.toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z]", "");
    return switch (s) {
      case "highschool" -> HIGH_SCHOOL;
      case "diploma" -> DIPLOMA;
      case "bachelor", "bachelors", "btech", "bsc" -> BACHELORS;
      case "master", "masters", "mtech", "msc" -> MASTERS;
      case "phd" -> PHD;
      default -> throw new IllegalArgumentException("Unknown qualification: " + text);
    };
  }
}
