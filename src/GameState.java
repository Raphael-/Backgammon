import java.util.ArrayList;
import java.util.Arrays;


public class GameState
{
	int[] board;
	int[] dice;
	private double score;

	public GameState()
	{
		board = new int[26];
		dice = new int[4];
		this.setDice(3, 4);
		for (int i = 0; i < board.length; i++)
		{
                    if(i == 0)
                        board[i] = 2;
                    if(i == 1)
                        board[i] = 2;
                    if(i == 2)
                        board[i] = 2;
                    if(i == 3)
                        board[i] = 2;
                    if(i == 4)
                        board[i] = 2;
                    if(i == 5)
                        board[i] = 2;
                    if(i == 10)
                        board[i] = -2;
//			if(i == 0)
//				board[i] = -2;
//			else if(i == 5 || i == 12)
//				board[i] = 5;
//			else if(i == 11 || i == 18)
//				board[i] = -5;
//			else if(i == 16)
//				board[i] = -3;
//			else if(i == 7)
//				board[i] = 3;
//			else if(i == 23)
//				board[i] = 2;
		}
	}

	public GameState(int[] board)
	{
		this.board = board.clone();
	}

	public GameState(int[] board, int[] dice)
	{
		this.board = board.clone();
		if(dice[0] < dice[1])
			this.setDice(dice[0], dice[1]);
		else
			this.setDice(dice[1], dice[0]);
	}

	public GameState(Board currentBoard, Dice dice)
	{
		this.board = new int[26];
		int[] temp = dice.getDiceResults();
		if(temp[0] < temp[1])
			this.setDice(temp[0], temp[1]);
		else
			this.setDice(temp[1], temp[0]);
		// Fill the int board using the actual positions of checkers in the
		// Board b.
		for (byte i = 0; i < 26; i++)
		{
			board[i] = currentBoard.checkersNumber(i);
		}
	}

	public void setDice(int i, int j)
	{
		this.dice = new int[4];
		this.dice[0] = i;
		this.dice[1] = j;
		if(i == j)
			dice[2] = dice[3] = i;
		else
			dice[2] = dice[3] = 0;
	}

	public double evaluate(int player)
	{
		double eval = 0;
		int doorsMx=0;
		int doorsMn=0;
		int totalMx=0;
		int totalMn=0;
		int distMx=0;
		int distMn=0;
		int tmp=0;
		for (int b = 0; b < 24; b++)
		{
			if(this.board[b]==0)
				continue;
			if(this.board[b]>0)
			{
				if(this.board[b]>1)
					doorsMn++;
				if(this.board[gyard(1)]==0)
					tmp=b;
				else
					tmp=gyard(1);
				if(tmp>distMn)
					distMn=tmp;
				totalMn+=this.board[b];
			}
			else
			{
				if(this.board[b]<-1)
					doorsMx++;
				if(this.board[gyard(2)]==0)
					tmp=24-b;
				else
					tmp=gyard(2);
				if(tmp>distMx)
					distMx=tmp;
				totalMx+=this.board[b];
			}
		}
		if(player == 1)
		{
			eval = (-0.025 * (doorsMn - doorsMx)) - (0.02525 * (Math.abs(board[this.gyard(2)]) - board[this.gyard(1)])) + 0.4*totalMn + 0.0125*distMn;
		}
		else
		{
			eval = (0.025 * (doorsMx - doorsMn)) + (0.02525 * (board[this.gyard(1)] - Math.abs(board[this.gyard(2)]))) - 0.4*totalMx - 0.0125*distMx;
		}
		return eval;
	}

