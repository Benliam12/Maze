package ca.benliam12.maze;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.benliam12.hub.Hub;

import ca.benliam12.maze.commands.Commands;
import ca.benliam12.maze.game.GameManager;
import ca.benliam12.maze.listeners.PlayerListener;
import ca.benliam12.maze.utils.SettingManager;

public class Maze extends JavaPlugin
{
	public static String prefix = ChatColor.GOLD + "[" + ChatColor.YELLOW + "Maze" + ChatColor.GOLD + "] ";
	public static Logger log = Logger.getLogger("minecraft");
	
	public static Hub getHub()
	{
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("Hub");
		if(p instanceof Hub)
		{
			return (Hub) p;
		}
		else
		{
			return null;
		}
	}
	
	public void onEnable()
	{
		if(getHub() == null)
		{
			log.severe("You must have Hub install on your server !");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerListener(), this);
		SettingManager.getInstance().setup();
		GameManager.getInstance().setup();
		getCommand("maze").setExecutor(new Commands());
	}
	
	public void onDisable()
	{
		SettingManager.getInstance().clear();
	}
}
