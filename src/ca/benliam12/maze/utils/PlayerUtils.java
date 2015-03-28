package ca.benliam12.maze.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class PlayerUtils 
{
	private static PlayerUtils instance = new PlayerUtils();
	
	public static PlayerUtils getInstance()
	{
		return instance;
	}
	
	public PlayerUtils()
	{
		
	}
	
	private ItemStack getDoor()
	{

		ItemStack door = new ItemStack(Material.BARRIER);
		ItemMeta im = door.getItemMeta();
		im.setDisplayName("§6Exit");
		door.setItemMeta(im);
		return door;
	}
	
	public void giveDoor(Player p)
	{
		p.getInventory().setItem(8, this.getDoor());
	}
	
	//TODO : Give the exit door
}