	public ArrayList<GameState> getChildren(int player)
	{
		int diceLeft = (this.dice[0] == this.dice[1] ? 4 : 2);
		if(!this.hasGyard(this.board, player))
		{
			ArrayList<GameState> children = new ArrayList<GameState>(30);
			this.getChildren(children, player, 0, diceLeft - 1, this.board);
			this.setDice(dice[1], dice[0]); //swap dice
			this.getChildren(children, player, 0, diceLeft - 1, this.board);
			if(!children.isEmpty())
			{
				return children;
			}
			else //cannot find children,will check for partial moves only
			{
				if(diceLeft==2)
				{
					diceLeft--;
					this.getChildren(children, player, 0, diceLeft - 1, this.board);
					this.setDice(dice[1], dice[0]);
					this.getChildren(children, player, 0, diceLeft - 1, this.board);
					return children;
				}
				else //doubles
				{
					while(diceLeft>0)
					{
						this.getChildren(children, player, 0, diceLeft - 1, this.board);
						this.setDice(dice[1], dice[0]);
						this.getChildren(children, player, 0, diceLeft - 1, this.board);
						if(!children.isEmpty())
							return children;
						diceLeft--;
					}
				}
			}
			return children;
		}
		else
		{
			ArrayList<GameState> children = new ArrayList<GameState>(20);
			int[][] noGy = null;
			if(Math.abs(board[this.gyard(player)])>=2 || diceLeft == 4)
				 noGy= new int[1][]; //at most two possible boards when moving out of graveyard
			else
				 noGy= new int[2][];
			int op = player == 1 ? -1 : 1;
			int moveLoc=0;
			if(diceLeft == 4) //doubles
			{
				noGy[0] = this.board.clone();
				while(this.hasGyard(noGy[0], player) && diceLeft!=0)
				{
					moveLoc = this.countFrom(player) + (op * this.dice[--diceLeft]);
					if(this.isValid(player, this.board[moveLoc]))
					{
						this.move(player, noGy[0], this.gyard(player), moveLoc);
					}
					else //can't place a checker from graveyard,return
					{
						return children;
					}
				}
				this.getChildren(children,player, 0, diceLeft-1, noGy[0]);
				return children;
			}
			else
			{
				if(Math.abs(board[this.gyard(player)])<2)
				{
					moveLoc = this.countFrom(player) + (op * this.dice[0]);
					if(this.isValid(player, this.board[moveLoc]))
					{
						noGy[0] = this.board.clone();
						this.move(player, noGy[0], this.gyard(player), moveLoc);
						diceLeft--;
						this.setDice(dice[1], dice[0]);
						this.getChildren(children,player, 0, diceLeft-1, noGy[0]);
						this.setDice(dice[1], dice[0]);
						diceLeft++;
					}
					moveLoc = this.countFrom(player) + (op * this.dice[1]);
					if(this.isValid(player, this.board[moveLoc]))
					{
						diceLeft--;
						noGy[1] = this.board.clone();
						this.move(player, noGy[1], this.gyard(player), moveLoc);
						this.getChildren(children,player, 0, diceLeft-1, noGy[1]);
					}
					return children;
				}
				else
				{
					noGy[0] = this.board.clone();
					moveLoc = this.countFrom(player) + (op * this.dice[0]);
					if(this.isValid(player, noGy[0][moveLoc]))
					{
						this.move(player, noGy[0], this.gyard(player), moveLoc);
					}
					moveLoc=this.countFrom(player) + (op * this.dice[1]);
					if(this.isValid(player, noGy[0][moveLoc]))
					{
						this.move(player, noGy[0], this.gyard(player), moveLoc);
					}
					children.add(this);
					return children;
				}
					
			}
		}
	}

	/**
	 * 
	 * @param children
	 *            : the ArrayList<State> will be populated by legal states
	 * @param player
	 *            : player for whom getChildren is called
	 * @param startLoc
	 *            : search of possible move will start from startLoc
	 * @param diceLeft
	 *            : number of unconsumed dice
	 * @param board
	 *            : representation of the current board
	 */
	public void getChildren(ArrayList<GameState> children, int player, int startLoc, int diceLeft, int[] board)
	{
		if(diceLeft == -1) // no more dice left,add the new state
		{
			children.add(new GameState(board));
			return;
		}
		// find the direction of movement
		int op = player == 1 ? -1 : 1;
		for (int i = startLoc; i < 24; i++)
		{
			// copy the board
			// int[] tempBoard = this.deepCopy(board);
			int[] tempBoard = board.clone();
			// if player is the owner of board[i] group then we might find a
			// move from there
			if(this.playerHas(player, board[i]))
			{
				int moveLoc = i + (op * this.dice[diceLeft]);
				
				if(this.canGather(player, tempBoard, i, diceLeft))
				{
					this.gather(player, tempBoard, i);
					this.getChildren(children, player, i, diceLeft - 1, tempBoard);
				}
				else if(this.inBounds(moveLoc)) // if destination is inside bounds
				{
					// if destination is a valid location
					if(this.isValid(player, board[moveLoc]))
					{
						// perform the move at the tempBoard
						this.move(player, tempBoard, i, moveLoc);
						// recursive call of getChildren to find other possible
						// moves using tempBoard
						this.getChildren(children, player, i, diceLeft - 1, tempBoard);
					}
				}				
			}
		}
	}

