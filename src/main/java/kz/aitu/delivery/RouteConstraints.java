package kz.aitu.delivery;

import java.util.*;
public final class RouteConstraints {
 public record Candidates(List<Route> valid, Map<String,List<String>> rejected) {}
 public Candidates filter(List<Route> routes, DeliveryRequest request) {
  Objects.requireNonNull(routes,"routes"); Objects.requireNonNull(request,"request");
  List<Route> valid=new ArrayList<>(); Map<String,List<String>> rejected=new LinkedHashMap<>(); Set<String> ids=new HashSet<>();
  for(Route r:routes) {
   Objects.requireNonNull(r,"route"); if(!ids.add(r.id())) throw new IllegalArgumentException("Duplicate route ID: "+r.id());
   List<String> reasons=new ArrayList<>();
   if(!r.available()) reasons.add("Route unavailable");
   if(RouteMetrics.cost(r,request)>request.maxBudget()) reasons.add("Budget exceeded");
   if(RouteMetrics.time(r)>request.maxDeliveryMinutes()) reasons.add("Delivery deadline exceeded");
   if(r.carbonEmission()>request.maxEmissionKg()) reasons.add("Emission limit exceeded");
   if(request.prohibitedRouteIds().contains(r.id())) reasons.add("Route prohibited for this delivery");
   if(reasons.isEmpty()) valid.add(r); else rejected.put(r.id(),List.copyOf(reasons));
  }
  if(valid.isEmpty()) throw new NoSuitableRouteException(rejected);
  return new Candidates(List.copyOf(valid),Collections.unmodifiableMap(rejected));
 }
}
