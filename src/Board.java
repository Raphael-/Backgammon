public class Board
{
	public int[] board;

	public Board()
	{
		board = new int[26];
		initCheckers();
	}

	/**
	 * Places all checkers at their initial positions.
	 * 
	 */

	public void initCheckers()
	{
		for (int i = 0; i < board.length; i++)
		{
			if(i == 0)
				board[i] = -2;
			else if(i == 5 || i == 12)
				board[i] = 5;
			else if(i == 11 || i == 18)
				board[i] = -5;
			else if(i == 16)
				board[i] = -3;
			else if(i == 7)
				board[i] = 3;
			else if(i == 23)
				board[i] = 2;
		}
	}// end of initCheckers

	public boolean isValid(int player, int cgroup)
	{
		if(player == 1)
		{
			if(cgroup >= 0 || cgroup == -1)
				return true;
		}
		else
		{
			if(cgroup <= 0 || cgroup == 1)
				return true;
		}
		return false;
	}// end of isValid

	/**
	 * Moves a checker using from/to values
	 * 
	 * @param player
	 *            the number of the player that will make the move
	 * @param from
	 *            the source of the move
	 * @param to
	 *            the destination of the move
	 * @return true if move was successfull and false otherwise
	 */
	public boolean move(int player, int from, int to)
	{
		int diff =   (from - to);
		//check if the direction of movement is correct
		if(diff < 0 && player == 1)
			return false;
		else if(diff > 0 && player == 2 && from!=25)
			return false;
		if(inBounds(to))
		{
			if(isValid(player, this.board[to]))
			{
				int r = Integer.signum(this.board[from]);
				this.board[from] -= r;
				if(-r == this.board[to])
				{
					this.board[player == 1 ? this.gyard(2) : this.gyard(1)] += board[to];
					this.board[to] = 0;
				}
				this.board[to] += r;
				return true;
			}
		}
		return false;
	}// end of move

	/**
	 * Checks if player has graveyard
	 * 
	 * @param board
	 * @param player
	 * @return
	 */
	public boolean hasGyard(int player)
	{
		return this.board[gyard(player)] != 0;
	}// end of hasGyard

	// Returns whether player "owns" this checker or not.
	public boolean playerHas(int player, int cgroup)
	{
		if(player == 1)
			return cgroup > 0;
		else
			return cgroup < 0;
	}

	// Returns whether the position is inside board bounds or not(excluding
	// graveyards).
	public boolean inBounds(int pos)
	{
		return pos >= 0 && pos < 24;
	}

	public int gyard(int player)
	{
		return   (player == 1 ? 24 : 25);
	}

	public int checkersNumber(int index)
	{
		return this.board[index];
	}
	
	/**
	 * Counts and returns checkers number
	 * @param playerID
	 */
	public int totalCount(int playerID)
	{
		int count=0;
		for(int i=0;i<board.length;i++)
		{
			if(board[i]>0 && playerID==1)
				count++;
			else if(board[i]<0 && playerID==2)
				count++;
		}
		return count;
	}
	
	// Returns the amount of checkers inside player's home.
	public int homeCount(int player)
	{
		int count = 0;
		for (int i = 0; i < 6; i++) //white's home
		{
			if(this.playerHas(player, this.board[i]))
				count += this.board[i];
		}
		return count;
	}
	
	/**
	 * Checks if player has checker inside his home before given point argument 
	 * @param point
	 * @return
	 */
	private boolean hasPrev(int point)
	{
		for(int i= point+1; i<6; i++)
		{
			if(board[i]>0)
				return true;
		}
		return false;
	}
	
	// Return the position to start counting from when moving out of graveyard.
	public int countFrom(int player)
	{
		return player == 1 ? 24 : -1;
	}
	
	public boolean canGather(int pos,Dice dice)
	{
		if(homeCount(1)<totalCount(1))
		{
			return false;
		}
		else
		{
			int i=0;
			while(i<4)
			{
				if(dice.getDie(i)-1 == pos && !dice.isConsumed(i))
				{
					dice.consume(i);
					return true;
				}
				i++;
			}
			if(!hasPrev(pos) && dice.consumeBiggerThan(pos))
			{
				return true;
			}
			return false;
		}
	}
	
	/**
	 * Sents the checker at player's point to graveyard
	 * @param point
	 */
	public void sendToGraveyard(int point)
	{
		board[point]--;
		board[this.gyard(1)]++;
	}
	
	public void removeChecker(int point)
	{
		if(board[point] > 0)
			board[point]--;
		else if(board[point] < 0)
			board[point]++;
	}
}
