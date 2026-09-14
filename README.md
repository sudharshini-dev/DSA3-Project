# Résumé–Job Matching & Talent Marketplace Engine

A complete university DSA web application built around the existing Java matching engine. The browser uses server-rendered pages; the real Java services perform every ranking, recommendation, assignment, and team calculation.

## Technology stack

- Java 17+ (verified with Oracle JDK 25; Maven compiles to Java 17)
- Spring Boot 3.5.16, Spring MVC, embedded Tomcat
- Thymeleaf templates and Bootstrap 5.3.8
- Jakarta Bean Validation and Spring Security CSRF protection
- Maven, JUnit 5, and Spring MockMvc
- In-memory HashMaps, HashSets, inverted index, PriorityQueue, and weighted bipartite graph

Bootstrap CSS and JavaScript are included in `static/vendor/bootstrap`, so the UI does not need a CDN connection. There is no Node/React application and no external database. Eight candidates and five jobs load automatically.

## Problem and objectives

Recruiters need explainable candidate rankings; candidates need suitable job recommendations and skill-gap information. A talent marketplace also needs optimal one-to-one allocation and small teams that collectively cover project requirements.

The application demonstrates normalization, indexed retrieval, weighted scoring, max-heap ranking, sorting, exact Hungarian assignment, and approximate greedy set cover through an accessible website.

## Run the website

From the `talent-matcher` directory, with JDK 17+ and Maven available:

```shell
mvn clean test
mvn spring-boot:run
```

Open **http://localhost:8080**. Stop the server with Ctrl+C. Port 8080 must be free.

On this Windows workspace, a local Maven distribution is already installed:

```powershell
cd "C:\Users\haasi\OneDrive\Documents\ChatGPT\PROJECTS\talent-matcher"
.\mvn-local.cmd clean test
.\mvn-local.cmd spring-boot:run
```

Dependencies need internet access on the first build. The local runner keeps its Maven cache inside the ignored `.tools` directory. It is a convenience runner for this workspace, not a portable Maven Wrapper; on a fresh copy install Maven and use the standard commands.

The original terminal menu is still available:

```shell
mvn exec:java
```

The console and website have independent in-memory data. No persistence is implied.

## Pages and features

| URL | Features |
| --- | --- |
| `/` | Dashboard with candidate/job counts, unique indexed skills, and mean compatibility across every candidate–job pair |
| `/candidates` | Candidate table with skills, experience, qualification, certifications, View and Edit actions |
| `/candidates/add` | Add a validated candidate; generated ID and immediate index update |
| `/candidates/{id}` | Profile details and expandable delete action |
| `/candidates/{id}/edit` | Edit profile and refresh skill postings |
| `/jobs` | Job table |
| `/jobs/add` | Add a job with skills, experience and qualification requirements |
| `/ranking` | Select job, top-N results, optional indexed shared-skill filtering; progress bars and expandable explanations |
| `/recommendations` | Select candidate and rank jobs with matched/missing skills and explanations |
| `/skill-index` | Real skill-to-candidate posting sets with links to candidate profiles |
| `/assignment` | Generate exact Hungarian assignment and show weighted bipartite pairs, total score, unmatched candidates and jobs |
| `/team` | Enter project skills; show greedy team, newly contributed skills and covered/uncovered requirements |

All state-changing operations use POST and CSRF-protected forms. Add/edit forms preserve entries and display validation errors. Desktop uses a left sidebar; mobile uses collapsible navigation. Tables scroll inside their containers on small screens.

## Website architecture

```text
Browser / Bootstrap
       ↓ HTTP form submission
Spring MVC controllers + validated form objects
       ↓
Existing Java services
       ↓
HashMap + inverted index / scorer + PriorityQueue
HungarianAlgorithm / GreedySetCover
       ↓ immutable result records
Thymeleaf templates
       ↓
Explained scores, allocation rows, skill badges, team cards
```

