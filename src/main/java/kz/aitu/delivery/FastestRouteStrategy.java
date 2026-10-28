package kz.aitu.delivery;

import java.util.List;
public final class FastestRouteStrategy extends ScoredRouteStrategy {
 public String getStrategyName() { return "Fastest"; }
 protected double score(Route r, DeliveryRequest q, List<Route> valid) { return RouteMetrics.time(r); }
 protected String explain(Route r, DeliveryRequest q, List<Route> valid) {
  return "Minimum effective time: base "+r.estimatedMinutes()+" + traffic "+r.trafficPenaltyMinutes()+" + weather "+r.weatherPenaltyMinutes()+" = "+RouteMetrics.time(r)+" min";
 }
}
