package ca.benliam12.maze.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ca.benliam12.maze.game.GameManager;

public class PlayerListener implements Listener{
	
	private GameManager gm = GameManager.getInstance();
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if(gm.getGame(e.getPlayer()) != null)
		{
			gm.getGame(e.getPlayer()).leavePlayer(e.getPlayer());
		}
	}
	
	
}
