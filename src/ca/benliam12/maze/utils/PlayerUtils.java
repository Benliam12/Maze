package ca.benliam12.maze.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Players methods that are useful
 */
public class PlayerUtils 
{
	private static PlayerUtils instance = new PlayerUtils();
	
	public static PlayerUtils getInstance()
	{
		return instance;
	}
	
	public PlayerUtils() {}
	
	private ItemStack getDoor()
	{
		ItemStack door = new ItemStack(Material.BARRIER);
		ItemMeta im = door.getItemMeta();

		if(im != null)
		{
			im.setDisplayName(ChatColor.BLACK +"Exit");
			door.setItemMeta(im);
		}

		return door;
	}

	/**
	 * Gives player the exit item in his last slot. This items gives him the possibility to leave the maze.
	 * @param p
	 */
	public void giveDoor(Player p)
	{
		p.getInventory().setItem(8, this.getDoor());
	}
}
