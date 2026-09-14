package com.talentmatcher.controller;

import com.talentmatcher.model.Qualification;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class PageAdvice {
  @ModelAttribute("currentPath")
  String currentPath(HttpServletRequest request) {
    return request.getRequestURI();
  }

  @ModelAttribute("qualifications")
  Qualification[] qualifications() {
    return Qualification.values();
  }
}
