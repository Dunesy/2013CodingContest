package ca.kijiji.contest;

import java.io.InputStream;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ParkingTicketsStats 
{
	private static final Logger LOG = LoggerFactory.getLogger(ParkingTicketsStats.class);

    public static final  int NUMBER_OF_THREADS = 4;
    public static SortedMap<String, Integer> sortStreetsByProfitability(InputStream parkingTicketsStream) {
    	ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    	//Create the TicketProcessors Based off a Quad Core Machine
    	
    	//Preparing the Threads
    	
    	TicketProcessor[] processors = new TicketProcessor[NUMBER_OF_THREADS - 1];
    	Runnable[] workers = new Runnable[NUMBER_OF_THREADS];
    	for (int i = 0 ; i < NUMBER_OF_THREADS; i++)
    	{
    		if (i < NUMBER_OF_THREADS - 1)
    		{
    			LOG.info("Initializing Processor #" + i);
    			processors[i] = new TicketProcessor(i);
    			workers[i] = processors[i];
    		}
    		else
    		{
    			LOG.info("Initializing TicketDataReader");
    			workers[i] = new TicketDataReader(parkingTicketsStream, processors);
    		}
    	}
    	
    	//Executing The Threads
     	for (int i = NUMBER_OF_THREADS - 1 ; i >= 0 ; i--)
    	{
    		service.execute(workers[i]);
    	}
    	
     	service.shutdown(); 	
    	try 
    	{
    		service.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			LOG.error("sortStreetByProfitabilityInterrupted Interrupted Exception Caught");
		}
     	  	
    	SortedMap<String, Integer> sorted_Map = processors[0].getMap();
    	
    	for (int i = 1 ; i < NUMBER_OF_THREADS - 1; i++)
    	{
    		mergeMaps(processors[i].getMap(), sorted_Map);
    	}
  
    	return MapSort.sortByValue(sorted_Map);
    }

    public static void mergeMaps( TreeMap<String, Integer> tree, SortedMap<String, Integer> parentTree)
    { 	
    	for (Map.Entry<String, Integer> entry : tree.entrySet())
    	{
    		if (parentTree.containsKey(entry.getKey()))
    		{
    			int currentValue = parentTree.get(entry.getKey());
    			parentTree.put(entry.getKey(), entry.getValue() + currentValue);
    		}
    		else
    		{
    			parentTree.put(entry.getKey(), entry.getValue());
    		}
    	}
    }
    
}


