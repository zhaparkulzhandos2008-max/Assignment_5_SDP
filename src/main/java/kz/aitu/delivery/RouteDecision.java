package kz.aitu.delivery;

import java.util.*;
public record RouteDecision(String strategyName, Route route, double score, double effectiveMinutes,
 double totalCost, String explanation, Map<String,List<String>> rejectedRoutes) {
 public RouteDecision {
  Objects.requireNonNull(route); Checks.text(strategyName,"strategy"); Checks.text(explanation,"explanation");
  Checks.nonNegative(score,"score"); Checks.nonNegative(effectiveMinutes,"time"); Checks.nonNegative(totalCost,"cost");
  Map<String,List<String>> copy=new LinkedHashMap<>();
  rejectedRoutes.forEach((k,v)->copy.put(k,List.copyOf(v))); rejectedRoutes=Collections.unmodifiableMap(copy);
 }
}
