package ca.benliam12.maze.game;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.utils.SettingManager;
public class GameManager
{
	private ArrayList<Game> games = new ArrayList<>();
	
	private static GameManager instance = new GameManager();
	private SettingManager sm = SettingManager.getInstance();
	
	private int nextInt()
	{
		int id = 0;
		while(sm.isFile("Arena_" + id + ".yml", "plugins/Maze/arenas"))
		{
			id++;
		}
		return id;
	}
	
	public static GameManager getInstance()
	{
		return instance;
	}
	
	public GameManager()
	{
		
	}
	
	public void setup()
	{
		this.loadGames();
	}
	
	public void loadGames()
	{
		if(sm.countFile("plugins/Maze/arenas") != 0)
		{
			for(File f : sm.listFile("plugins/Maze/arenas"))
			{
				if(f.getName().contains(".yml"))
				{
					String string = f.getName().replaceAll(".yml","");
					string = string.replaceAll("Arena_", "");
					try
					{
						int GameID = Integer.parseInt(string);
						this.games.add(new Game(GameID));
						sm.addConfig("Arena_" + GameID, "plugins/Maze/arenas");
					} catch(Exception ex){
						Maze.log.info("Could load config with name : " + string);
					}
				}
			}
		}
	}
	/*
	 * Player interaction
	 */
	public void addPlayer(int GameID, Player p)
	{
		if(this.getGame(GameID) != null)
		{
			if(this.getGame(p) == null){
				this.getGame(GameID).joinPlayer(p);
			}
			else {
				p.sendMessage(Maze.prefix + ChatColor.RED + "You are already in game !");
			}
		}
		else
		{
			p.sendMessage(Maze.prefix + ChatColor.RED + "Cannot find game with id : " + GameID);
		}
	}
	
	public void removePlayer(Player p)
	{
		if(this.getGame(p) != null)
		{
			this.getGame(p).leavePlayer(p);
		} 
		else 
		{
			p.sendMessage(Maze.prefix + ChatColor.RED + "You are already in game !");
		}
	}
	/*
	 * Game interaction
	 */
	public void createGame(Player player,Location spawn)
	{
		int id = this.nextInt();
		sm.createFile("Arena_" + id + ".yml", "plugins/Maze/arenas");
		sm.addConfig("Arena_" + id, "plugins/Maze/arenas");
		Game game = new Game(id);
		game.setName(Integer.toString(id));
		game.setMaxPlayer(5);
		game.setMinPlayer(1);
		game.setSpawn(spawn);
		game.setWaitRoom(spawn);
		this.addGame(game);
		player.sendMessage(Maze.prefix + ChatColor.GREEN + "Game created ! (Id : " + id + ")");
	}
	
	public void createGame(Player player,Location spawn, String name)
	{
		
	}
	
	public void deleteGame(Player player,int ID)
	{
		if(sm.getConfig("Arena_" + ID) != null)
		{
			if(this.getGame(ID) != null){
				this.removeGame(ID);
				sm.deleteConfig("Arena_" + ID);
				player.sendMessage(Maze.prefix + ChatColor.GREEN + "Game deleted !");
			} 
			else
			{
				player.sendMessage(Maze.prefix + ChatColor.RED + " This game does not exists !");
			}
		}
	}
	
	public void removeGame(int ID)
	{
		if(this.getGame(ID) != null)
		{
			this.getGame(ID).stop();
			this.games.remove(this.getGame(ID));
		}
	}
	
	public void addGame(Game game)
	{
		if(!this.isGame(game))
		{
			this.games.add(game);
		}
	}
	
	public Game getGame(Player p)
	{
		for(Game game : this.games)
		{
			if(game.isPlayer(p.getName())) 
			{
				return game;
			}
		}
		return null;
	}
	
	public Game getGame(int id)
	{
		for(Game game : this.games)
		{
			if(game.getID() == id) 
			{
				return game;
			}
		}
		return null;
	}
	
	public boolean isGame(Game game)
	{
		return this.games.contains(game);
	}
}
