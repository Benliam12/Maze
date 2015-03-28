package ca.benliam12.maze.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Utils 
{
	private SettingManager sm = SettingManager.getInstance();
	private static Utils instance = new Utils();
	
	public static Utils getInstance()
	{
		return instance;
	}
	
	/**
	 * Get a location from a config file
	 * 
	 * @param path Path to the location
	 * @param config Config file
	 * @return The location gotten
	 */
	public Location getLocation(String path, FileConfiguration config)
	{
		if(config.get(path) != null){
			World w = Bukkit.getWorld(config.getString(path + ".world"));
			double x = config.getDouble(path + ".x");
			double y = config.getDouble(path + ".y");
			double z = config.getDouble(path + ".z");
			float pitch = (float)config.getDouble(path + ".pitch");
			float yaw = (float)config.getDouble(path + ".yaw");
			return new Location(w,x,y,z,yaw,pitch);
		}
		return null;
	}
	
	/**
	 * Set a location to a config file
	 * 
	 * @param path Path to the location
	 * @param name Name of the config
	 * @param config Config file
	 * @param location Location to save
	 */
	public void setConfigLocation(String path, String name, FileConfiguration config, Location location)
	{
		config.set(path + ".world", location.getWorld().getName());
		config.set(path + ".x", location.getX());
		config.set(path + ".y", location.getY());
		config.set(path + ".z", location.getZ());
		config.set(path + ".pitch", location.getPitch());
		config.set(path + ".yaw" , location.getYaw());
		sm.saveConfig(name, config);
	}
}
