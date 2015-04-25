package ca.benliam12.maze.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ca.benliam12.maze.game.Game;

public class GameJoinEvent extends Event{

	private Player player;
	private Game game;
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public GameJoinEvent(Game game, Player player)
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
