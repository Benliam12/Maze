package ca.benliam12.maze;

import java.util.logging.Logger;

import ca.benliam12.hub.Hub;
import ca.benliam12.maze.debuggers.Debugger;
import ca.benliam12.maze.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ca.benliam12.maze.commands.Commands;
import ca.benliam12.maze.game.GameManager;
import ca.benliam12.maze.listeners.PlayerListener;
import ca.benliam12.maze.signs.SignManager;
import ca.benliam12.maze.utils.SettingManager;


public class Maze extends JavaPlugin
{
	public static String prefix = ChatColor.GOLD + "[" + ChatColor.YELLOW + "Maze" + ChatColor.GOLD + "] ";
	public static Logger log = Logger.getLogger("minecraft");
	private static Maze maze;

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

	public static Maze getMaze()
	{
		return maze;
	}
	
	public void onEnable()
	{
		MessageUtils.getInstance().setup();
		try
		{
			if (getHub() == null) {
				log.severe("You must have Hub install on your server !");
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}

			maze = this;
			//Debug On / Off option. For developers purpose only
			Debugger.getInstance().set(true);

			PluginManager pm = Bukkit.getPluginManager();
			pm.registerEvents(new PlayerListener(), this);
			SettingManager.getInstance().setup();
			GameManager.getInstance().setup();
			SignManager.getInstance().load();
			getCommand("maze").setExecutor(new Commands());
			//DataBase.getInstance().connexion("localhost", "servers", "root", "", 3306);
		}
		catch (Exception exception)
		{
			if(Debugger.getInstance().isEnable())
			{
				exception.printStackTrace();
			}
		}
	}
	
	public void onDisable()
	{
		GameManager.getInstance().stop();
		SettingManager.getInstance().clear();
	}
}
