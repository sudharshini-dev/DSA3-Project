package com.talentmatcher;

import com.talentmatcher.algorithm.*;
import com.talentmatcher.data.SampleDataLoader;
import com.talentmatcher.service.*;
import com.talentmatcher.util.ConsolePrinter;
import java.util.*;

public final class Main {
  public static void main(String[] args) {
    CandidateService candidates = new CandidateService();
    JobService jobs = new JobService();
    SampleDataLoader.load(candidates, jobs);
    CompatibilityScorer scorer = new CompatibilityScorer();
    MatchingService matching = new MatchingService(candidates, scorer);
    RecommendationService recommendations = new RecommendationService(jobs, scorer);
    TeamFormationService teams = new TeamFormationService(candidates);
    Scanner input = new Scanner(System.in);
    System.out.println("Resume-Job Matching and Talent-Marketplace Engine");
    while (true) {
      System.out.println(
          "\n"
              + "1. Display all candidates\n"
              + "2. Display all jobs\n"
              + "3. Show inverted skill index\n"
              + "4. Rank candidates for a job\n"
              + "5. Recommend jobs for a candidate\n"
              + "6. Show optimal candidate-job assignment\n"
              + "7. Form minimum team for project (greedy approximation)\n"
              + "8. Exit");
      System.out.print("Choice: ");
      if (!input.hasNextLine()) return;
      try {
        switch (input.nextLine().trim()) {
          case "1" -> candidates.all().forEach(ConsolePrinter::candidate);
          case "2" -> jobs.all().forEach(ConsolePrinter::job);
          case "3" ->
              candidates.skillIndex().forEach((s, ids) -> System.out.println(s + " -> " + ids));
          case "4" -> {
            System.out.print("Job ID: ");
            String id = input.nextLine().trim();
            System.out.print("Top N: ");
            int n = Integer.parseInt(input.nextLine().trim());
            System.out.print("Shared-skill candidates only? (y/n): ");
            boolean indexed = input.nextLine().trim().equalsIgnoreCase("y");
            var results = matching.rank(jobs.get(id), n, indexed);
            if (results.isEmpty()) System.out.println("No candidates found.");
            else results.forEach(ConsolePrinter::match);
          }
          case "5" -> {
            System.out.print("Candidate ID: ");
            String id = input.nextLine().trim();
            System.out.print("Top N: ");
            int n = Integer.parseInt(input.nextLine().trim());
            var results = recommendations.recommend(candidates.get(id), n);
            if (results.isEmpty()) System.out.println("No jobs found.");
            else results.forEach(ConsolePrinter::match);
          }
          case "6" -> ConsolePrinter.assignment(matching.assign(jobs.all()));
          case "7" -> {
            System.out.print("Comma-separated project skills (Enter for sample): ");
            String line = input.nextLine();
            Set<String> required =
                line.isBlank()
                    ? SampleDataLoader.PROJECT_SKILLS
                    : new HashSet<>(Arrays.asList(line.split(",")));
            ConsolePrinter.team(teams.form(required));
          }
          case "8" -> {
            System.out.println("Goodbye.");
            return;
          }
          default -> System.out.println("Choose a number from 1 to 8.");
        }
      } catch (IllegalArgumentException ex) {
        System.out.println("Input error: " + ex.getMessage());
      } catch (NoSuchElementException ex) {
        return;
      }
    }
  }
}
