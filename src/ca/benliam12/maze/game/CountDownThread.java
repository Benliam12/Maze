package ca.benliam12.maze.game;

import org.bukkit.Bukkit;

import ca.benliam12.maze.Maze;

public class CountDownThread extends Thread
{
	private boolean running = true;
	private GameManager gm = GameManager.getInstance();
	
	public void end()
	{
		this.running = false;
	}
	
	public void run()
	{
		while(this.running)
		{
			for(Game game : gm.getGames())
			{
				if(game.getCountDown() != null)
				{
					game.getCountDown().go();
				}
				else 
				{
					int[] i = {30,20,15,10,5,4,3,2,1};
					game.setCountDown(new CountDown(game, 30, i));
				}
			}
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException ex)
			{
				Maze.log.info("Error occur on main thread !");
				Bukkit.getPluginManager().disablePlugin(Maze.getMaze());
			}
		}
	}
}
