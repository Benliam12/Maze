package ca.benliam12.maze.game;

import org.bukkit.ChatColor;

import ca.benliam12.maze.Maze;

public class CountDown implements Runnable
{
	private int id;
	private int time;
	private boolean started = false;
	private GameManager gm = GameManager.getInstance();
	
	/**
	 * Constructor
	 * 
	 * @param ID Game ID
	 * @param time Duration of the timer in SECONDS
	 */
	public CountDown(int ID, int time)
	{
		this.id = ID;
		this.time = time;
	}
	
	public void setTimer(int time)
	{
		this.time = time;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public void setCanStart(boolean canstart)
	{
		this.started = canstart;
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			if(gm.getGame(this.id) != null)
			{
				if(gm.getGame(this.id).canStart() && gm.getGame(this.id).getState().equalsIgnoreCase("lobby") && this.started)
				{
					if(this.time == 0)
					{
						gm.getGame(this.id).broadcast(Maze.prefix + ChatColor.GREEN + "Game has started !");
						gm.getGame(this.id).start();
					}
					else 
					{
						gm.getGame(this.id).broadcast(Maze.prefix + ChatColor.GREEN + "Game start in : " + this.time);
						this.time--;	
					}
					try
					{
						Thread.sleep(1000);
					}
					catch (Exception ex)
					{
					
					}
				}	
			}

		}
	}
}
