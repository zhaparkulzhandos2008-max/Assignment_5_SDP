package kz.aitu.delivery;

import java.util.List;
public final class DistanceFirstStrategy extends ScoredRouteStrategy {
 public String getStrategyName() { return "Distance First"; }
 protected double score(Route r, DeliveryRequest q, List<Route> valid) { return r.distanceKm(); }
 protected String explain(Route r, DeliveryRequest q, List<Route> valid) { return "Minimum feasible distance: "+r.distanceKm()+" km"; }
}
