package ca.kijiji.contest;

public class TicketData {
	public static int Ticket_Number = 0; 
	String location;
	int amount;
	int ticketNumber;
	
	TicketData(String aLocation, int anAmount)
	{
		location = aLocation;
		amount = anAmount;
		ticketNumber = Ticket_Number++;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public String getLocation()
	{
		return location;
	}
	
	public int getTicketNumber()
	{
		return ticketNumber;
	}
	
	
		
}
