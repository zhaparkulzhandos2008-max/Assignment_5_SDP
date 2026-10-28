package kz.aitu.delivery;

import java.util.*;
public final class StrategyComparison {
 public record Outcome(String strategyName, Optional<RouteDecision> decision, Optional<String> failure) {}
 private final List<RouteStrategy> strategies;
 public StrategyComparison(List<RouteStrategy> strategies) { this.strategies=List.copyOf(strategies); }
 public List<Outcome> compare(List<Route> routes,DeliveryRequest request) {
  List<Outcome> results=new ArrayList<>();
  for(RouteStrategy s:strategies) {
   try { results.add(new Outcome(s.getStrategyName(),Optional.of(s.selectRoute(routes,request)),Optional.empty())); }
   catch(NoSuitableRouteException e) { results.add(new Outcome(s.getStrategyName(),Optional.empty(),Optional.of(e.getMessage()))); }
  }
  return List.copyOf(results);
 }
}
