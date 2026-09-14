package com.talentmatcher.util;

import com.talentmatcher.model.Qualification;
import java.util.*;
import org.springframework.stereotype.Component;

/** Presentation helpers only: no matching or assignment algorithms belong here. */
@Component("display")
public class WebDisplay {
  public String number(double value) {
    return String.format(Locale.ROOT, "%.1f", value);
  }

  public List<String> sorted(Collection<String> values) {
    return values.stream().sorted().toList();
  }

  public String qualification(Qualification q) {
    return switch (q) {
      case HIGH_SCHOOL -> "High School";
      case DIPLOMA -> "Diploma";
      case BACHELORS -> "Bachelor's";
      case MASTERS -> "Master's";
      case PHD -> "PhD";
    };
  }

  public static HashSet<String> split(String value) {
    HashSet<String> result = new HashSet<>();
    if (value != null) for (String s : value.split(",")) if (!s.isBlank()) result.add(s.trim());
    return result;
  }
}
