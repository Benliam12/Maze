package ca.benliam12.maze.utils;

import java.util.ArrayList;

import org.bukkit.ChatColor;
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
	
	public void getDoor(Player p)
	{
		p.getInventory().setItem(8, this.getDoor());
	}
	
	public ItemStack getDoor()
	{
		ItemStack door = new ItemStack(Material.WOODEN_DOOR);
		ItemMeta im = door.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Exit");
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_PURPLE + "Right click to exit your game");
		im.setLore(lore);
		door.setItemMeta(im);
		return door;
	}
	
	public void clearInventory(Player p)
	{
		p.getInventory().clear();
		p.updateInventory();
	}
}
