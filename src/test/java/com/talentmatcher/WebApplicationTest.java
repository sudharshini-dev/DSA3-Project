package com.talentmatcher;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.talentmatcher.algorithm.CompatibilityScorer;
import com.talentmatcher.data.SampleDataLoader;
import com.talentmatcher.model.*;
import com.talentmatcher.service.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/** Full MVC tests render real Thymeleaf templates and call the real DSA service beans. */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WebApplicationTest {
  @Autowired MockMvc mvc;
  @Autowired CandidateService candidates;
  @Autowired JobService jobs;
  @Autowired MatchingService matching;
  @Autowired RecommendationService recommendations;
  @Autowired TeamFormationService teams;
  @Autowired CompatibilityScorer scorer;

  @Test
  void everyPageRendersAndAssetsAreLocal() throws Exception {
    String[][] pages = {
      {"/", "Résumé–Job Matching"},
      {"/candidates", "Haasini"},
      {"/candidates/add", "Full name"},
      {"/candidates/C1", "Candidate details"},
      {"/candidates/C1/edit", "Edit Candidate"},
      {"/jobs", "Java Backend Developer"},
      {"/jobs/add", "Role details"},
      {"/ranking", "Max PriorityQueue"},
      {"/recommendations", "Find Suitable Jobs"},
      {"/assignment", "Hungarian Algorithm"},
      {"/team", "Greedy Set Cover"},
      {"/skill-index", "Inverted Index"}
    };
    for (String[] page : pages)
      mvc.perform(get(page[0]))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString(page[1])));
    mvc.perform(get("/css/style.css")).andExpect(status().isOk());
    mvc.perform(get("/vendor/bootstrap/bootstrap.min.css")).andExpect(status().isOk());
    mvc.perform(get("/vendor/bootstrap/bootstrap.bundle.min.js")).andExpect(status().isOk());
    var model = mvc.perform(get("/")).andReturn().getModelAndView().getModel();
    assertEquals(8, model.get("candidateCount"));
    assertEquals(5, model.get("jobCount"));
    double average =
        candidates.all().stream()
            .flatMap(c -> jobs.all().stream().map(j -> scorer.score(c, j)))
            .mapToDouble(MatchResult::finalScore)
            .average()
            .orElse(0);
    assertEquals(average, (double) model.get("averageScore"), 1e-9);
  }

  @Test
  void candidateCreateEditDeleteUpdatesRealIndex() throws Exception {
    String location =
        mvc.perform(
                post("/candidates/add")
                    .with(csrf())
                    .param("name", "Web Candidate")
                    .param("skills", "JS, javascript, UniqueSkill")
                    .param("yearsOfExperience", "2.5")
                    .param("qualification", "BACHELORS")
                    .param("certifications", "AWS Certified, Java Certified"))
            .andExpect(status().is3xxRedirection())
            .andReturn()
            .getResponse()
            .getRedirectedUrl();
    String id = location.substring(location.lastIndexOf('/') + 1);
    Candidate candidate = candidates.get(id);
    assertEquals(Set.of("javascript", "uniqueskill"), candidate.skills());
    assertEquals(Set.of("AWS Certified", "Java Certified"), candidate.certifications());
    assertTrue(candidates.skillIndex().get("uniqueskill").contains(id));
    mvc.perform(get(location))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("AWS Certified")));
    mvc.perform(get("/skill-index")).andExpect(content().string(containsString("Web Candidate")));
    mvc.perform(
            post(location + "/edit")
                .with(csrf())
                .param("name", "Updated Candidate")
                .param("skills", "Python")
                .param("yearsOfExperience", "3")
                .param("qualification", "MASTERS")
                .param("certifications", "Updated Certification"))
        .andExpect(status().is3xxRedirection());
    assertFalse(candidates.skillIndex().containsKey("uniqueskill"));
    assertEquals(Set.of("python"), candidates.get(id).skills());
    mvc.perform(post(location + "/delete").with(csrf())).andExpect(redirectedUrl("/candidates"));
    assertThrows(IllegalArgumentException.class, () -> candidates.get(id));
    assertFalse(candidates.skillIndex().get("python").contains(id));
  }

  @Test
  void jobsCanBeAddedAndMatchedImmediately() throws Exception {
    mvc.perform(
            post("/jobs/add")
                .with(csrf())
                .param("title", "Web Added Role")
                .param("requiredSkills", "Java, Spring")
                .param("minimumExperience", "0")
                .param("requiredQualification", "DIPLOMA"))
        .andExpect(redirectedUrl("/jobs"));
    Job job =
        jobs.all().stream()
            .filter(j -> j.title().equals("Web Added Role"))
            .findFirst()
            .orElseThrow();
    assertEquals(Set.of("java", "spring boot"), job.requiredSkills());
    mvc.perform(get("/jobs")).andExpect(content().string(containsString("Web Added Role")));
    mvc.perform(post("/ranking").with(csrf()).param("selectedId", job.id()).param("topN", "8"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Arjun")));
  }

  @Test
  void rankingsAndRecommendationsAreExistingServiceResults() throws Exception {
    var ranked =
        mvc.perform(
                post("/ranking")
                    .with(csrf())
                    .param("selectedId", "J1")
                    .param("topN", "8")
                    .param("sharedSkillsOnly", "true"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Why this score?")))
            .andReturn()
            .getModelAndView()
            .getModel()
            .get("results");
    assertEquals(matching.rank(jobs.get("J1"), 8, true), ranked);
    var full =
        mvc.perform(post("/ranking").with(csrf()).param("selectedId", "J1").param("topN", "8"))
            .andExpect(status().isOk())
            .andReturn()
            .getModelAndView()
            .getModel()
            .get("results");
    assertEquals(matching.rank(jobs.get("J1"), 8, false), full);
    var rec =
        mvc.perform(
                post("/recommendations").with(csrf()).param("selectedId", "C1").param("topN", "5"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("82.5")))
            .andReturn()
            .getModelAndView()
            .getModel()
            .get("results");
    assertEquals(recommendations.recommend(candidates.get("C1"), 5), rec);
  }

  @Test
  void assignmentAndTeamsRenderActualAlgorithmResults() throws Exception {
    var result =
        (AssignmentResult)
            mvc.perform(post("/assignment").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("500.0")))
                .andReturn()
                .getModelAndView()
                .getModel()
                .get("result");
    assertEquals(matching.assign(jobs.all()), result);
    assertEquals(500, result.totalScore(), 1e-9);
    var team =
        (TeamResult)
            mvc.perform(
                    post("/team")
                        .with(csrf())
                        .param("skills", String.join(",", SampleDataLoader.PROJECT_SKILLS)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Full Skill Coverage Achieved")))
                .andReturn()
                .getModelAndView()
                .getModel()
                .get("result");
    assertEquals(teams.form(SampleDataLoader.PROJECT_SKILLS), team);
    mvc.perform(post("/team").with(csrf()).param("skills", "Java,Quantum"))
        .andExpect(status().isOk())
        .andExpect(
            content().string(containsString("Full coverage is not possible. Missing skills:")))
        .andExpect(content().string(containsString("quantum")));
    mvc.perform(post("/team").with(csrf()).param("skills", ""))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("No team is needed")));
  }

  @Test
  void invalidFormsKeepUserInputAndDoNotMutateData() throws Exception {
    mvc.perform(
            post("/candidates/add")
                .with(csrf())
                .param("name", "")
                .param("yearsOfExperience", "-1")
                .param("qualification", "BACHELORS"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("candidateForm", "name", "yearsOfExperience"));
    mvc.perform(
            post("/jobs/add")
                .with(csrf())
                .param("title", "Unfinished role")
                .param("minimumExperience", "oops")
                .param("requiredQualification", "BACHELORS"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("jobForm", "minimumExperience"))
        .andExpect(content().string(containsString("Unfinished role")));
    mvc.perform(post("/ranking").with(csrf()).param("selectedId", "MISSING").param("topN", "2"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("selectionForm", "selectedId"));
    mvc.perform(post("/recommendations").with(csrf()).param("selectedId", "C1").param("topN", "-1"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("selectionForm", "topN"));
    mvc.perform(
            post("/recommendations").with(csrf()).param("selectedId", "MISSING").param("topN", "2"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("selectionForm", "selectedId"));
    mvc.perform(
            post("/candidates/C1/edit")
                .with(csrf())
                .param("name", "")
                .param("yearsOfExperience", "2")
                .param("qualification", "BACHELORS"))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("candidateForm", "name"));
    assertEquals(8, candidates.all().size());
    assertEquals(5, jobs.all().size());
    assertEquals("Haasini", candidates.get("C1").name());
  }

  @Test
  void csrfMissingRecordsAndEscaping() throws Exception {
    mvc.perform(post("/candidates/C1/delete")).andExpect(status().isForbidden());
    mvc.perform(get("/candidates/unknown")).andExpect(status().isNotFound());
    mvc.perform(get("/candidates/unknown/edit")).andExpect(status().isNotFound());
    candidates.create(
        "<script>alert(1)</script>", new HashSet<>(), 0, Qualification.BACHELORS, Set.of());
    String html =
        mvc.perform(get("/candidates"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertFalse(html.contains("<script>alert(1)</script>"));
    assertTrue(html.contains("&lt;script&gt;"));
  }

  @Test
  void emptyCandidatePoolIsHandledAcrossPages() throws Exception {
    candidates.all().forEach(c -> candidates.delete(c.id()));
    mvc.perform(get("/candidates"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Your talent pool is empty")));
    mvc.perform(get("/skill-index"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("No skills indexed")));
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("averageScore", 0.0));
    mvc.perform(post("/assignment").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Add at least one candidate")));
    mvc.perform(post("/ranking").with(csrf()).param("selectedId", "J1").param("topN", "10"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("No candidates found")));
  }
}
