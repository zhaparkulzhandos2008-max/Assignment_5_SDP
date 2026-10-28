package kz.aitu.delivery;

import java.util.*;
import java.util.function.ToDoubleFunction;
public final class BalancedRouteStrategy extends ScoredRouteStrategy {
 private final double timeWeight,costWeight,distanceWeight,emissionWeight;
 public BalancedRouteStrategy(double t,double c,double d,double e) {
  Checks.nonNegative(t,"time weight");Checks.nonNegative(c,"cost weight");Checks.nonNegative(d,"distance weight");Checks.nonNegative(e,"emission weight");
  if(Math.abs(t+c+d+e-1.0)>1e-9) throw new IllegalArgumentException("Weights must sum to 1.0");
  timeWeight=t;costWeight=c;distanceWeight=d;emissionWeight=e;
 }
 public String getStrategyName() { return "Balanced"; }
 private double normalize(Route r,List<Route> valid,ToDoubleFunction<Route> f) {
  double min=valid.stream().mapToDouble(f).min().orElseThrow(), max=valid.stream().mapToDouble(f).max().orElseThrow();
  return max==min?0:(f.applyAsDouble(r)-min)/(max-min);
 }
 protected double score(Route r,DeliveryRequest q,List<Route> v) {
  return timeWeight*normalize(r,v,RouteMetrics::time)+costWeight*normalize(r,v,x->RouteMetrics.cost(x,q))
   +distanceWeight*normalize(r,v,Route::distanceKm)+emissionWeight*normalize(r,v,Route::carbonEmission);
 }
 protected String explain(Route r,DeliveryRequest q,List<Route> v) {
  return String.format(Locale.ROOT,"Min-max normalized (time,cost,distance,CO2) = (%.4f,%.4f,%.4f,%.4f); weights = (%.2f,%.2f,%.2f,%.2f); score = %.4f",
   normalize(r,v,RouteMetrics::time),normalize(r,v,x->RouteMetrics.cost(x,q)),normalize(r,v,Route::distanceKm),normalize(r,v,Route::carbonEmission),timeWeight,costWeight,distanceWeight,emissionWeight,score(r,q,v));
 }
}