	// Return the position to start counting from when moving out of graveyard.
	public int countFrom(int player)
	{
		return player == 1 ? 24 : -1;
	}

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
	}

	public void move(int player, int[] board, int from, int to)
	{
		int r = Integer.signum(board[from]);
		board[from] -= r;
		if(-r == board[to])
		{
			board[player == 1 ? this.gyard(2) : this.gyard(1)] += board[to];
			board[to] = 0;
		}
		board[to] += r;
	}

	// Removes the top checker of "from" from the board.
	public void gather(int player, int[] board, int from)
	{
		if(player == 1)
		{
			board[from] -= 1;
		}
		else
		{
			board[from] += 1;
		}
	}

	// Return true if player has a graveyard
	public boolean hasGyard(int[] board, int player)
	{
		return board[gyard(player)] != 0;
	}

	// Returns whether player "owns" this checkergroup or not.
	public boolean playerHas(int player, int cgroup)
	{
		if(player == 1)
			return cgroup > 0;
		else
			return cgroup < 0;
	}

	public int[] deepCopy(int[] arg)
	{
		return Arrays.copyOf(arg, arg.length);
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	public double getScore()
	{
		return this.score;
	}

	public boolean isTerminal()
	{
		return false;
	}

	public boolean PlayerWins()
	{
		if(this.count(1, this.board) == 0)
		{
			return true;
		}
		return false;
	}

	public boolean CPUWins()
	{
		if(this.count(2, this.board) == 0)
		{
			return true;
		}
		return false;
	}

	// Returns whether the position is inside board bounds or not(excluding
	// graveyards).
	public boolean inBounds(int pos)
	{
		return pos >= 0 && pos < 24;
	}

	public int gyard(int player)
	{
		return player == 1 ? 24 : 25;
	}

	// Returns the amount of checkers owned by the given player.
	public int count(int player, int[] board)
	{
		int count = 0;
		for (int i = 0; i < board.length; i++)
		{
			if(this.playerHas(player, board[i]))
				count += Math.abs(board[i]);
		}
		return count;
	}

	// Returns whether given player can gather with given arguments.
	public boolean canGather(int player, int[] board, int from, int dienum)
	{
		int c = this.count(player, board);
		if(c > 0)
		{
			int start = player == 1 ? 0 : 18;
			int homecount = 0;
			for (int i = 0; i < 6; i++)
			{
				if(this.playerHas(player, board[start + i]))
					homecount += Math.abs(board[start + i]);	
			}
			if(homecount == c)
                        {
                            int finish = player == 1? -1 : 24;
                            int sign = player == 1? -1 : 1;
                            if(from + sign*this.dice[dienum] == finish)
                                return true;
                            else
                            {
                                // Check if checker on previous group exists.
                                // If it does we can't gather, we can only
                                // move the previous checkers.
                                int lastInHome = player == 1? 5 : 18;
                                if(player == 1)
                                    from++;
                                else
                                    from--;
                                if(player == 1){
                                    while(from <= lastInHome)
                                    {
                                        if(this.playerHas(player, board[from]))
                                            return false;
                                        if(player == 1)
                                            from++;
                                        else
                                            from--;
                                    }
                                }
                                else
                                {
                                    while(from >= lastInHome)
                                    {
                                        if(this.playerHas(player, board[from]))
                                            return false;
                                        if(player == 1)
                                            from++;
                                        else
                                            from--;
                                    }
                                }
                            }
                            return true;
                        }
		}
		return false;
	}

	public static String print(int[] board2)
	{
		String str = "\r\n=========================================\r\n";
		str += "[12]\t[13]\t[14]\t[15]\t[16]\t[17]\t[18]\t[19]\t[20]\t[21]\t[22]\t[23]\r\n";
		byte b = 12;
		for (; b < 24; b++)
		{
			if(board2[b] == 0)
			{
				str += "empty\t";
			}
			else
			{
				str += board2[b] + "\t";
			}
		}
		b = 11;
		str += "\r\n\r\n";
		for (; b >= 0; b--)
		{
			if(board2[b] == 0)
			{
				str += "empty\t";
			}
			else
			{
				str += board2[b] + "\t";
			}
		}
		str += "\r\n[11]\t[10]\t[9]\t[8]\t[7]\t[6]\t[5]\t[4]\t[3]\t[2]\t[1]\t[0]\r\nGraveyards\r\n";
		str += "[24]\t[25]\r\n";
		str += ((board2[24] == 0) ? "empty" : board2[24]) + "\t" + ((board2[25] == 0 ? "empty" : board2[25])) + "\r\n";
		return str;
	}

	@Override
	public String toString()
	{
		String str = print(this.board);
		if(dice != null)
			str += "================dice" + dice[0] + " " + dice[1] + "=========================\n";
		return str;
	}
}
