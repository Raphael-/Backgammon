import java.util.ArrayList;

public class Computer extends Player
{
	private Expectiminimax emm;

	public Computer(Integer pn, Game game)
	{
		super(pn, "TavliaroBot", game);
		emm = new Expectiminimax();
	}

	public void play()
	{
		GameState init = new GameState(this.getGame().getCurrentBoard(), this.getGame().getDice());
		GameState res = emm.chooseMove(init);
		if(res == null) //no moves to play
		{
			this.getGame().getDice().consumeAll();
			return;
		}
		calcMoves(this.getGame().getCurrentBoard().board, res.board, this.getGame().getDice());
		this.getGame().getDice().consumeAll();
		System.out.println(res); //print the result of the algorithm in the console
	}

	/**
	 * Find which moves took place at init GameState that produced res GameState Perform those moves
	 * 
	 * @param init
	 *            initial GameState
	 * @param init
	 *            result GameState
	 */
	private void calcMoves(int[] init, int[] dest, Dice dice)
	{
		ArrayList<Integer> from = new ArrayList<Integer>();
		ArrayList<Integer> to = new ArrayList<Integer>();
		if(init[25] != dest[25])
		{
			int counter = dest[25] - init[25];
			while (counter > 0)
			{
				from.add(25);
				counter--;
			}
		}
		calcFromTo(init, dest, from, to);
		calcMoves(dice, from, to,dest);
		if(!to.isEmpty())
		{
			for(int i:to)
			{
				if(init[i]>0) //Player was hit during a multiple move
				{
					this.getGame().getBoardAnimation().getCurrentBoard().sendToGraveyard(i);
				}
				
			}
		}
		if(!from.isEmpty()) //gathering move for Computer
		{
				for(int i:from)
				{
					this.getGame().getBoardAnimation().removeChecker(i);
				}
		}
	}

	private static void calcFromTo(int[] init, int[] dest, ArrayList<Integer> diff, ArrayList<Integer> to)
	{
		int counter = 0;
		for (int i = 0; i < 24; i++)
		{
			if(init[i] < dest[i])
			{
				counter = dest[i] - init[i];
				while (counter > 0)
				{
					diff.add(i);
					counter--;
				}
			}
			else if(init[i] > dest[i])
			{
				if(Math.abs(dest[i]) != init[i])
				{
					counter = init[i] - dest[i];
				}
				else
					counter = 1;
				while (counter > 0)
				{
					to.add(i);
					counter--;
				}
			}
		}
	}

	public void calcMoves(Dice dice, ArrayList<Integer> from, ArrayList<Integer> to, int[] dest)
	{
		ArrayList<Integer> temp=new ArrayList<Integer>();
		temp.addAll(from);
		int maxDie=0,minDie=0;
		if(dice.getDie(0)>dice.getDie(1))
		{
			maxDie=dice.getDie(0);
			minDie=dice.getDie(1);
		}
		else
		{
			maxDie=dice.getDie(1);
			minDie=dice.getDie(0);
		}
		for (int i=0;i<temp.size();i++)
		{
			int f=temp.get(i);
			if(f==25)
				f=-1;
			if(!dice.isRollDouble())
			{
				if(to.contains(f + minDie) && dest[f + minDie]!=0)
				{
					to.remove(Integer.valueOf(f + minDie));
					this.getGame().getBoardAnimation().animateMove(f==-1?25:f, f + minDie);
					from.remove(Integer.valueOf(temp.get(i)));
				}
				else if(to.contains(f + maxDie) && dest[f + maxDie]!=0)
				{
					to.remove(Integer.valueOf(f + maxDie));
					this.getGame().getBoardAnimation().animateMove(f==-1?25:f, f + maxDie);
					from.remove(Integer.valueOf(temp.get(i)));

				}
				else if(to.contains(f + maxDie + minDie))
				{
					to.remove(Integer.valueOf(f + maxDie + minDie));
					this.getGame().getBoardAnimation().animateMove(f==-1?25:f, f + maxDie + minDie);
					from.remove(Integer.valueOf(temp.get(i)));
				}
			}
			else
			{
				if(to.contains(f + minDie) && dest[f + minDie]!=0)
				{
					to.remove(Integer.valueOf(f + minDie));
					this.getGame().getBoardAnimation().animateMove(f==-1?25:f, f + minDie);
					from.remove(Integer.valueOf(temp.get(i)));
				}
				else if(to.contains(f + (2 * minDie)) && dest[f +2* minDie]!=0)
				{
					to.remove(Integer.valueOf(f + (2 * minDie)));
					this.getGame().getBoardAnimation().animateMove(f==-1?25:f, f + (2 * minDie));
					from.remove(Integer.valueOf(temp.get(i)));
				}
				else if(to.contains(f + (3 * minDie)) && dest[f +3* minDie]!=0)
				{
					to.remove(Integer.valueOf(f + (3 * minDie)));
					this.getGame().getBoardAnimation().animateMove(f==-1?25:f, f + (3 * minDie));
					from.remove(Integer.valueOf(temp.get(i)));
				}
				else if(to.contains(f + (4 * minDie)))
				{
					to.remove(Integer.valueOf(f + (4 * minDie)));
					this.getGame().getBoardAnimation().animateMove(f==-1?25:f, f + (4 * minDie));
					from.remove(Integer.valueOf(temp.get(i)));
				}
			}
		}
	}

}
