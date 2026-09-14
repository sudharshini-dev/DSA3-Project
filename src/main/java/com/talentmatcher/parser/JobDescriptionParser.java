package com.talentmatcher.parser;

import com.talentmatcher.model.*;
import java.util.*;

/** Structured plain text parser, not a PDF reader or an NLP inference engine. */
public final class JobDescriptionParser {
  public Job parse(String line) {
    String[] p = line.split("\\|", -1);
    if (p.length != 5)
      throw new IllegalArgumentException("Expected id|title|skills|years|qualification");
    return new Job(
        p[0].trim(),
        p[1].trim(),
        new HashSet<>(Arrays.asList(p[2].split(","))),
        Double.parseDouble(p[3].trim()),
        Qualification.parse(p[4]));
  }
}
