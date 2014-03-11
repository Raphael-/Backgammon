
import java.util.ArrayList;
import java.util.Iterator;

public class Expectiminimax
{
	private final int MAX_DEPTH = 4; //4 because chooseMove already builds Max's children before expectiminimax is called
	private int[][] dice = {{1,1},{1,2},{1,3},{1,4},{1,5},{1,6},
            {2,2},{2,3},{2,4},{2,5},{2,6},
            {3,3},{3,4},{3,5},{3,6},
            {4,4},{4,5},{4,6},
            {5,5},{5,6},
            {6,6}};
	
	public GameState chooseMove(GameState gs)
	{
		GameState temp;
		ArrayList<GameState> list = gs.getChildren(2);
		double max_val = Double.NEGATIVE_INFINITY;
		double temp_val;
		GameState best = null;
		for (Iterator<GameState> iter = list.listIterator(); iter.hasNext();)
		{
			temp = iter.next();
			temp_val = expectiminimax(temp, 0);
			if(temp_val > max_val)
			{
				best = temp;
				max_val = temp_val;
			}
		}
		return best;
	}

	private double expectiminimax(GameState gs, int depth)
	{
		if(depth == MAX_DEPTH)
		{
			return gs.evaluate(2);
		}

		if(depth % 2 == 0) // chance node
		{
			float v = 0f;
			for(int[] diceRoll : dice)
			{

					v += (dice[0] == dice[1] ? 1.0f / 36 : 1.0f / 18) *expectiminimax(new GameState(gs.board,diceRoll), depth + 1);
			}
			return v;
		}
		else if(depth % 4 == 1) // min node
		{
			double v = Double.POSITIVE_INFINITY;
			ArrayList<GameState> list = gs.getChildren(1);
			if(list.isEmpty())
			{
				return Math.min(v, expectiminimax(gs, depth + 1));
			}
			for (Iterator<GameState> iter = list.listIterator(); iter.hasNext();)
			{
				v = Math.min(v, expectiminimax(iter.next(), depth + 1));
			}
			return v;
		}
		else
		{
			double v = Double.NEGATIVE_INFINITY;
			ArrayList<GameState> list = gs.getChildren(2);
			if(list.isEmpty())
			{
				return Math.max(v, expectiminimax(gs, depth + 1));
			}
			for (Iterator<GameState> iter = list.listIterator(); iter.hasNext();)
			{
				v = Math.max(v, expectiminimax(iter.next(), depth + 1));
			}
			return v;
		}
	}
}