Controllers contain routing, validation and model preparation. They do not implement replacement ranking or assignment logic. `CoreConfiguration` registers the original services as Spring beans and loads the original sample data. `TalentMatcherApplication` is the web entry point; `Main` remains the console entry point.

| New class / group | Role |
| --- | --- |
| HomeController | Counts data and averages scores from CompatibilityScorer |
| CandidateController / JobController | Validated profile and job operations |
| RankingController | Calls MatchingService.rank → existing scorer and max PriorityQueue |
| RecommendationController | Calls RecommendationService.recommend → existing sorting |
| SkillIndexController | Displays CandidateService.skillIndex; resolves candidate names |
| AssignmentController | Calls MatchingService.assign → CompatibilityGraph → existing HungarianAlgorithm |
| TeamController | Normalizes form skills and calls TeamFormationService → existing GreedySetCover |
| CandidateForm / JobForm / SelectionForm / TeamForm | Bound form values with server-side validation |
| PageAdvice / WebDisplay | Shared navigation data and presentation-only helpers |
| WebSecurityConfiguration | No login for this local demo; CSRF protection remains enabled |
| CoreConfiguration | Dependency wiring and startup sample loader |

Core additions are limited to certifications on Candidate (with a backwards-compatible constructor), generated IDs, candidate deletion, and synchronized store/index operations for web requests. Candidate changes refresh the same inverted index used by ranking. The three files in `algorithm/` are byte-for-byte unchanged.

A single lookup/update is synchronized; a whole multi-service page calculation is not a database transaction. This is intended for small demonstration datasets.

## Architecture and class-by-class DSA map

The website and menu call services. Services manage model objects and delegate to indexes and algorithms. Structured parsers feed normalized records into the services. Models defensively copy skill collections to protect the index from external mutation.

| Package / class | Responsibility and DSA concept |
| --- | --- |
| model/Candidate, model/Job | Validated records; HashSet skills remove duplicates and support membership checks |
| model/Qualification | Ordered enum: High School < Diploma < Bachelor's < Master's < PhD |
| model/MatchResult | Component scores, set intersection/difference results, explanation |
| model/AssignmentResult | Assignment list, summed edge weight, unmatched vertices |
| model/TeamResult | Ordered selection list, candidate-to-contribution map, covered/uncovered sets |
| model/CompatibilityGraph | Complete weighted bipartite graph represented as a candidate-by-job adjacency matrix |
| normalization/SkillNormalizer | HashMap alias lookup; HashSet deduplication; case, hyphen and whitespace normalization |
| index/InvertedSkillIndex | HashMap from skill to HashSet candidate IDs; union of posting sets retrieves relevant candidates |
| index/CandidateIndex | HashMap ID lookup; synchronizes posting sets on insert/replacement; sorts IDs for deterministic results |
| service/CandidateService | Candidate create/save/get/list/delete, delegating lookup and indexed retrieval |
| service/JobService | HashMap ID lookup and sorted job listing |
| algorithm/CompatibilityScorer | HashSet membership, intersection and difference; weighted arithmetic |
| service/MatchingService | Max PriorityQueue ranking; constructs graph and invokes Hungarian assignment |
| service/RecommendationService | Reverse scoring and comparator sorting, then top-N selection |
| algorithm/HungarianAlgorithm | Exact minimum-cost assignment through potentials and augmenting paths, converted from maximum weight |
| algorithm/GreedySetCover | Repeated maximum uncovered-skill gain; HashSet intersections/differences |
| service/TeamFormationService | Uses index to shortlist candidates and invokes set cover |
| parser/ResumeParser, parser/JobDescriptionParser | Delimiter-based structured parsing; skills become HashSets |
| data/SampleDataLoader | Loads eight candidates, five jobs, and a six-skill project |
| util/ConsolePrinter | Deterministic sorted skill presentation and explainable output |
| Main | Console orchestration and input validation |

