package kz.aitu.delivery;

import java.util.*;
/** Shared selection mechanics; concrete strategies own the scoring algorithm. */
public abstract class ScoredRouteStrategy implements RouteStrategy {
 private final RouteConstraints constraints=new RouteConstraints();
 protected abstract double score(Route route, DeliveryRequest request, List<Route> valid);
 protected abstract String explain(Route route, DeliveryRequest request, List<Route> valid);
 @Override public final RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
  var c=constraints.filter(routes,request);
  Route best=c.valid().stream().min(Comparator.<Route>comparingDouble(r->score(r,request,c.valid())).thenComparing(Route::id)).orElseThrow();
  return new RouteDecision(getStrategyName(),best,score(best,request,c.valid()),RouteMetrics.time(best),RouteMetrics.cost(best,request),explain(best,request,c.valid()),c.rejected());
 }
}
