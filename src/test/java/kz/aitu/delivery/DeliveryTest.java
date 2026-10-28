package kz.aitu.delivery;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {
 private final List<Route> routes=DemoData.routes();
 private final DeliveryRequest standard=new DeliveryRequest(DeliveryPriority.STANDARD,10000,90,false);
 private RouteDecision choose(RouteStrategy s) { return s.selectRoute(routes,standard); }
 private Route route(String id) { return routes.stream().filter(r->r.id().equals(id)).findFirst().orElseThrow(); }
 private StrategySelector selector() {
  return new StrategySelector(List.of(
   new StrategySelector.Rule(q->q.priority()==DeliveryPriority.URGENT,new FastestRouteStrategy()),
   new StrategySelector.Rule(q->q.priority()==DeliveryPriority.ECO || q.ecoPreferred(),new EcoFriendlyRouteStrategy()),
   new StrategySelector.Rule(q->q.priority()==DeliveryPriority.ECONOMY,new CheapestRouteStrategy()),
   new StrategySelector.Rule(q->q.priority()==DeliveryPriority.STANDARD,new BalancedRouteStrategy(.4,.3,.1,.2))));
 }
 @Test void fastestIncludesTrafficAndWeather() { var d=choose(new FastestRouteStrategy());assertEquals("R2",d.route().id());assertEquals(23,d.effectiveMinutes()); }
 @Test void cheapestUsesAllCostComponents() { var d=choose(new CheapestRouteStrategy()); assertEquals("R5",d.route().id());assertEquals(1580,d.totalCost()); }
 @Test void costIncludesPrioritySurcharge() { assertEquals(2580,RouteMetrics.cost(route("R5"),new DeliveryRequest(DeliveryPriority.URGENT,10000,90,false))); }
 @Test void ecoBalancesEmissionAndDistance() { var d=choose(new EcoFriendlyRouteStrategy());assertEquals("R4",d.route().id());assertEquals(1.66,d.score(),1e-9); }
 @Test void balancedTimeOnlyMatchesFastest() { assertEquals("R2",choose(new BalancedRouteStrategy(1,0,0,0)).route().id()); }
 @Test void balancedDefaultHasExpectedWinnerAndScore() {
  var d=choose(new BalancedRouteStrategy(.4,.3,.1,.2));assertEquals("R6",d.route().id());
  double expected=.4*9/22+.3*1470/3220+.2*1.2/4.1;
  assertEquals(expected,d.score(),1e-9);
 }
 @Test void runtimeSwitchesSamePlannerThreeTimes() {
  var p=new RoutePlanner(new FastestRouteStrategy());assertEquals("R2",p.plan(routes,standard).route().id());
  p.setStrategy(new CheapestRouteStrategy());assertEquals("R5",p.plan(routes,standard).route().id());
  p.setStrategy(new EcoFriendlyRouteStrategy());assertEquals("R4",p.plan(routes,standard).route().id());
  p.setStrategy(new DistanceFirstStrategy());assertEquals("R6",p.plan(routes,standard).route().id());
 }
 @Test void autoSelectsAllFourPriorities() {
  var s=selector();String[] expected={"Fastest","Cheapest","Eco Friendly","Balanced"};int i=0;
  for(var p:DeliveryPriority.values()) assertEquals(expected[i++],s.select(new DeliveryRequest(p,10000,90,false)).getStrategyName());
 }
 @Test void urgentRuleHasPriorityOverEcoPreference() { assertEquals("Fastest",selector().select(new DeliveryRequest(DeliveryPriority.URGENT,10000,90,true)).getStrategyName()); }
 @Test void ecoPreferenceOverridesEconomy() { assertEquals("Eco Friendly",selector().select(new DeliveryRequest(DeliveryPriority.ECONOMY,10000,90,true)).getStrategyName()); }
 @Test void customSelectorRuleIsExtensible() {var s=new StrategySelector(List.of(new StrategySelector.Rule(q->true,new DistanceFirstStrategy())));assertEquals("Distance First",s.select(standard).getStrategyName());}
 @Test void unavailableCheapGreenRouteNeverWins() {
  for(var s:List.of(new FastestRouteStrategy(),new CheapestRouteStrategy(),new EcoFriendlyRouteStrategy(),new BalancedRouteStrategy(.4,.3,.1,.2),new DistanceFirstStrategy())) {
   var d=s.selectRoute(routes,standard);assertNotEquals("R7",d.route().id());assertTrue(d.rejectedRoutes().get("R7").contains("Route unavailable"));
  }
 }
 @Test void budgetChangesFastestChoice() {var d=new FastestRouteStrategy().selectRoute(routes,new DeliveryRequest(DeliveryPriority.STANDARD,3500,90,false));assertEquals("R6",d.route().id());assertTrue(d.rejectedRoutes().get("R2").contains("Budget exceeded"));}
 @Test void deadlineFiltersEcoWinner() {assertEquals("R6",new EcoFriendlyRouteStrategy().selectRoute(routes,new DeliveryRequest(DeliveryPriority.STANDARD,10000,32,false)).route().id());}
 @Test void emissionsAreHardConstraints() {var q=new DeliveryRequest(DeliveryPriority.STANDARD,10000,90,false,0.5,Set.of());assertEquals("R4",new FastestRouteStrategy().selectRoute(routes,q).route().id());}
 @Test void prohibitionIsAHardConstraint() {var q=new DeliveryRequest(DeliveryPriority.STANDARD,10000,90,false,10,Set.of("R2"));assertEquals("R6",new FastestRouteStrategy().selectRoute(routes,q).route().id());}
 @Test void noFeasibleRouteHasExplicitDiagnostic() {var e=assertThrows(NoSuitableRouteException.class,()->new FastestRouteStrategy().selectRoute(routes,new DeliveryRequest(DeliveryPriority.STANDARD,1,1,false)));assertEquals(8,e.rejectedRoutes().size());}
 @Test void emptyCandidatesFailExplicitly() {assertThrows(NoSuitableRouteException.class,()->new CheapestRouteStrategy().selectRoute(List.of(),standard));}
 @Test void invalidWeightSumRejected() {assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(.4,.4,.4,.4));}
 @Test void negativeAndNanWeightsRejected() {assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(-1,1,1,0));assertThrows(IllegalArgumentException.class,()->new BalancedRouteStrategy(Double.NaN,0,0,1));}
 @Test void fifthStrategyMinimizesFeasibleDistance() {assertEquals("R6",choose(new DistanceFirstStrategy()).route().id());}
 @Test void comparisonRunsSameRequestThroughAllStrategies() {var out=new StrategyComparison(List.of(new FastestRouteStrategy(),new CheapestRouteStrategy())).compare(routes,standard);assertEquals("R2",out.get(0).decision().orElseThrow().route().id());assertEquals("R5",out.get(1).decision().orElseThrow().route().id());}
 @Test void comparisonRetainsFailures() {var out=new StrategyComparison(List.of(new FastestRouteStrategy(),new CheapestRouteStrategy())).compare(List.of(),standard);assertEquals(2,out.size());assertTrue(out.stream().allMatch(o->o.failure().isPresent()&&o.decision().isEmpty()));}
 @Test void exactBudgetDeadlineEmissionBoundariesAreInclusive() {var q=new DeliveryRequest(DeliveryPriority.STANDARD,4800,23,false,4.5,Set.of());assertEquals("R2",new FastestRouteStrategy().selectRoute(List.of(route("R2")),q).route().id());}
 @Test void singleCandidateNormalizationHasNoDivisionByZero() {var d=new BalancedRouteStrategy(.4,.3,.1,.2).selectRoute(List.of(route("R6")),standard);assertEquals(0,d.score());}
 @Test void identicalMetricsTieBreakByIdIndependentOfOrder() {var x=route("R6");var a=new Route("A",x.origin(),x.destination(),x.distanceKm(),x.estimatedMinutes(),x.baseCost(),x.costPerKm(),x.additionalFees(),x.carbonEmission(),x.trafficPenaltyMinutes(),x.weatherPenaltyMinutes(),true);assertEquals("A",new FastestRouteStrategy().selectRoute(List.of(x,a),standard).route().id());}
 @Test void negativeDistanceRejected() {assertThrows(IllegalArgumentException.class,()->new Route("bad",route("R1").origin(),route("R1").destination(),-1,1,0,0,0,0,0,0,true));}
 @Test void missingRequestAndStrategyRejected() {assertThrows(NullPointerException.class,()->new FastestRouteStrategy().selectRoute(routes,null));assertThrows(NullPointerException.class,()->new RoutePlanner(null));}
 @Test void duplicateRouteIdsRejected() {assertThrows(IllegalArgumentException.class,()->new FastestRouteStrategy().selectRoute(List.of(route("R1"),route("R1")),standard));}
 @Test void requestCopiesProhibitionSet() {var ids=new HashSet<String>();ids.add("R2");var q=new DeliveryRequest(DeliveryPriority.STANDARD,10000,90,false,10,ids);ids.clear();assertTrue(q.prohibitedRouteIds().contains("R2"));}
 @Test void invalidRequestAndCoordinatesRejected() {assertThrows(IllegalArgumentException.class,()->new DeliveryRequest(DeliveryPriority.STANDARD,-1,10,false));assertThrows(IllegalArgumentException.class,()->new Location("bad",91,0));}
 @Test void explanationContainsFormulaAndRejections() {var d=choose(new FastestRouteStrategy());assertTrue(d.explanation().contains("traffic 2"));assertEquals(List.of("Route unavailable"),d.rejectedRoutes().get("R7"));}
}
