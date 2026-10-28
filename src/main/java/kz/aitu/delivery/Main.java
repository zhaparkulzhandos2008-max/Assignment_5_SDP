package kz.aitu.delivery;

import java.util.*;
public final class Main {
 public static void main(String[] args) {
  Locale.setDefault(Locale.ROOT);
  RouteStrategy fastest=new FastestRouteStrategy(), cheapest=new CheapestRouteStrategy(),eco=new EcoFriendlyRouteStrategy(),balanced=new BalancedRouteStrategy(.4,.3,.1,.2);
  List<RouteStrategy> strategies=List.of(fastest,cheapest,eco,balanced,new DistanceFirstStrategy());
  var routes=DemoData.routes(); var standard=new DeliveryRequest(DeliveryPriority.STANDARD,10000,90,false);
  RoutePlanner planner=new RoutePlanner(fastest);
  System.out.println("RUNTIME SWITCHING: same RoutePlanner, three switches");
  print(planner.plan(routes,standard));
  for(RouteStrategy s:List.of(cheapest,eco,balanced)) { planner.setStrategy(s); print(planner.plan(routes,standard)); }
  var selector=new StrategySelector(List.of(
   new StrategySelector.Rule(q->q.priority()==DeliveryPriority.URGENT,fastest),
   new StrategySelector.Rule(q->q.priority()==DeliveryPriority.ECO || q.ecoPreferred(),eco),
   new StrategySelector.Rule(q->q.priority()==DeliveryPriority.ECONOMY,cheapest),
   new StrategySelector.Rule(q->q.priority()==DeliveryPriority.STANDARD,balanced)));
  System.out.println("\nAUTOMATIC SELECTION: first matching business rule wins");
  for(var q:List.of(new DeliveryRequest(DeliveryPriority.URGENT,10000,40,false),new DeliveryRequest(DeliveryPriority.ECONOMY,2000,90,false),new DeliveryRequest(DeliveryPriority.ECO,10000,90,true),standard)) {
   planner.setStrategy(selector.select(q)); System.out.println("Request "+q.priority()+" budget="+q.maxBudget()); print(planner.plan(routes,q));
  }
  System.out.println("\nCOMPARISON: identical request");
  for(var o:new StrategyComparison(strategies).compare(routes,standard)) { o.decision().ifPresent(Main::print); o.failure().ifPresent(System.out::println); }
  System.out.println("\nDYNAMIC BUDGET: fastest with budget 3500"); planner.setStrategy(fastest);print(planner.plan(routes,new DeliveryRequest(DeliveryPriority.STANDARD,3500,90,false)));
  System.out.println("\nFAILURE SCENARIOS");
  for(Runnable action:List.<Runnable>of(()->planner.plan(List.of(),standard),()->planner.plan(routes,new DeliveryRequest(DeliveryPriority.STANDARD,1,1,false)),()->new BalancedRouteStrategy(.5,.5,.5,.5))) {
   try { action.run(); } catch(IllegalArgumentException | NoSuitableRouteException e) { System.out.println(e.getClass().getSimpleName()+": "+e.getMessage()); }
  }
 }
 private static void print(RouteDecision d) {
  System.out.printf("%s -> %s | time=%.0f min | cost=%.2f KZT | distance=%.1f km | CO2=%.2f kg | score=%.4f%n",d.strategyName(),d.route().id(),d.effectiveMinutes(),d.totalCost(),d.route().distanceKm(),d.route().carbonEmission(),d.score());
  System.out.println("Reason: "+d.explanation()); d.rejectedRoutes().forEach((id,reasons)->System.out.println("Rejected "+id+": "+String.join(", ",reasons)));
 }
}
