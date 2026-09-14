package com.talentmatcher.parser;

import com.talentmatcher.model.*;
import java.util.*;

/** Deliberately explicit text format: id|name|skill,skill|years|qualification. */
public final class ResumeParser {
  public Candidate parse(String line) {
    String[] p = line.split("\\|", -1);
    if (p.length != 5)
      throw new IllegalArgumentException("Expected id|name|skills|years|qualification");
    return new Candidate(
        p[0].trim(),
        p[1].trim(),
        new HashSet<>(Arrays.asList(p[2].split(","))),
        Double.parseDouble(p[3].trim()),
        Qualification.parse(p[4]));
  }
}
