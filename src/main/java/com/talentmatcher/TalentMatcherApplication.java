package com.talentmatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/** Web entry point. Main remains the independent console entry point. */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class TalentMatcherApplication {
  public static void main(String[] args) {
    SpringApplication.run(TalentMatcherApplication.class, args);
  }
}
