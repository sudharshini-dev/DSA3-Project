package com.talentmatcher.data;

import com.talentmatcher.parser.*;
import com.talentmatcher.service.*;
import java.util.*;

public final class SampleDataLoader {
  private SampleDataLoader() {}

  public static final Set<String> PROJECT_SKILLS =
      Set.of("Java", "Python", "SQL", "AWS", "Docker", "Cybersecurity");

  public static void load(CandidateService candidates, JobService jobs) {
    ResumeParser cp = new ResumeParser();
    JobDescriptionParser jp = new JobDescriptionParser();
    List.of(
            "C1|Haasini|Java,SQL,Git,Data Structures|2|Bachelors",
            "C2|Arjun|Java,Spring,SQL,Docker,AWS,Git|5|Masters",
            "C3|Meera|Python,ML,TensorFlow,SQL|3|Masters",
            "C4|Ravi|JS,React,Git|1|Diploma",
            "C5|Sara|AWS,Docker,Linux,Networking,Python|4|Bachelors",
            "C6|Nikhil|Cybersecurity,Linux,Networking,Python|6|PhD",
            "C7|Ananya|Java,Python,SQL,React,Data Structures|0|Bachelors",
            "C8|Dev|Linux,Git|0.5|High School")
        .forEach(s -> candidates.save(cp.parse(s)));
    List.of(
            "J1|Java Backend Developer|Java,SQL,Git,Spring Boot|2|Bachelors",
            "J2|Machine Learning Engineer|Python,Machine-Learning,TensorFlow,SQL|3|Masters",
            "J3|Frontend Developer|JavaScript,React,Git|1|Diploma",
            "J4|Cloud Engineer|AWS,Docker,Linux,Python|4|Bachelors",
            "J5|Security Analyst|Cybersecurity,Linux,Networking|2|Bachelors")
        .forEach(s -> jobs.save(jp.parse(s)));
  }
}
