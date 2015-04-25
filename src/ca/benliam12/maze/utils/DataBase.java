package ca.benliam12.maze.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase 
{
	private static DataBase db = new DataBase();
	
	public static DataBase getInstance()
	{
		return db;
	}
	
	private Connection connection;
	public synchronized void connexion(String host, String db, String user, String password, int port)
	{		
		String url = "jdbc:mysql://"+host+":"+port+"/" + db;
		try {
		    this.connection = DriverManager.getConnection( url, user, password);
		} catch ( SQLException e ) {
		    e.printStackTrace();
		}
	}
	
	public Connection getConnection()
	{
		return this.connection;
	}
	
	public synchronized void close()
	{
		try
		{
			this.connection.close();
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public boolean isConnect()
	{
		if(this.connection == null)
		{
			return false;
		}
		return true;
	}
}