Parsers accept `id|name-or-title|skill,skill|years|qualification`. They deliberately parse structured plain text, not arbitrary PDF resumes. Unknown qualifications are rejected. Aliases include ML → machine learning, JS → javascript, Spring → spring boot, DB → sql. `addAlias` extends an individual normalizer; custom aliases should be applied before constructing records and before querying the index. Alias targets should be canonical names; alias chains are not recursively expanded.

## Algorithms and policies

### Compatibility and skill gaps

Skill score is 100 × matched / required. An empty required-skill set scores 100 because no skill requirement is unmet. A candidate with no skills scores zero when the job requires skills.

Experience score is 100 when the requirement is zero or met; otherwise it is 100 × candidate years / required years. Qualification is 100 if the ordered level is met/exceeded, otherwise zero.

`final = 0.70 × skills + 0.20 × experience + 0.10 × qualification`

Weights can be configured through the scorer constructor; they must be finite, nonnegative and sum to one. Experience and qualifications affect compatibility but are not hard eligibility filters. Every match reports normalized matched and missing skills, all components, and the actual weighted calculation.

### Indexed ranking and recommendations

Indexed mode unions postings for required skills and scores only candidates with at least one shared skill. Full mode ranks everyone, including candidates with zero overlap who can still receive experience/qualification points. Empty-skill jobs consider all candidates in either mode. Candidate ranking inserts results into a max heap and removes the best N; ties use ascending candidate ID. Job recommendations sort by descending score and ascending job ID. No minimum recommendation threshold is imposed.

### Optimal assignment

The complete bipartite graph contains one vertex per candidate and one per job; each edge is the compatibility score. For N = max(candidate count, job count), the Hungarian algorithm pads the matrix to N × N with zero-weight dummy vertices. It converts weight to cost using `maxWeight - weight`, then maintains row/column potentials, reduced costs, and augmenting paths until every padded row is assigned.

Discarding dummy assignments produces min(candidate count, job count) real assignments with maximum total compatibility. All real edges are allowed, including zero-score edges. Each candidate/job appears at most once. Empty sides return unmatched vertices cleanly. This is an exact algorithm, not greedy matching.

### Team formation

Normalize requirements, retrieve relevant candidates, then repeatedly select the candidate with the largest intersection with uncovered skills. Remove newly covered skills and repeat until complete or no gain is possible. Ties use ascending candidate ID. Contributions report the **new** skills covered at selection time; full candidate skills remain available on the candidate record.

Greedy set cover approximates minimum team size; it does not guarantee the smallest possible team. For a coverable requirement of R skills it has the standard harmonic H(R) approximation bound. Impossible skills are returned explicitly, and an empty requirement produces an empty, fully covering team.

## Time and space complexity

Let C be candidates, J jobs, S candidate skills, R required skills, P total posting IDs visited, K retrieved unique candidates, T selected team size, and N = max(C,J). Hash operations are average-case, excluding string-processing cost.

| Operation | Time | Extra space / notes |
| --- | --- | --- |
| HashMap ID lookup | O(1) average | O(C) candidate store / O(J) job store |
| HashSet membership | O(1) average | O(S) skill storage |
| Normalize skills | O(total input characters) | Canonical strings + unique skills |
| Inverted index retrieval | O(R + P) for union | O(K); CandidateIndex additionally sorts K IDs in O(K log K) |
| Index insert / replace | O(S old + S new) | Total posting storage O(sum of candidate skill counts) |
| PriorityQueue insert / remove | O(log K) each | O(K) heap |
| Compatibility calculation | O(S + R) | O(S + R), including defensive skill copies |
| Top-N candidate ranking | O(K(S + R) + K log K + min(topN,K) log K) | O(KR) retained explanations/skill sets plus heap |
| Job recommendations | O(J(S + R) + J log J) | O(JR) retained match details |
| Bipartite graph construction | O(CJ(S + R)) | O(CJ) matrix |
| Hungarian algorithm | O(N³) | O(N²) padded matrix, O(N) working arrays |
| Greedy set cover | O(C log C + T × C × (S + R)) upper bound | Skill sets and results; T ≤ min(C,R) for nonempty requirements |

