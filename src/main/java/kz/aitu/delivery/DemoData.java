package kz.aitu.delivery;

import java.util.*;
public final class DemoData {
 private DemoData() {}
 public static List<Route> routes() {
  Location a=new Location("Astana warehouse",51.16,71.43),b=new Location("City hospital",51.12,71.45);
  return List.of(
   new Route("R1",a,b,12,18,2000,100,100,3.8,14,2,true),
   new Route("R2",a,b,16,20,3000,100,200,4.5,2,1,true),
   new Route("R3",a,b,11,35,800,70,100,2.0,3,2,true),
   new Route("R4",a,b,13,42,1300,70,0,0.4,1,0,true),
   new Route("R5",a,b,18,39,500,60,0,2.5,5,1,true),
   new Route("R6",a,b,9,27,2100,100,50,1.6,4,1,true),
   new Route("R7",a,b,10,22,100,10,0,0.1,0,0,false),
   new Route("R8",a,b,14,30,1700,80,100,1.2,2,1,true));
 }
}
