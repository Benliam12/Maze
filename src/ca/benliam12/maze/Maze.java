package ca.benliam12.maze;

import java.util.logging.Logger;

import ca.benliam12.hub.Hub;
import ca.benliam12.maze.debuggers.Debugger;
import ca.benliam12.maze.utils.MessageUtils;
import ca.benliam12.maze.utils.MoneyManager;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ca.benliam12.maze.commands.Commands;
import ca.benliam12.maze.game.GameManager;
import ca.benliam12.maze.listeners.PlayerListener;
import ca.benliam12.maze.signs.SignManager;
import ca.benliam12.maze.utils.SettingManager;


public class Maze extends JavaPlugin
{
	public static String prefix = ChatColor.GOLD + "[" + ChatColor.YELLOW + "Maze" + ChatColor.GOLD + "] ";
	public static String loggerPrefix = "[Maze] ";
	public static Logger log = Logger.getLogger("minecraft");
	public Logger logger = log;
	private static Maze maze;

	private boolean setupEconomy()
	{
		Economy economy = null;
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	public static Hub getHub()
	{
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("Hub");
		return (p instanceof  Hub) ? (Hub) p : null;
	}

	public static Vault getVault()
	{
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("Vault");
		return (p instanceof  Vault) ? (Vault) p : null;
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

			if(getVault() != null)
			{
				MoneyManager.getInstance().enable();
				if(!this.setupEconomy())
				{
					MoneyManager.getInstance().disable();
				}
			}

			maze = this;
			//Debug On / Off option. For developers purpose only
			Debugger.getInstance().set(true);

			PluginManager pm = Bukkit.getPluginManager();
			pm.registerEvents(new PlayerListener(), this);
			SettingManager.getInstance().setup();
			GameManager.getInstance().setup();
			SignManager.getInstance().load();
			MoneyManager.getInstance().setup();
			MessageUtils.getInstance().setup();
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

	public void Logger(String message)
	{
		this.Logger(message,1);
	}
	public void Logger(String message, int importance)
	{
		switch (importance)
		{
			case 1: log.info(loggerPrefix + message); break;
			case 2: log.warning(loggerPrefix + message); break;
			case 3: log.severe(loggerPrefix + message); break;

			default: log.info(loggerPrefix + message + " [ERROR] IMPORTANCE LEVEL COULDN'T BE FOUND! IMPORTANCE : " + importance); break;
		}
	}
}
