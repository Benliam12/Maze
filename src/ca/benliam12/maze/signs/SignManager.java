package ca.benliam12.maze.signs;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.utils.SettingManager;

public class SignManager 
{
	private SettingManager sm = SettingManager.getInstance();
	private ArrayList<Signs> signs = new ArrayList<>();
	private static SignManager instance = new SignManager();
	
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
						this.signs.add(new Signs(SignID));
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
		this.getSign(id).update();
	}
	
	public void removeSign(int id)
	{
		if(this.getSign(id) != null)
		{
			this.signs.remove(id);
		}
	}
	
	public void addSign(int id)
	{
		if(this.getSign(id) == null)
		{
			this.signs.add(new Signs(id));
		}
	}
	
	public Signs getSign(int id)
	{
		for(Signs signs : this.signs)
		{
			if(signs.getID() == id){
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
