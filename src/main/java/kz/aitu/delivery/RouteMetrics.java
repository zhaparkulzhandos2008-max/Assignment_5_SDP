package kz.aitu.delivery;

public final class RouteMetrics {
 private RouteMetrics() {}
 public static double time(Route r) { return (double)r.estimatedMinutes()+r.trafficPenaltyMinutes()+r.weatherPenaltyMinutes(); }
 public static double cost(Route r, DeliveryRequest q) {
  return r.baseCost()+r.distanceKm()*r.costPerKm()+r.additionalFees()+(q.priority()==DeliveryPriority.URGENT ? 1000 : 0);
 }
}
