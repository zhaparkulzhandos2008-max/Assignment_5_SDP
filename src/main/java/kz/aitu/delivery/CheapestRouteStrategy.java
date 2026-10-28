package kz.aitu.delivery;

import java.util.List;
public final class CheapestRouteStrategy extends ScoredRouteStrategy {
 public String getStrategyName() { return "Cheapest"; }
 protected double score(Route r, DeliveryRequest q, List<Route> valid) { return RouteMetrics.cost(r,q); }
 protected String explain(Route r, DeliveryRequest q, List<Route> valid) {
  return "Minimum total cost: base "+r.baseCost()+" + distance "+r.distanceKm()+" * "+r.costPerKm()+" + fees "+r.additionalFees()+" + urgent surcharge "+(q.priority()==DeliveryPriority.URGENT?1000:0)+" = "+RouteMetrics.cost(r,q)+" KZT";
 }
}
