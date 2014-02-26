package ca.kijiji.contest;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketDataReader implements Runnable
{

    static final  int set_final_amount = 4;
    static final  int location2 = 7;
    static final  int CHUNK_SIZE = 1 << 24;
    private byte[] chunkBuffer;
    
    private static final Logger LOG = LoggerFactory.getLogger(TicketDataReader.class);

    
	private InputStream parkingTicketStream = null;
	public TicketProcessor[] processors = null;
	public void run() {
		long startTime = System.currentTimeMillis();
		try
		{
		readData();
		}
		catch (Exception ex)
		{
			return;
		}
		 long duration = System.currentTimeMillis() - startTime;
			LOG.info("Completed Shutting Down DataReader - Duration: {}ms", duration);
	}

	public TicketDataReader(InputStream aStream, TicketProcessor[] processorSet)
	{
		processors = processorSet;
		parkingTicketStream = aStream;
		chunkBuffer = new byte[CHUNK_SIZE];
	}
	
	
	public void readData() throws IOException 
	{
		LOG.info("Read Data Started");
		int actualLengthRead = 0;
		int counter = 0;
		do 
		{	
			try 
			{
				actualLengthRead = parkingTicketStream.read(chunkBuffer,0, CHUNK_SIZE);				
				// Look for Carriage Return Characters
			//	int lastCarriageReturnIndex = 0;
			//	for (int i = actualLengthRead - 1; i >= 0; i--)
			//	{
			//		if (chunkBuffer[i] == '\n' || chunkBuffer[i] == '\r')
			//		{
			//			lastCarriageReturnIndex = i;
			//			break;
			//		}
			//	}
				
				processors[counter % (ParkingTicketsStats.NUMBER_OF_THREADS - 1)].dataChunks.add(new String(chunkBuffer));
				counter++;
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}  while(actualLengthRead == CHUNK_SIZE);
		
		parkingTicketStream.close();
		
		LOG.info("Completed Readthrough - Telling Processors to Shutdown when finished.");
    	for (int i = 0 ; i < processors.length ; i++)
    	{
    		processors[i].shutDown();
    	}
			
			
		
		
		
		
	}

}
