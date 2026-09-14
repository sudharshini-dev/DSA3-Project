# Web conversion verification

Verified on 11 September 2026 using Java 25, Maven 3.9.9, and Spring Boot 3.5.16. Maven compiles all application classes with `release 17`.

## Final clean build

Executed from this project:

```powershell
.\.tools\apache-maven-3.9.9\bin\mvn.cmd -B -Dmaven.repo.local=.tools/repository clean test
```

**BUILD SUCCESS — 20 tests, 0 failures, 0 errors, 0 skipped.**

- 12 existing EngineTest tests preserved, including 240 exhaustive-oracle Hungarian comparisons.
- 8 WebApplicationTest integration tests use real services and render Thymeleaf.
- Final clean build completed at 12:35 IST, after formatting the Java changes.

An initial shared navigation-expression error was detected by the web tests and fixed. All templates subsequently resolved correctly.

## Live server and browser checks

Started with Maven `spring-boot:run`. Embedded Tomcat started on port 8080 without application startup errors.

| Check | Result |
| --- | --- |
| Dashboard | 8 candidates, 5 jobs, 15 indexed skills, 42.9% mean compatibility |
| Candidate list | All sample profiles render |
| Add candidate | Created a test profile with experience and certification through the browser |
| Normalization | JS and JavaScript deduplicated to javascript |
| Skill index | New browsercheck posting appeared and linked to the test candidate |
| Add job | Created a test job through the browser; job table updated |
| Ranking | Arjun 100.0%; Haasini 82.5% for J1 |
| Explainability | Expanded Haasini's explanation: 3/4 skills and weighted total 82.50 |
| Recommendations | Newly created role recommended to the test candidate at 100.0% |
| Hungarian assignment | Five 100-point sample matches, total 500.0; three unassigned candidates |
| Greedy team | Arjun + Nikhil; full coverage of six sample skills |
| Desktop | Visually inspected dashboard styling and layout |
| Mobile | Visually inspected 390 × 844 layout; mobile navigation opens correctly |

After the checks, the app was restarted with the original sample data to remove temporary browser-test records. The final server is bound to 127.0.0.1:8080. The preview returns to the dashboard.

## Integration test details

The web suite checks every requested page, add/detail/edit forms, and all local Bootstrap/style assets. It verifies candidate add/edit/delete and posting updates, new jobs, rendered rankings/recommendations equal to service results, assignment equal to MatchingService.assign, and teams equal to TeamFormationService.form.

Additional cases: negative and malformed input, missing IDs, HTML escaping, rejected POST without CSRF, impossible and empty team requirements, and empty-candidate pages.

## Preservation evidence

Algorithm SHA-256 checksums before and after the web conversion are identical:

| File | SHA-256 |
| --- | --- |
| CompatibilityScorer.java | 7BEC1765B749BD114B021579D62D53C9549846F2D2569DB1596CA7C5C3742301 |
| GreedySetCover.java | A24E24EAB80E7C9EB272233E077BAA4C8A3EEAC15B048160B737D87C2632FB20 |
| HungarianAlgorithm.java | ABD80A19D0F5F80FD83765A27E6A15085F2B1A96348C6E914B13CFF1A99A4C79 |

MatchingService, RecommendationService, TeamFormationService and EngineTest were also preserved. Changes to CandidateService, JobService and CandidateIndex add web data operations; they do not replace the DSA implementation.

## Logs and practical limits

- `web-test-output.txt`: final clean build output.
- `spring-boot-output.txt`: current startup log.
- `spring-boot-error.txt`: Maven/JDK library warnings.
- `target/surefire-reports/`: generated per-suite reports.
- Earlier console verification remains in `demo-output.txt` and `test-output.txt`.

Java 25 emits native-access and deprecated-Unsafe warnings from build dependencies; these do not prevent startup or tests. Java 17 compatibility is checked through compilation, not a separate Java 17 runtime. Data is in memory, certifications are informational, jobs support add/list, and greedy team formation is approximate.
