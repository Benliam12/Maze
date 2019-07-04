package ca.benliam12.maze.signs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.game.Game;
import ca.benliam12.maze.game.GameManager;
import ca.benliam12.maze.utils.SettingManager;

public class Signs
{
	private int ID;
	private int GameID;
	private Location location;
	private SettingManager sm = SettingManager.getInstance();
	
	private void closeSign(Sign sign)
	{
		sign.setLine(1, "0/0");
		sign.setLine(3, ChatColor.RED + "" + ChatColor.BOLD + "[OFFLINE]");
		sign.update();
	}
	
	private void load()
	{
		FileConfiguration config = sm.getConfig("Signs_" + this.ID);
		if(config.get("GameID") != null)
		{
			try{
				int GameID = config.getInt("GameID");
				this.GameID = GameID;
			}
			catch(Exception ex)
			{
				SignManager.getInstance().removeSign(this.ID);
				Maze.log.info(ex.getMessage());
			}
		}
		
		if(config.get("Location") != null)
		{
			World w = Bukkit.getWorld(config.getString("Location.world"));
			double x = config.getDouble("Location.x");
			double y = config.getDouble("Location.y");
			double z = config.getDouble("Location.z");
			this.location = new Location(w,x,y,z);
		} 
		else 
		{
			SignManager.getInstance().removeSign(this.ID);
			Maze.log.info("No location config found with id : "+ this.ID);
		}
	}
	
	public Signs(int id)
	{
		this.ID = id;
		if(sm.getConfig("Signs_" + id) != null)
		{
			this.load();
			this.update();
		} else
		{
			SignManager.getInstance().removeSign(id);
			return;
		}
	}
	
	/**
	 * Update this sign
	 */
	public void update()
	{
		Block block = this.location.getWorld().getBlockAt(this.location);
		
		if(block == null)
		{
			SignManager.getInstance().deleteSign(this.ID);
			Maze.log.info("block null");
			return;
		}
		if(block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
		{
			Sign sign = (Sign) block.getState();
			Game game = GameManager.getInstance().getGame(this.GameID);
			sign.setLine(0, ChatColor.BLUE + "Maze " +  ChatColor.DARK_PURPLE + "#" + this.GameID);
			if(game != null)
			{
				sign.setLine(2,game.getName());
				if(game.getState().equalsIgnoreCase("lobby"))
				{
					if(game.getPlayer().size() >= game.getMaxPlayer())
					{
						sign.setLine(1, game.getPlayer().size() + "/" + game.getMaxPlayer());
						sign.setLine(3, ChatColor.RED + "" + ChatColor.BOLD + "[FULL]");
						sign.update();
					} else {
						sign.setLine(1, game.getPlayer().size() + "/" + game.getMaxPlayer());
						sign.setLine(3, ChatColor.GREEN + "" + ChatColor.BOLD + "[JOIN]");
						sign.update();
					}
				} 
				else if(game.getState().equalsIgnoreCase("inprocess"))
				{
					sign.setLine(1, game.getPlayer().size() + "/" + game.getMaxPlayer());
					sign.setLine(3, ChatColor.RED + "" + ChatColor.BOLD + "[IN PROCESS]");
					sign.update();
				}
				else if(game.getState().equalsIgnoreCase("off"))
				{
					this.closeSign(sign);
				}

			}
			else
			{
				sign.setLine(2, "Null");
				this.closeSign(sign);
			}
		} 
		else
		{
			SignManager.getInstance().deleteSign(this.ID);
		}
		
	}
	
	/**
	 * Method to make a player join the game the sign is linked to 
	 * 
	 * @param p Object player
	 */
	public void joinGame(Player p)
	{
		GameManager.getInstance().addPlayer(this.GameID, p);
	}
	
	/**
	 * Set a new GameID to this sign
	 * 
	 * @param id new GameID
	 */
	public void setGameID(int id)
	{
		this.GameID = id;
		this.update();
		FileConfiguration config = sm.getConfig("Signs_" + this.ID);
		config.set("GameID", id);
		sm.saveConfig("Signs_" + this.ID, config);
	}
	
	/**
	 * Get the sign ID
	 * 
	 * @return Id of the sign
	 */
	public int getID()
	{
		return this.ID;
	}
	
	/**
	 * Get GameID
	 * 
	 * @return Game Id of this sign
	 */
	public int getGameID()
	{
		return this.GameID;
	}
	
	/**
	 * Checking if ths sign is a this location
	 * 
	 * @param loc Location to compare
	 * @return True / False
	 */
	public boolean isLocation(Location loc)
	{
		return (this.location.getBlockX() == loc.getBlockX() && this.location.getBlockY() == loc.getBlockY() && this.location.getBlockZ() == loc.getBlockZ());
	}
}
