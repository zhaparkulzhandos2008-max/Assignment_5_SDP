package kz.aitu.delivery;

import java.util.Objects;
public record Route(String id, Location origin, Location destination, double distanceKm,
 int estimatedMinutes, double baseCost, double costPerKm, double additionalFees,
 double carbonEmission, int trafficPenaltyMinutes, int weatherPenaltyMinutes, boolean available) {
 public Route {
  Checks.text(id,"id"); Objects.requireNonNull(origin,"origin"); Objects.requireNonNull(destination,"destination");
  Checks.nonNegative(distanceKm,"distance"); Checks.nonNegative(baseCost,"base cost");
  Checks.nonNegative(costPerKm,"distance rate"); Checks.nonNegative(additionalFees,"fees"); Checks.nonNegative(carbonEmission,"emissions");
  if(estimatedMinutes<0 || trafficPenaltyMinutes<0 || weatherPenaltyMinutes<0) throw new IllegalArgumentException("Negative time");
 }
}
