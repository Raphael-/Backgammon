
public class Move
{
	private int to;
	private int diceNeeded;
	private int scenario;
	
	public Move(int dest,int dn)
	{
		this.to = dest;
		this.diceNeeded = dn;
		//this.scenario = sc;
	}
	
	public int getDest()
	{
		return this.to;
	}
	
	public int getDiceNeeded()
	{
		return this.diceNeeded;
	}
	
	public String toString()
	{
		return "{ dest = "+to+" dice needed "+diceNeeded+" scenario "+scenario+" }";
	}
}
