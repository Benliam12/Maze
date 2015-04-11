package ca.benliam12.maze.game;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ca.benliam12.maze.Maze;

public class CountDown extends Thread
{
	private Game game;
	private int time;
	private ArrayList<Integer> wait = new ArrayList<>();
	private boolean started = false;
	
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
		this.started = false;
	}
	
	public void startCountDown()
	{
		this.started = true;
	}
	
	/**
	 * Restarting the countdown with new values
	 * 
	 * @param game Object game
	 */
	public void restartCountDown(Game game)
	{
		this.started = false;
		this.time = 30;
		this.game = game;
	}
	
	public void go()
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
				for(Player player : this.game.getPlayer())
				{
					player.setLevel(this.time);
				}
				
				this.time--;	
			}
		}		
	}
}
