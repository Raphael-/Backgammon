public class Dice
{
	private int[] nums; // up to 4
	private boolean[] consumed; // shows if and which dice are consumed(used) during this turn

	public Dice()
	{
		nums = new int[4];
		consumed = new boolean[4];
	}

	/**
	 * Returns the sum of the dice.
	 * 
	 */
	public int getTotal()
	{
		int total = 0;
		for (int b : nums)
		{
			total += b;
		}
		return total;
	}

	public int[] getDiceResults()
	{
		return nums;
	}

	public int getDie(int num)
	{
		return nums[num];
	}

	public int getFirstNotConsumedNum()
	{
		for (int b = 0; b < 4; b++)
		{
			if(!consumed[b])
				return b;
		}
		return -1;
	}

	public int totalConsumed()
	{
		int total = 0;
		for (int b = 0; b < 4; b++)
		{
			if(consumed[b])
			{
				total++;
			}
		}
		return total;
	}

	public boolean isRollDouble()
	{
		return nums[0] == nums[1];
	}

	public boolean isConsumed(int b)
	{
		return consumed[b];
	}

	public boolean diceAvailable(int num)
	{
		if(!this.isRollDouble())
		{
			if(this.nums[0] == num || this.nums[1] == num)
			{
				return !consumed[nums[0] == num ? 0 : 1];
			}
			else if(this.nums[0]+this.nums[1] == num)
				return this.totalConsumed() == 2;
			return false;
		}
		else
		{
			int c =   (4-(num/nums[0]));
			return this.totalConsumed() <= c ;
		}
	}

	public void consumeAll()
	{
		for (int b = 0; b < 4; b++)
		{
			consumed[b] = true;
		}
	}

	/**
	 * Checks whether the player made all their moves.
	 * 
	 */
	public boolean consumedAll()
	{
		for (int i = 0; i < 4; i++)
		{
			if(!consumed[i])
			{
				return false;
			}
		}
		return true;
	}

	public void consumeDiceWithNum(int roll)
	{
		if(roll == this.getTotal()) //check for multimove and consume all dice
		{
			consumeAll();
		}
		else if(this.isRollDouble()) //consume the number of dice that were used to perform the move
		{
			int temp = getFirstNotConsumedNum();
			for (int i = temp; i < temp + (roll / nums[0]); i++)
			{
				consumed[i] = true;
			}
		}
		else
		{
			for (int i = getFirstNotConsumedNum(); i < 2; i++)
			{
				if(nums[i] == roll)
				{
					consumed[i] = true;
				}
			}
		}
	}
	
	/**
	 * Consume dice with number bigger than argument t
	 * @param t
	 * @return true if an unconsumed die was found and consumed,false otherwise
	 */
	public boolean consumeBiggerThan(int t)
	{
		for(int i=0;i<4;i++)
		{
			if(nums[i]>t && !consumed[i])
			{
				consumed[i]=true;
				return true;
			}
		}
		return false;
	}
	
	public void consume(int num)
	{
		consumed[num] = true;
	}

	/**
	 * Reset the dice.
	 */
	public void reset()
	{
		for (int i = 0; i < 4; i++)
		{
			consumed[i] = false;
		}
	}

	public void setDummyDice(int b, int c)
	{
		reset();
		if(b == c)
			for (int d = 0; d < 4; d++)
				nums[d] = b;
		else
		{
			nums[0] = b;
			nums[1] = c;
			nums[2] = 0;
			nums[3] = 0;
		}
	}
}
