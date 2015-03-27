package ca.benliam12.maze.signs;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.utils.SettingManager;
import ca.benliam12.maze.utils.Utils;

public class SignManager 
{
	private SettingManager sm = SettingManager.getInstance();
	private ArrayList<Signs> signs = new ArrayList<>();
	private static SignManager instance = new SignManager();
	
	private int nextInt()
	{
		int id = 0;
		while(sm.isFile("Signs_" + id + ".yml", "plugins/Maze/signs"))
		{
			id++;
		}
		return id;
	}
	
	private Signs getSignByID(int id)
	{
		for(Signs signs : this.signs)
		{
			if(signs.getID() == id)
			{
				return signs;
			}
		}
		return null;
	}
	
	public static SignManager getInstance()
	{
		return instance;
	}
	
	public void load()
	{
		if(sm.countFile("plugins/Maze/signs") != 0)
		{
			for(File f : sm.listFile("plugins/Maze/signs"))
			{
				if(f.getName().contains(".yml"))
				{
					String string = f.getName().replaceAll(".yml","");
					string = string.replaceAll("Signs_", "");
					try
					{
						int SignID = Integer.parseInt(string);
						sm.addConfig("Signs_" + SignID, "plugins/Maze/signs");
						Signs signs = new Signs(SignID);
						this.signs.add(signs);
						signs.update();
					} catch(Exception ex){
						Maze.log.info("Could load config with name : " + string);
					}
				}
			}
		}
	}
	
	public void updateSigns()
	{
		for(Signs signs : this.signs)
		{
			signs.update();
		}
	}
	
	public void updateSign(int id)
	{
		for(Signs s : this.signs)
		{
			if(s.getGameID() == id)
			{
				s.update();	
			}
		}
	}
	
	public void updateSign(Location loc)
	{
		for(Signs signs : this.signs)
		{
			if(signs.isLocation(loc))
			{
				signs.update();
			}
		}
	}
	
	public void removeSign(int id)
	{
		if(this.getSign(id) != null)
		{
			this.signs.remove(id);
		}
	}
	
	public void addSign(Signs signs)
	{
		if(!this.signs.contains(signs))
		{
			this.signs.add(signs);
		}
	}
	
	public void createSign(Location loc, int GameID)
	{
		int signID = this.nextInt();
		sm.addConfig("Signs_" + signID, "plugins/Maze/signs");
		FileConfiguration config = sm.getConfig("Signs_" + signID);
		config.set("GameID", GameID);
		Utils.getInstance().setConfigLocation("Location", "Signs_" + signID, config, loc);
		Signs signs = new Signs(signID);
		this.addSign(signs);
	}
	
	public void deleteSign(int ID)
	{
		if(sm.getConfig("Signs_" + ID) != null)
		{
			if(this.getSignByID(ID) != null){
				this.removeSign(ID);
				sm.deleteConfig("Signs_" + ID);
				Maze.log.info("Sign DELETED ! " + ID);
			} 
		}
	}
	
	public Signs getSign(int id)
	{
		for(Signs signs : this.signs)
		{
			if(signs.getGameID() == id){
				return signs;
			}
		}
		return null;
	}
	
	public Signs getSign(Location loc)
	{
		for(Signs signs : this.signs)
		{
			if(signs.isLocation(loc))
			{
				return signs;
			}
		}
		return null;
	}
}
