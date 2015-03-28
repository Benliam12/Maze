package ca.benliam12.maze.game;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import ca.benliam12.maze.Maze;

public class CountDown extends Thread
{
	private Game game;
	private int time;
	private ArrayList<Integer> wait = new ArrayList<>();
	private boolean started = false;
	private boolean running = true;
	
	/**
	 * Constructor
	 * 
	 * @param ID Game ID
	 * @param time Duration of the timer in SECONDS
	 */
	public CountDown(Game game, int time, int[] wait)
	{
		this.game = game;
		this.time = time;
		for(Integer integer : wait)
		{
			this.wait.add(integer);
		}
	}
	
	/**
	 * Set the initial time
	 * 
	 * @param time New initial time
	 */
	public void setTimer(int time)
	{
		this.time = time;
	}
	
	/**
	 * Set a attached game
	 * 
	 * @param game Object Game
	 */
	public void setGame(Game game)
	{
		this.game = game;
	}
	
	/**
	 * Set if the time can start
	 * 
	 * @param canstart True/False
	 */
	public void setCanStart(boolean canstart)
	{
		this.started = canstart;
	}

	public void stopCountDown()
	{
		this.running = false;
	}
	
	public void startCountDown()
	{
		this.running = true;
	}
	
	/**
	 * Restarting the countdown with new values
	 * 
	 * @param game Object game
	 */
	public void restartCountDown(Game game)
	{
		this.running = true;
		this.time = 30;
		this.game = game;
	}
	
	@Override
	public void run()
	{
		while(this.running)
		{
			if(this.game != null)
			{
				if(this.game.canStart() && this.game.getState().equalsIgnoreCase("lobby") && this.started)
				{
					if(this.time == 0)
					{
						this.game.broadcast(Maze.prefix + ChatColor.GREEN + "Game has started !");
						this.game.start();
					}
					else 
					{
						if(this.wait.contains(this.time))
						{
							this.game.broadcast(Maze.prefix + ChatColor.GREEN + "Game start in : " + this.time + " seconds");
						}
						
						this.time--;	
					}
					try
					{
						Thread.sleep(1000);
					}
					catch (Exception ex)
					{
						this.stopCountDown();
					}
				}	
			}

		}
	}
}
