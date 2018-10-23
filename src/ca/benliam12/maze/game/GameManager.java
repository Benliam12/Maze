package ca.benliam12.maze.game;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.signs.SignManager;
import ca.benliam12.maze.utils.SettingManager;

public class GameManager
{
	private ArrayList<Game> games = new ArrayList<>();
	
	private static GameManager instance = new GameManager();
	private SettingManager sm = SettingManager.getInstance();
	private SignManager signm = SignManager.getInstance();
	private Thread thread;
	private CountDownThread cdt;
	private int bukkitTask;
	
	private int nextInt()
	{
		int id = 1;
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
		this.cdt = new CountDownThread();
		this.bukkitTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Maze.getMaze(), this.cdt, 0, 20);
		this.thread = new Thread(this.cdt);
		this.thread.start();
	}
	
	public void stop()
	{
		for(Game game : this.games)
		{
			game.stop();
		}
		this.cdt.end();

		Bukkit.getScheduler().cancelTask(this.bukkitTask);
	}

	/**
	 * Load game in Config File
	 */
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
						sm.addConfig("Arena_" + GameID, "plugins/Maze/arenas");
						this.games.add(new Game(GameID));
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
			p.sendMessage(Maze.prefix + ChatColor.RED + "You are not in game !");
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
		game.restart();
		this.addGame(game);
		signm.updateSign(id);
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
				this.getGame(ID).stop();
				this.removeGame(ID);
				sm.deleteConfig("Arena_" + ID);
				signm.updateSign(ID);
				player.sendMessage(Maze.prefix + ChatColor.GREEN + "Game deleted !");
			} 
			else
			{
				player.sendMessage(Maze.prefix + ChatColor.RED + "This game does not exists !");
			}
		} 
		else
		{
			player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
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
	
	public ArrayList<Game> getGames()
	{
		return this.games;
	}
	
	public void getGames(Player p)
	{	
		if(this.games.size() == 0)
		{
			p.sendMessage("No game avaliable");
		}
		for(Game game : this.games)
		{
			String state = ChatColor.RED + "" + ChatColor.BOLD + "[Error]";
			if(game.getState() != null){
				if(game.getState().equalsIgnoreCase("lobby"))
				{
					if(game.isfull())
					{
						state = ChatColor.RED + "" + ChatColor.BOLD + "[FULL]";
					}
					else
					{
						state = ChatColor.GREEN + "" + ChatColor.BOLD + "[Waitting]";
					}
					
				}
				else if(game.getState().equalsIgnoreCase("off"))
				{
					state = ChatColor.RED + "" + ChatColor.BOLD + "[OFF]";
				} 
				else if(game.getState().equalsIgnoreCase("inprocess"))
				{
					state = ChatColor.RED + "" + ChatColor.BOLD + "[In Process]";
				}
			}
			p.sendMessage("#" + game.getID() + " - " + game.getName() + " " + state);
		}
	}
	
	public boolean isGame(Game game)
	{
		return this.games.contains(game);
	}
}
