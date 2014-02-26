package ca.kijiji.contest;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapSort 
{	 
	  public static SortedMap<String, Integer> sortByValue(Map<String, Integer> unsortedMap)
	  {
	    SortedMap<String, Integer> sortedMap = new TreeMap<String, Integer>(new ValueComparator(unsortedMap));
	    sortedMap.putAll(unsortedMap);
	    return sortedMap;
	  }
	  public static Map<String, Integer> sortByKey(Map<String, Integer> unsortedMap)
	  {
	    Map<String, Integer> sortedMap = new TreeMap<String, Integer>();
	    sortedMap.putAll(unsortedMap);
	    return sortedMap;
	  }
	  
}