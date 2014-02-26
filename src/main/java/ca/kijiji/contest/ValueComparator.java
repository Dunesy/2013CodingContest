package ca.kijiji.contest;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<String> {
 
  Map<String, Integer> map;
 
  public ValueComparator(Map<String, Integer> map){
    this.map = map;
  }
  
  public int compare(String keyA, String keyB){
 
    Comparable<Integer> valueA =  map.get(keyA);
    Comparable<Integer> valueB =  map.get(keyB);
  
    return valueB.compareTo((Integer) valueA);
 
  }

}
