package ca.benliam12.maze.listeners;

import ca.benliam12.maze.debuggers.DebugPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.game.Game;
import ca.benliam12.maze.game.GameManager;
import ca.benliam12.maze.signs.SignManager;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener{
	
	private GameManager gm = GameManager.getInstance();
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if(gm.getGame(e.getPlayer()) != null)
		{
			gm.getGame(e.getPlayer()).leavePlayer(e.getPlayer());
		}

		DebugPlayerManager.getInstance().leavePlayer(e.getPlayer());
	}

	@EventHandler
	public void OnPlayerJoin(PlayerJoinEvent e)
	{
		DebugPlayerManager.getInstance().addPlayer(e.getPlayer());
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
				if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.DIAMOND_BLOCK)
				{
					game.finishPlayer(player);
					return;
				}

				if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GOLD_BLOCK)
				{
						// CHECK TP PADS;

						game.tpPad(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation());
						player.sendMessage(Maze.prefix + ChatColor.RED + "TP PAD IS COMMING");
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e)
	{
		if(e.getLine(0).equalsIgnoreCase("maze"))
		{
			try
			{
				e.setCancelled(true);
				String signgame = e.getLine(1);
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
		if(e.getBlock().getType() == Material.SIGN || e.getBlock().getType() == Material.WALL_SIGN)
		{
			SignManager.getInstance().updateSigns();
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
	public void onPlayerDrop(PlayerDropItemEvent e)
	{
		Game game = GameManager.getInstance().getGame(e.getPlayer());
		if(game != null)
		{
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
			ItemStack itemStack = player.getItemInHand();
			if(itemStack.hasItemMeta())
			{
				if(itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLACK + "Exit") && itemStack.getType() == Material.BARRIER)
				{
					if(GameManager.getInstance().getGame(player) != null)
					{
						GameManager.getInstance().removePlayer(player);
						return;
					}
				}
			}

			Block block = e.getClickedBlock();
			
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
