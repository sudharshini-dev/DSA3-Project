# Project files and web conversion changes

## Modified existing files

- `pom.xml`
- `src/main/java/com/talentmatcher/model/Candidate.java`
- `src/main/java/com/talentmatcher/index/CandidateIndex.java`
- `src/main/java/com/talentmatcher/service/CandidateService.java`
- `src/main/java/com/talentmatcher/service/JobService.java`
- `README.md`
- `FILES.md`
- `VERIFICATION.md`

Changes add Spring Boot wiring, candidate certifications, generated IDs, synchronized store access, candidate deletion with posting cleanup, and web documentation. Existing algorithm files, matching/recommendation/team services and core tests are preserved.

## New files

- `spring-boot-error.txt`
- `spring-boot-output.txt`
- `src/main/java/com/talentmatcher/config/CoreConfiguration.java`
- `src/main/java/com/talentmatcher/config/WebSecurityConfiguration.java`
- `src/main/java/com/talentmatcher/controller/AssignmentController.java`
- `src/main/java/com/talentmatcher/controller/CandidateController.java`
- `src/main/java/com/talentmatcher/controller/HomeController.java`
- `src/main/java/com/talentmatcher/controller/JobController.java`
- `src/main/java/com/talentmatcher/controller/PageAdvice.java`
- `src/main/java/com/talentmatcher/controller/RankingController.java`
- `src/main/java/com/talentmatcher/controller/RecommendationController.java`
- `src/main/java/com/talentmatcher/controller/SkillIndexController.java`
- `src/main/java/com/talentmatcher/controller/TeamController.java`
- `src/main/java/com/talentmatcher/form/CandidateForm.java`
- `src/main/java/com/talentmatcher/form/JobForm.java`
- `src/main/java/com/talentmatcher/form/SelectionForm.java`
- `src/main/java/com/talentmatcher/form/TeamForm.java`
- `src/main/java/com/talentmatcher/TalentMatcherApplication.java`
- `src/main/java/com/talentmatcher/util/WebDisplay.java`
- `src/main/resources/application.properties`
- `src/main/resources/static/css/style.css`
- `src/main/resources/static/vendor/bootstrap/bootstrap.bundle.min.js`
- `src/main/resources/static/vendor/bootstrap/bootstrap.min.css`
- `src/main/resources/static/vendor/bootstrap/LICENSE`
- `src/main/resources/templates/assignment.html`
- `src/main/resources/templates/candidate-detail.html`
- `src/main/resources/templates/candidate-form.html`
- `src/main/resources/templates/candidates.html`
- `src/main/resources/templates/error.html`
- `src/main/resources/templates/fragments/layout.html`
- `src/main/resources/templates/fragments/navbar.html`
- `src/main/resources/templates/fragments/sidebar.html`
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/job-form.html`
- `src/main/resources/templates/jobs.html`
- `src/main/resources/templates/ranking.html`
- `src/main/resources/templates/recommendations.html`
- `src/main/resources/templates/skill-index.html`
- `src/main/resources/templates/team.html`
- `src/test/java/com/talentmatcher/WebApplicationTest.java`
- `web-test-output.txt`

## Complete source/resource inventory

- `src/main/java/com/talentmatcher/algorithm/CompatibilityScorer.java`
- `src/main/java/com/talentmatcher/algorithm/GreedySetCover.java`
- `src/main/java/com/talentmatcher/algorithm/HungarianAlgorithm.java`
- `src/main/java/com/talentmatcher/config/CoreConfiguration.java`
- `src/main/java/com/talentmatcher/config/WebSecurityConfiguration.java`
- `src/main/java/com/talentmatcher/controller/AssignmentController.java`
- `src/main/java/com/talentmatcher/controller/CandidateController.java`
- `src/main/java/com/talentmatcher/controller/HomeController.java`
- `src/main/java/com/talentmatcher/controller/JobController.java`
- `src/main/java/com/talentmatcher/controller/PageAdvice.java`
- `src/main/java/com/talentmatcher/controller/RankingController.java`
- `src/main/java/com/talentmatcher/controller/RecommendationController.java`
- `src/main/java/com/talentmatcher/controller/SkillIndexController.java`
- `src/main/java/com/talentmatcher/controller/TeamController.java`
- `src/main/java/com/talentmatcher/data/SampleDataLoader.java`
- `src/main/java/com/talentmatcher/form/CandidateForm.java`
- `src/main/java/com/talentmatcher/form/JobForm.java`
- `src/main/java/com/talentmatcher/form/SelectionForm.java`
- `src/main/java/com/talentmatcher/form/TeamForm.java`
- `src/main/java/com/talentmatcher/index/CandidateIndex.java`
- `src/main/java/com/talentmatcher/index/InvertedSkillIndex.java`
- `src/main/java/com/talentmatcher/Main.java`
- `src/main/java/com/talentmatcher/model/AssignmentResult.java`
- `src/main/java/com/talentmatcher/model/Candidate.java`
- `src/main/java/com/talentmatcher/model/CompatibilityGraph.java`
- `src/main/java/com/talentmatcher/model/Job.java`
- `src/main/java/com/talentmatcher/model/MatchResult.java`
- `src/main/java/com/talentmatcher/model/Qualification.java`
- `src/main/java/com/talentmatcher/model/TeamResult.java`
- `src/main/java/com/talentmatcher/normalization/SkillNormalizer.java`
- `src/main/java/com/talentmatcher/parser/JobDescriptionParser.java`
- `src/main/java/com/talentmatcher/parser/ResumeParser.java`
- `src/main/java/com/talentmatcher/service/CandidateService.java`
- `src/main/java/com/talentmatcher/service/JobService.java`
- `src/main/java/com/talentmatcher/service/MatchingService.java`
- `src/main/java/com/talentmatcher/service/RecommendationService.java`
- `src/main/java/com/talentmatcher/service/TeamFormationService.java`
- `src/main/java/com/talentmatcher/TalentMatcherApplication.java`
- `src/main/java/com/talentmatcher/util/ConsolePrinter.java`
- `src/main/java/com/talentmatcher/util/WebDisplay.java`
- `src/main/resources/application.properties`
- `src/main/resources/static/css/style.css`
- `src/main/resources/static/vendor/bootstrap/bootstrap.bundle.min.js`
- `src/main/resources/static/vendor/bootstrap/bootstrap.min.css`
- `src/main/resources/static/vendor/bootstrap/LICENSE`
- `src/main/resources/templates/assignment.html`
- `src/main/resources/templates/candidate-detail.html`
- `src/main/resources/templates/candidate-form.html`
- `src/main/resources/templates/candidates.html`
- `src/main/resources/templates/error.html`
- `src/main/resources/templates/fragments/layout.html`
- `src/main/resources/templates/fragments/navbar.html`
- `src/main/resources/templates/fragments/sidebar.html`
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/job-form.html`
- `src/main/resources/templates/jobs.html`
- `src/main/resources/templates/ranking.html`
- `src/main/resources/templates/recommendations.html`
- `src/main/resources/templates/skill-index.html`
- `src/main/resources/templates/team.html`
- `src/test/java/com/talentmatcher/EngineTest.java`
- `src/test/java/com/talentmatcher/WebApplicationTest.java`

The existing Maven runner, .gitignore, console output logs, and ignored .tools toolchain remain available. Build products are generated under target/.
