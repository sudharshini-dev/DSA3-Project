package com.talentmatcher;

import static org.junit.jupiter.api.Assertions.*;

import com.talentmatcher.algorithm.*;
import com.talentmatcher.index.*;
import com.talentmatcher.model.*;
import com.talentmatcher.normalization.*;
import com.talentmatcher.parser.*;
import com.talentmatcher.service.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class EngineTest {
  Candidate c(String id, double years, String... skills) {
    return new Candidate(id, id, new HashSet<>(List.of(skills)), years, Qualification.BACHELORS);
  }

  Job j(double years, String... skills) {
    return new Job("J", "Job", new HashSet<>(List.of(skills)), years, Qualification.BACHELORS);
  }

  @Test
  void normalization() {
    var n = new SkillNormalizer();
    assertEquals("machine learning", n.normalize(" Machine-Learning "));
    assertEquals("machine learning", n.normalize("ML"));
    assertEquals(
        Set.of("javascript", "spring boot", "sql"),
        n.normalizeAll(List.of("JS", "javascript", "Spring", "DB")));
    n.addAlias("K8s", "Kubernetes");
    assertEquals("kubernetes", n.normalize("K8s"));
    assertEquals("data structures", n.normalize(" DATA   STRUCTURES "));
  }

  @Test
  void scoringAndExplanation() {
    var m =
        new CompatibilityScorer()
            .score(c("A", 2, "java", "sql", "git"), j(2, "java", "sql", "git", "spring"));
    assertEquals(82.5, m.finalScore(), 1e-9);
    assertEquals(75, m.skillScore());
    assertEquals(Set.of("spring boot"), m.missingSkills());
    assertTrue(m.explanation().contains("3 of 4"));
  }

  @Test
  void scoringEdgesAndWeights() {
    var s = new CompatibilityScorer();
    assertEquals(100, s.score(c("A", 0), j(0)).finalScore(), 1e-9);
    var m = s.score(c("A", 1), j(2, "java"));
    assertEquals(0, m.skillScore());
    assertEquals(50, m.experienceScore());
    assertEquals(20, m.finalScore(), 1e-9);
    assertEquals(
        0,
        s.score(new Candidate("X", "X", new HashSet<>(), 0, Qualification.HIGH_SCHOOL), j(0))
            .qualificationScore());
    assertEquals(50, new CompatibilityScorer(0, 1, 0).score(c("A", 1), j(2)).finalScore());
    assertThrows(IllegalArgumentException.class, () -> new CompatibilityScorer(.5, .5, .5));
    assertThrows(IllegalArgumentException.class, () -> c("A", -1));
  }

  @Test
  void indexUpdatesAndDefensiveCopies() {
    CandidateIndex index = new CandidateIndex();
    Candidate a = c("A", 1, "JS");
    index.put(a);
    index.put(c("B", 1, "java"));
    assertEquals(List.of(a), index.relevant(Set.of("javascript")));
    a.skills().clear();
    assertEquals(1, index.relevant(Set.of("JS")).size());
    index.put(c("A", 1, "python"));
    assertTrue(index.relevant(Set.of("JS")).isEmpty());
    assertEquals(
        1,
        index.relevant(Set.of("python", "java")).stream().filter(x -> x.id().equals("A")).count());
    assertThrows(UnsupportedOperationException.class, () -> index.skillIndex().clear());
  }

  @Test
  void heapRankingAndFiltering() {
    var cs = new CandidateService();
    cs.save(c("B", 2, "java"));
    cs.save(c("A", 2, "java"));
    cs.save(c("C", 2));
    var s = new MatchingService(cs, new CompatibilityScorer());
    assertEquals(
        List.of("A", "B"),
        s.rank(j(2, "java"), 2, true).stream().map(m -> m.candidate().id()).toList());
    assertEquals(3, s.rank(j(2, "java"), 8, false).size());
    assertEquals(2, s.rank(j(2, "java"), 8, true).size());
    assertEquals(3, s.rank(j(0), 8, true).size());
    assertTrue(s.rank(j(0), 0, false).isEmpty());
    assertThrows(IllegalArgumentException.class, () -> s.rank(j(0), -1, false));
  }

  @Test
  void recommendationSorting() {
    var js = new JobService();
    js.save(j(0, "java"));
    js.save(new Job("K", "K", new HashSet<>(Set.of("python")), 0, Qualification.BACHELORS));
    assertEquals(
        "J",
        new RecommendationService(js, new CompatibilityScorer())
            .recommend(c("A", 0, "java"), 1)
            .get(0)
            .job()
            .id());
  }

  @Test
  void hungarianBeatsGreedy() {
    double[][] w = {{9, 8}, {8, 0}};
    int[] a = new HungarianAlgorithm().maximize(w);
    assertArrayEquals(new int[] {1, 0}, a);
    assertEquals(16, total(w, a));
  }

  @Test
  void rectangularAndEmptyMatrices() {
    var h = new HungarianAlgorithm();
    assertArrayEquals(new int[0], h.maximize(new double[0][0]));
    assertArrayEquals(new int[] {-1, -1}, h.maximize(new double[][] {{}, {}}));
    assertEquals(
        19,
        total(
            new double[][] {{10, 1}, {9, 8}, {0, 9}},
            h.maximize(new double[][] {{10, 1}, {9, 8}, {0, 9}})));
    assertEquals(
        19,
        total(
            new double[][] {{10, 1, 0}, {9, 8, 9}},
            h.maximize(new double[][] {{10, 1, 0}, {9, 8, 9}})));
    assertThrows(IllegalArgumentException.class, () -> h.maximize(new double[][] {{1}, {1, 2}}));
    assertThrows(IllegalArgumentException.class, () -> h.maximize(new double[][] {{Double.NaN}}));
  }

  @Test
  void hungarianMatchesExhaustiveSearch() {
    Random random = new Random(42);
    var h = new HungarianAlgorithm();
    for (int rows = 1; rows <= 4; rows++)
      for (int cols = 1; cols <= 4; cols++)
        for (int trial = 0; trial < 15; trial++) {
          double[][] w = new double[rows][cols];
          for (double[] row : w) for (int j = 0; j < cols; j++) row[j] = random.nextInt(101) / 10.0;
          int n = Math.max(rows, cols);
          double[][] square = new double[n][n];
          for (int i = 0; i < rows; i++) System.arraycopy(w[i], 0, square[i], 0, cols);
          int[] a = h.maximize(w);
          Set<Integer> used = new HashSet<>();
          for (int x : a) if (x >= 0) assertTrue(used.add(x));
          assertEquals(brute(square, 0, new boolean[n]), total(w, a), 1e-8);
        }
  }

  double total(double[][] w, int[] a) {
    double s = 0;
    for (int i = 0; i < a.length; i++) if (a[i] >= 0) s += w[i][a[i]];
    return s;
  }

  double brute(double[][] w, int i, boolean[] used) {
    if (i == w.length) return 0;
    double best = Double.NEGATIVE_INFINITY;
    for (int j = 0; j < w.length; j++)
      if (!used[j]) {
        used[j] = true;
        best = Math.max(best, w[i][j] + brute(w, i + 1, used));
        used[j] = false;
      }
    return best;
  }

  @Test
  void assignmentGraphAndEmptySides() {
    var cs = new CandidateService();
    var scorer = new CompatibilityScorer();
    var m = new MatchingService(cs, scorer);
    assertEquals(1, m.assign(List.of(j(0))).unfilledJobs().size());
    cs.save(c("A", 0, "java"));
    cs.save(c("B", 0));
    assertEquals(2, m.assign(List.of()).unassignedCandidates().size());
    var a = m.assign(List.of(j(0, "java")));
    assertEquals("A", a.assignments().get(0).candidate().id());
    assertEquals(100, a.totalScore(), 1e-9);
    assertEquals(1, a.unassignedCandidates().size());
    var graph = new CompatibilityGraph(cs.all(), List.of(j(0)), scorer);
    double[][] copy = graph.weights();
    copy[0][0] = 0;
    assertEquals(100, graph.weights()[0][0], 1e-9);
  }

  @Test
  void greedyCoverAndImpossibleSkill() {
    var g = new GreedySetCover();
    var people =
        List.of(c("B", 0, "java", "python"), c("A", 0, "java", "sql"), c("C", 0, "python"));
    var t = g.cover(Set.of("java", "sql", "python"), people);
    assertTrue(t.fullCoverage());
    assertEquals(List.of("A", "B"), t.members().stream().map(Candidate::id).toList());
    assertEquals(Set.of("python"), t.contributions().get("B"));
    var impossible = g.cover(Set.of("java", "quantum"), people);
    assertFalse(impossible.fullCoverage());
    assertEquals(Set.of("quantum"), impossible.uncoveredSkills());
    assertTrue(g.cover(Set.of(), people).members().isEmpty());
    assertTrue(g.cover(Set.of(), List.of()).fullCoverage());
    assertFalse(g.cover(Set.of("java"), List.of()).fullCoverage());
  }

  @Test
  void structuredParsers() {
    assertEquals(
        Set.of("javascript"),
        new ResumeParser().parse("A|Ada|JS, javascript|1|Bachelor's").skills());
    assertTrue(new JobDescriptionParser().parse("J|Job||0|Diploma").requiredSkills().isEmpty());
    assertThrows(IllegalArgumentException.class, () -> new ResumeParser().parse("bad"));
    assertThrows(IllegalArgumentException.class, () -> Qualification.parse("unknown"));
  }
}
