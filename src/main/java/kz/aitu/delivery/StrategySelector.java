package kz.aitu.delivery;

import java.util.*;
import java.util.function.Predicate;
public final class StrategySelector {
 public record Rule(Predicate<DeliveryRequest> condition, RouteStrategy strategy) {
  public Rule { Objects.requireNonNull(condition);Objects.requireNonNull(strategy); }
 }
 private final List<Rule> rules;
 public StrategySelector(List<Rule> rules) { this.rules=List.copyOf(rules); }
 public RouteStrategy select(DeliveryRequest request) {
  Objects.requireNonNull(request,"request");
  return rules.stream().filter(r->r.condition().test(request)).findFirst().orElseThrow(()->new IllegalArgumentException("No strategy rule matches request")).strategy();
 }
}
