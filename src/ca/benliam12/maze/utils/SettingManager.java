package ca.benliam12.maze.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ca.benliam12.maze.Maze;


public class SettingManager 
{
	private static SettingManager instance = new SettingManager();
	private HashMap<String,FileConfiguration> configs = new HashMap<>();
	private HashMap<String, File> files = new HashMap<>();
	
	/*
	 * Private methods
	 */
	
	private void addFile(String name,File f)
	{
		if(isFile(f))
		{
			if(!isLoadedFile(f.getName()))
			{
				files.put(name, f);
			}
		} else {
			try{
				f.createNewFile();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	private void removeFile(String name)
	{
		if(files.containsKey(name))
		{
			files.remove(name);
		}
	}
	
	@SuppressWarnings("unused")
	private void DeleteFile(File f)
	{
		if(f.exists())
		{
			try
			{
				f.delete();
				Maze.log.info("File : "+ f.getName() + " is now deleted !");
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		} 
		else 
		{
			Maze.log.info("Couldn't delete file : " + f.getName() + " (File doesn't exist !)");
		}
	}
	
	public static SettingManager getInstance(){
		return instance;
	}
	
	/*
	 * Public methods
	 */
	
	/**
	 * Creating directories & adding configs
	 * 
	 */
	public void setup()
	{
		createDirectory("plugins/Maze/arenas");
		createDirectory("plugins/Maze/signs");
	}
	/*
	 * Configuration interactions
	 */
	/**
	 * Method to create directories
	 * 
	 * @param directory Path to directory
	 */
	public void createDirectory(String directory)
	{
		File f = new File(directory);
		if(!f.exists())
		{
			try
			{
				f.mkdir();
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	/**
	 * Method to create a file
	 * 
	 * @param name File name
	 * @param directory Path to the file
	 */
	public void createFile(String name, String directory)
	{
		File f = new File(directory,name);
		
		if(!f.exists()){
			try
			{
				f.createNewFile();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	/**
	 * Method to add a Config in the list
	 * 
	 * @param name Name of the config. Use this name to get the config
	 * @param directory Path to get to the config file
	 */
	public void addConfig(String name,String directory)
	{
		if(!this.isConfig(name))
		{
			FileConfiguration config = YamlConfiguration.loadConfiguration(this.getFile(name + ".yml",directory));
			addFile(name,this.getFile(name + ".yml",directory));
			configs.put(name,config);
			Maze.log.info("Added config with name : " + name);
		} 
		else 
		{
			Maze.log.info("Couldn't add config with name : " + name);
		}
	}
	
	/**
	 * Method to remove a config from the list
	 * 
	 * @param name
	 */
	public void removeConfig(String name){
		if(this.isConfig(name))
		{
			configs.remove(name);
			removeFile(name);
			Maze.log.info("Removed config : " + name);
		} else {
			Maze.log.info("Couldn't remove config with name : " + name);
		}
	}
	
	/**
	 * Method to delete config file
	 * 
	 * @param name Name of the config
	 */
	public void deleteConfig(String name)
	{
		if(this.getFile(name) != null)
		{
			try
			{
				this.getFile(name).delete();
				this.removeConfig(name);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Method to clear the SettingManager
	 */
	public void clear()
	{
		for(Entry<String, FileConfiguration> config : this.configs.entrySet())
		{
			this.saveConfig(config.getKey(), config.getValue());
		}
		files.clear();
		configs.clear();
		Maze.log.info("Removed all Files & Configs");
	}


	public void saveConfig(String name,FileConfiguration config)
	{
		if(config != null)
		{
			try
			{
				config.save(files.get(name));
				configs.put(name,config);
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		} 
		else 
		{
			Maze.log.info("Config null");
		}
	}

	
	/**
	 * Checking if file exists
	 * 
	 * @param name
	 * @param directory Path to the file
	 */
	public boolean isFile(String name, String directory)
	{
		File f = new File(directory, name);
		return f.exists();
	}
	
	/**
	 * Checking if file exists
	 * 
	 * @param file
	 */
	public boolean isFile(File file)
	{
		return file.exists();
	}
	
	/**
	 * Getting a specific file
	 * 
	 * @param name
	 * @param directory Path to the file
	 * @return file
	 */
	public File getFile(String name, String directory)
	{
		File f = new File(directory,name);
		if(!f.exists())
		{
			try
			{
				f.createNewFile();
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return f;
	}
	
	public File getFile(String name)
	{
		return this.files.get(name);
	}
	
	/**
	 * Listing all files in a specific directory
	 * 
	 * @param directory
	 * @return Array of file
	 */
	public ArrayList<File> listFile(String directory)
	{
		ArrayList<File> file = new ArrayList<>(); 
		File d = new File(directory); 
		
		File[] files = d.listFiles(); 
		for(File f : files)
		{
			file.add(f);
		}
		return file;
	}
	
	/**
	 * Checking if a specific config has been registered
	 * 
	 * @param name
	 * @return
	 */
	public boolean isConfig(String name)
	{
		return configs.containsKey(name);
	}
	
	/**
	 * Checking if a specific file has been registered
	 * 
	 * @param name
	 * @return
	 */
	public boolean isLoadedFile(String name)
	{
		return files.containsKey(name);
	}
	
	/**
	 * Method to get a specific config
	 * 
	 * @param name Registered name of the config
	 * @return config
	 */
	public FileConfiguration getConfig(String name)
	{
		if(configs.get(name) != null)
		{
			return configs.get(name);
		} 
		else
		{
			Maze.log.info("Config : " + name + " = null");
			return null;
		}
	}
	
	/**
	 * Count the number of file in a specific directory
	 * 
	 * @param directory
	 * @return Number of file
	 */
	public int countFile(String directory)
	{
		File d = new File(directory);
		List<String> r = new ArrayList<>();
		if(d.exists())
		{
			if(d.isDirectory())
			{
				File f[] = d.listFiles();
				for(File f2 : f)
				{
					if(f2.getName().contains(".yml")) r.add(f2.getName());
				}
				return r.size();
			}
			else 
			{
				return 0;
			}
		}
		else 
		{
			return 0;
		}
	}
}
