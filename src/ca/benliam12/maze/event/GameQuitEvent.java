package ca.benliam12.maze.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ca.benliam12.maze.game.Game;

public class GameQuitEvent extends Event{

	private Player player;
	private boolean cancelled = false;
	private Game game;
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}

	public GameQuitEvent(Game game, Player player)
	{
		this.player = player;
		this.game = game;
	}
	
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
	
	public boolean isCancelled()
	{
		return this.cancelled;
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
