package kz.aitu.delivery;

import java.util.List;
public final class EcoFriendlyRouteStrategy extends ScoredRouteStrategy {
 private final double emissionWeight, distanceWeight;
 public EcoFriendlyRouteStrategy() { this(0.9,0.1); }
 public EcoFriendlyRouteStrategy(double emissionWeight,double distanceWeight) {
  Checks.nonNegative(emissionWeight,"emission weight"); Checks.nonNegative(distanceWeight,"distance weight");
  if(emissionWeight+distanceWeight==0 || !Double.isFinite(emissionWeight+distanceWeight)) throw new IllegalArgumentException("Eco weights must have finite positive sum");
  this.emissionWeight=emissionWeight;this.distanceWeight=distanceWeight;
 }
 public String getStrategyName() { return "Eco Friendly"; }
 protected double score(Route r, DeliveryRequest q, List<Route> valid) { return r.carbonEmission()*emissionWeight+r.distanceKm()*distanceWeight; }
 protected String explain(Route r, DeliveryRequest q, List<Route> valid) {
  return "Minimum eco score: "+r.carbonEmission()+" kg * "+emissionWeight+" + "+r.distanceKm()+" km * "+distanceWeight+" = "+score(r,q,valid);
 }
}
