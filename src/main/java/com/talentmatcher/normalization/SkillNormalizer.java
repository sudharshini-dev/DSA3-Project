package com.talentmatcher.normalization;

import java.util.*;

/** HashMap aliases provide average O(1) canonical-name lookup. */
public final class SkillNormalizer {
  private final HashMap<String, String> aliases = new HashMap<>();

  public SkillNormalizer() {
    addAlias("ML", "machine learning");
    addAlias("JS", "javascript");
    addAlias("Spring", "spring boot");
    addAlias("DB", "sql");
  }

  private static String clean(String s) {
    return Objects.requireNonNull(s, "skill")
        .toLowerCase(Locale.ROOT)
        .trim()
        .replace('-', ' ')
        .replaceAll("\\s+", " ");
  }

  public void addAlias(String alias, String canonical) {
    String a = clean(alias), c = clean(canonical);
    if (a.isEmpty() || c.isEmpty()) throw new IllegalArgumentException("Empty alias");
    aliases.put(a, c);
  }

  public String normalize(String skill) {
    String s = clean(skill);
    return aliases.getOrDefault(s, s);
  }

  public HashSet<String> normalizeAll(Collection<String> skills) {
    HashSet<String> result = new HashSet<>();
    for (String s : skills) {
      String n = normalize(s);
      if (!n.isEmpty()) result.add(n);
    }
    return result;
  }
}
