package ca.kijiji.contest;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketProcessor implements Runnable
{
    
	private static final Logger LOG = LoggerFactory.getLogger(TicketProcessor.class);
	static final  int set_final_amount = 4;
    static final  int location2 = 7;
    static final  int record_length = 10;
    static Pattern streetType = Pattern.compile("RD$|ST$|DR$|AVE$|AV$|PK$|WAY$|BLVD$|GATE$|CRES$|CT$|HILL$|PARK$|GDNS$");
	static Pattern digits = Pattern.compile("(\\d)");
	boolean terminate;
	int processorNumber;
	
	//public StringBuilder dataChunks;
	ArrayList<TicketData> list;
	ArrayList<String> dataChunks; 
	TreeMap<String, Integer> currentProfit;
	int currentTicketIndex ;
	int currentChunkIndex ;
	int currentChunkBlockIndex ; 
	int totalBlocks ;
	
	TicketProcessor(int num)
	{
		processorNumber = num;
		dataChunks = new ArrayList<String>(300);
		currentChunkIndex = 0;
		currentChunkBlockIndex = 0 ; 
		totalBlocks = 0;
		
		currentProfit = new TreeMap<String, Integer>();
		
	}
	
	public void processTickets(String alocation, int profit)
	{
			String[] location = alocation.split("\\s");
			String formattedLocation = "";
			
			if (location.length <= 1)
			{
				return;
			}
			// Scan The Address from Right to Left to find the first match for a street type. Then clear them
			for (int i = location.length - 1 ; i >= location.length - 2 ; i--)
			{
				// Check for a Street Type
				Matcher m = streetType.matcher(location[i]);
				if (m.find())
				{
					//Clear Out The Right Most Tokens from The point a match was found
					for (int j = i ; j < location.length ; j++ )
					{
						location[j] = "";
					}
					break;
				}
			}
			
			// Check for any Digits
			
			Matcher m = digits.matcher(location[0]);
			if (m.find())
			{
				//Remove any Instances where a number appears on the left side.  This should be consistent with the majority of the data.
				location[0] = "";
			}
				
			for (int i = 0 ; i < location.length; i++)
			{
				if (!location[i].isEmpty())
					formattedLocation += location[i] + " ";
			}
			
			formattedLocation = formattedLocation.trim();
			
			//Updating the Map
			if (currentProfit.containsKey(formattedLocation))
			{
				int amount = currentProfit.get(formattedLocation);
				currentProfit.put(formattedLocation, amount + profit);
			}
			else
			{
				currentProfit.put(formattedLocation, profit);
			}
			
			currentTicketIndex++;
	}
	
	public void processChunks()
	{		
		//long startTime = System.currentTimeMillis();
		String location = "";
		String amount = "";
		int columnCounter = 0;
		
		String currentBlock = null;
		
		while (!terminate || currentChunkBlockIndex < dataChunks.size())
		{
			if (dataChunks.size() == 0)
				continue;
			
			if (currentBlock == null)
			{
				currentBlock = dataChunks.get(currentChunkBlockIndex);
				currentChunkIndex = 0;
				continue;
			}
			
			if (currentChunkIndex == currentBlock.length() - 1)
			{
				currentChunkBlockIndex ++;
				currentBlock = null;
				continue;
			}
			
			if (currentBlock.charAt(currentChunkIndex) == '\r')
			{
				
			}
			else if (currentBlock.charAt(currentChunkIndex) == '\n')
			{
				try
				{
					if (columnCounter == record_length)
						processTickets(location, Integer.parseInt(amount));
				}
				catch (Exception ex)
				{
					columnCounter = 0;
					amount = "";
					location = "";
					continue;
				}
				columnCounter = 0;
				amount = "";
				location = "";
			}
			else if (currentBlock.charAt(currentChunkIndex) == ',')
			{
				columnCounter++;
			}
			else if (columnCounter == set_final_amount )
			{
				amount += currentBlock.charAt(currentChunkIndex);
			}
			else if (columnCounter == location2)
			{
				location += currentBlock.charAt(currentChunkIndex);
			}
			
			if (currentChunkIndex < currentBlock.length())
				currentChunkIndex++;
			
		}
		
		
		//long duration = System.currentTimeMillis() - startTime;
		//LOG.info("One Chunk Processed - Duration: {}ms", duration);
		
	} 
		
	
	public void shutDown()
	{
		terminate = true;
	}
	
	public void run() 
	{
		LOG.info("Starting Up Ticket Processor #" + processorNumber );
		terminate = false;
		
		long startTime = System.currentTimeMillis();
		processChunks();
		long duration = System.currentTimeMillis() - startTime;
		LOG.info("Completed Shutting Down Processor #{} - Duration: {}ms", processorNumber, duration);;
	}

	public TreeMap<String,Integer> getMap()
	{
		return currentProfit;
	}
}
