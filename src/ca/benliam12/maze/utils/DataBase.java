package ca.benliam12.maze.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import ca.benliam12.maze.Maze;


public class DataBase 
{
	private static DataBase db = new DataBase();
	
	public static DataBase getInstance()
	{
		return db;
	}

	private HashMap<String,Connection> connections = new HashMap<String, Connection>();
	public synchronized boolean connexion(String name)
	{		
		FileConfiguration config = SettingManager.getInstance().getConfig("config");
		String host = config.getString(name + ".host");
		String db = config.getString(name + ".db");
		String user = config.getString(name + ".user");
		String password = config.getString(name + ".password");
		int port = config.getInt(name + ".port");
		
		
		String url = "jdbc:mysql://"+host+":"+port+"/" + db;
		try 
		{
			Connection c  = DriverManager.getConnection( url, user, password);
			this.connections.put(name, c);
			return true;
		}
		catch ( SQLException e )
		{
		    Maze.log.info(e.getMessage());
		    return false;
		}
		catch ( NullPointerException e)
		{
			return false;
		}
		catch ( Exception e )
		{
			return false;
		}
	}
	
	public Connection getConnection(String name)
	{
		return this.connections.get(name);
	}
	
	public synchronized void close(String name)
	{
		try
		{
			this.connections.get(name).close();
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public synchronized void closeConnexions()
	{
		for(Entry<String, Connection> c : this.connections.entrySet())
		{
			try {
				c.getValue().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConnect(String name)
	{
		return this.connections.containsKey(name);
	}
}
