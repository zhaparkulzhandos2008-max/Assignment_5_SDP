package kz.aitu.delivery;

import java.util.*;
public record DeliveryRequest(DeliveryPriority priority, double maxBudget, int maxDeliveryMinutes,
 boolean ecoPreferred, double maxEmissionKg, Set<String> prohibitedRouteIds) {
 public DeliveryRequest {
  Objects.requireNonNull(priority, "priority"); Checks.nonNegative(maxBudget,"budget");
  Checks.nonNegative(maxEmissionKg,"emission limit");
  if(maxDeliveryMinutes<0) throw new IllegalArgumentException("Negative deadline");
  prohibitedRouteIds=Set.copyOf(Objects.requireNonNull(prohibitedRouteIds,"prohibited routes"));
 }
 public DeliveryRequest(DeliveryPriority p, double budget, int deadline, boolean eco) {
  this(p,budget,deadline,eco,Double.MAX_VALUE,Set.of());
 }
}
