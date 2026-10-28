package kz.aitu.delivery;

final class Checks {
 private Checks() {}
 static String text(String s, String field) { if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " is required"); return s; }
 static double nonNegative(double n, String field) { if (!Double.isFinite(n) || n < 0) throw new IllegalArgumentException(field + " must be finite and nonnegative"); return n; }
}
