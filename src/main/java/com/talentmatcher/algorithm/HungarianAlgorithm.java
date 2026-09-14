package com.talentmatcher.algorithm;

import java.util.*;

/** Exact maximum-weight assignment using Hungarian potentials and augmenting paths. */
public final class HungarianAlgorithm {
  /** Returns column for each real row, or -1 when assigned to a dummy column. */
  public int[] maximize(double[][] weights) {
    Objects.requireNonNull(weights);
    int rows = weights.length;
    if (rows == 0) return new int[0];
    int cols = Objects.requireNonNull(weights[0]).length, n = Math.max(rows, cols);
    double max = 0;
    for (double[] row : weights) {
      if (row == null || row.length != cols) throw new IllegalArgumentException("Ragged matrix");
      for (double w : row) {
        if (!Double.isFinite(w)) throw new IllegalArgumentException("Non-finite weight");
        max = Math.max(max, w);
      }
    }
    // Dummy vertices carry zero weight; max-weight becomes min-cost via max - weight.
    double[][] cost = new double[n + 1][n + 1];
    for (int i = 1; i <= n; i++)
      for (int j = 1; j <= n; j++)
        cost[i][j] = max - (i <= rows && j <= cols ? weights[i - 1][j - 1] : 0);
    double[] u = new double[n + 1], v = new double[n + 1];
    int[] p = new int[n + 1], way = new int[n + 1];
    for (int i = 1; i <= n; i++) {
      p[0] = i;
      int j0 = 0;
      double[] minv = new double[n + 1];
      Arrays.fill(minv, Double.POSITIVE_INFINITY);
      boolean[] used = new boolean[n + 1];
      do {
        used[j0] = true;
        int i0 = p[j0], j1 = 0;
        double delta = Double.POSITIVE_INFINITY;
        for (int j = 1; j <= n; j++)
          if (!used[j]) {
            double cur = cost[i0][j] - u[i0] - v[j];
            if (cur < minv[j]) {
              minv[j] = cur;
              way[j] = j0;
            }
            if (minv[j] < delta) {
              delta = minv[j];
              j1 = j;
            }
          }
        for (int j = 0; j <= n; j++) {
          if (used[j]) {
            u[p[j]] += delta;
            v[j] -= delta;
          } else minv[j] -= delta;
        }
        j0 = j1;
      } while (p[j0] != 0);
      // Flip the alternating path to extend the matching by one vertex.
      do {
        int j1 = way[j0];
        p[j0] = p[j1];
        j0 = j1;
      } while (j0 != 0);
    }
    int[] result = new int[rows];
    Arrays.fill(result, -1);
    for (int j = 1; j <= n; j++)
      if (p[j] > 0 && p[j] <= rows && j <= cols) result[p[j] - 1] = j - 1;
    return result;
  }
}
