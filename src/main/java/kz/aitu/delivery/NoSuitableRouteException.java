package kz.aitu.delivery;

import java.util.*;
public final class NoSuitableRouteException extends RuntimeException {
 private final Map<String,List<String>> rejected;
 public NoSuitableRouteException(Map<String,List<String>> rejected) {
  super("No suitable route: " + (rejected.isEmpty()?"empty candidate list":rejected));
  Map<String,List<String>> copy=new LinkedHashMap<>(); rejected.forEach((k,v)->copy.put(k,List.copyOf(v)));
  this.rejected=Collections.unmodifiableMap(copy);
 }
 public Map<String,List<String>> rejectedRoutes() { return rejected; }
}
