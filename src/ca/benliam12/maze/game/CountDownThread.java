package ca.benliam12.maze.game;

import org.bukkit.Bukkit;

import ca.benliam12.maze.Maze;

public class CountDownThread implements Runnable
{
	private boolean running = true;
	private GameManager gm = GameManager.getInstance();
	
	public void end()
	{
		this.running = false;
	}
	
	public void run()
	{
		if(this.running)
		{
			try
			{
				for(Game game : gm.getGames())
				{
					if(game.getCountDown() != null)
					{
						game.checkGameMode();
						game.getCountDown().go();
					}
					else
					{
						int[] i = {30,20,15,10,5,4,3,2,1};
						game.setCountDown(new CountDown(game, 30, i));
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Bukkit.getServer().getPluginManager().disablePlugin(Maze.getMaze());
			}
		}
	}
}
