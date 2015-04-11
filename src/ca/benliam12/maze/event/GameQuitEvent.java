package ca.benliam12.maze.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ca.benliam12.maze.game.Game;

public class GameQuitEvent extends Event{

	private Player player;
	private Game game;
	
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

	public GameQuitEvent(Game game, Player player)
	{
		this.player = player;
		this.game = game;
	}
	
	public Game getGame()
	{
		return this.game;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
}