Hash lookup bounds assume normal hash distribution. Actual matching costs include copies intentionally used for model safety. The graph stores weights; match explanations are recomputed only for chosen assignment edges.


## Demo workflow

1. Open the dashboard: 8 candidates, 5 jobs, 15 normalized skills, average compatibility 42.9%.
2. Rank Java Backend Developer (J1): Arjun is first at 100%; Haasini has 82.5%.
3. Expand Haasini's “Why this score?”: 3 of 4 skills, full experience/qualification scores, missing spring boot.
4. Add a candidate with `JS, JavaScript, Python` and an optional certification. Open Skill Index to see deduplicated postings.
5. Edit the candidate's skills and see the postings refresh. Delete via the profile's expandable delete section if desired.
6. Add a job and select it on Ranking; recommend jobs for the new candidate.
7. Generate Optimal Assignment. With the untouched sample dataset the total is 500.0.
8. Form the default project team. Arjun contributes AWS/Docker/Java/SQL; Nikhil adds Cybersecurity/Python.
9. Add `Quantum` to requirements to demonstrate unavailable skill reporting.
10. Restart to restore the original sample dataset.

Certifications are displayed but do not change the original scoring formula. Web forms limit text lengths, experience to 0–80, and result limits to 1–100. Core APIs retain their original edge-case policies (including top N = 0).

## Screenshots

Capture these pages for your university report:

| Screenshot placeholder | Suggested content |
| --- | --- |
| Dashboard — desktop | Summary cards, sidebar, recruitment overview |
| Ranking | J1 results with Haasini's explanation expanded |
| Skill Index | Real normalized skill posting sets |
| Assignment | Hungarian labels and 500.0 total |
| Team Formation | Two-member sample team with full coverage |
| Mobile | Collapsible navigation and stacked cards at phone width |

## Tests and verification

`mvn clean test` runs **20 tests**: 12 preserved core tests and 8 full Spring MVC integration tests. The core suite includes 240 seeded Hungarian matrices checked against exhaustive permutation search.

The web tests render actual Thymeleaf templates with real services. They cover all GET pages and local assets, candidate creation/edit/deletion and index updates, job creation, ranking/recommendation equality with service results, exact assignment, possible/impossible/empty team requirements, validation errors, missing records, CSRF and HTML escaping, and an empty candidate pool.

Live browser checks exercised dashboard, candidate/job creation, skill-index updates, ranking and explanation expansion, recommendations, assignment and team formation. Desktop and 390px phone layouts and the mobile menu were visually checked. See `VERIFICATION.md` and `web-test-output.txt`.

## Limitations and future enhancements

- In-memory data resets on restart; no database, accounts, upload processing or persistent storage.
- Server binds to 127.0.0.1 for a local demonstration. Authentication and deployment configuration are future work.
- Jobs support add/list. Candidate add/view/edit/delete is implemented.
- Certifications are informational and do not affect scores.
- Hungarian assignment is one-to-one with all compatibility edges allowed, not a hard eligibility filter.
- Greedy set cover approximates minimum team size and can leave unachievable skills uncovered.
- No pagination or expensive-computation limits beyond form/result bounds; use small university-demo datasets.
- A Java 17 runtime was not separately available for execution testing; compilation targets release 17.

Possible next steps: optional persistence, job editing, import/export, reviewed PDF extraction, configurable filters and weights, job capacities, and an exact set-cover comparison for small inputs.

## Dependency references

[Spring Boot system requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html) · [Bootstrap distribution](https://getbootstrap.com/docs/5.3/getting-started/download/)

See `FILES.md` for the complete file manifest and the files added or modified in the web conversion.
