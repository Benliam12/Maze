package ca.benliam12.maze.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.game.Game;
import ca.benliam12.maze.game.GameManager;
import ca.benliam12.maze.signs.SignManager;

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
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		Game game = gm.getGame(player);
		if(game != null)
		{
			if(game.getState().equalsIgnoreCase("inprocess"))
			{
				if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.DIAMOND_BLOCK){
					game.finishPlayer(player);
				}
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e)
	{
		if(e.getLine(0).equalsIgnoreCase(ChatColor.BLACK + "maze"))
		{
			try
			{
				e.setCancelled(true);
				String signgame = e.getLine(1).replaceAll("§0", "");
				int SignGameID = Integer.parseInt(signgame);
				if(SignManager.getInstance().getSign(e.getBlock().getLocation()) != null)
				{
					SignManager.getInstance().getSign(e.getBlock().getLocation()).setGameID(SignGameID);
					SignManager.getInstance().updateSign(SignGameID);
				}
				else
				{
					SignManager.getInstance().createSign(e.getBlock().getLocation(), SignGameID);
				}
				e.getPlayer().sendMessage(Maze.prefix + ChatColor.GREEN + "Sign added !");
			}
			catch (Exception ex)
			{
				e.getPlayer().sendMessage(Maze.prefix + ChatColor.YELLOW + "You sign must be construct like this :");
				e.getPlayer().sendMessage(Maze.prefix + ChatColor.YELLOW + "Line 1 : maze");
				e.getPlayer().sendMessage(Maze.prefix + ChatColor.YELLOW + "Line 2 : GameID");
				ex.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onBreakingBlock(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		if(gm.getGame(player) != null)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerHunger(FoodLevelChangeEvent e){
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			player.setFoodLevel(20);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Clicksign(PlayerInteractEvent e)
	{
		SignManager signm = SignManager.getInstance();
		Player player = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)
		{
			Block block = e.getClickedBlock();
			
			ItemStack inHand = player.getItemInHand();
			
			if(inHand == null) return;
			
			if(player.getItemInHand().getType() == Material.WOODEN_DOOR)
			{
				if(player.getItemInHand().getItemMeta().getDisplayName() != null) return;
				if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Exit"))
				{
					gm.removePlayer(player);
					return;
				}
			}
			
			if(block == null) return;
			
			if(block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN)
			{
				if(signm.getSign(block.getLocation()) != null)
				{
					signm.getSign(block.getLocation()).joinGame(player);
					signm.updateSign(block.getLocation());
				}
			} 
			
		}
	}
	
	
}
