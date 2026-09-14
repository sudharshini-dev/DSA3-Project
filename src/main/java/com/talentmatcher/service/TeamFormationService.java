package com.talentmatcher.service;

import com.talentmatcher.algorithm.GreedySetCover;
import com.talentmatcher.model.*;
import java.util.*;

public final class TeamFormationService {
  private final CandidateService candidates;

  public TeamFormationService(CandidateService candidates) {
    this.candidates = candidates;
  }

  public TeamResult form(Set<String> skills) {
    return new GreedySetCover().cover(skills, candidates.relevant(skills));
  }
}
