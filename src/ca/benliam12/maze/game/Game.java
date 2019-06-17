package ca.benliam12.maze.game;

import java.util.ArrayList;
import java.util.HashMap;

import ca.benliam12.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ca.benliam12.maze.Maze;
import ca.benliam12.maze.event.GameJoinEvent;
import ca.benliam12.maze.event.GameQuitEvent;
import ca.benliam12.maze.signs.SignManager;
import ca.benliam12.maze.utils.PlayerUtils;
import ca.benliam12.maze.utils.SettingManager;
import ca.benliam12.maze.utils.Utils;
import org.bukkit.inventory.ItemStack;

/**
 * Main game class designed to work with Maze plugin
 * @author Benliam12
 * @version 1.1
 */
public class Game 
{
	private int id;
	private int maxPlayer;
	private int minPlayer;
	private long startTime;
	private Location spawn;
	private Location waitroom;
	private String state;
	private String name;
	private boolean isToggled;
	private FileConfiguration config;
	private ArrayList<Player> players = new ArrayList<>();
	private ArrayList<TpPad> tpPads = new ArrayList<>();
	private SettingManager sm = SettingManager.getInstance();
	private SignManager signm = SignManager.getInstance();
	private PlayerUtils playerutils = PlayerUtils.getInstance();
	private HashMap<Player, ItemStack[]> playerInventory = new HashMap<>();
	private HashMap<Player, GameMode> playerGamemode = new HashMap<>();
	//private DataBase dataBase = DataBase.getInstance();
	private Utils utils = Utils.getInstance();
	private CountDown countdown;
	
	/*
	 * Private methods
	 */

	/**
	 * Return chat info for the class use.
	 *
	 * @return [playerInGame/maxPlayers] for the current game
	 */
	private String getPlayerAmount()
	{
		return ChatColor.GRAY  + "[" + ChatColor.RED + this.players.size() + ChatColor.GRAY + "/" + ChatColor.RED + this.maxPlayer + ChatColor.GRAY + "]";
	}

	private void addPlayer(Player p)
	{
		if(!this.isPlayer(p)){
			GameJoinEvent gameJoinEvent = new GameJoinEvent(this, p);
			Bukkit.getPluginManager().callEvent(gameJoinEvent);
			if(!gameJoinEvent.isCancelled())
			{
				this.players.add(p);
				this.playerInventory.put(p,p.getInventory().getContents());
				this.playerGamemode.put(p,p.getGameMode());
				p.getInventory().clear();
				p.getInventory().setHeldItemSlot(0);
				this.playerutils.giveDoor(p);
				p.updateInventory();
				p.teleport(this.waitroom);
				p.setGameMode(GameMode.ADVENTURE);
			}
		}
	}
	
	private void removePlayer(Player p)
	{
		if(this.isPlayer(p))
		{
			GameQuitEvent gameQuitEvent = new GameQuitEvent(this, p);
			Bukkit.getPluginManager().callEvent(gameQuitEvent);
			if(!gameQuitEvent.isCancelled())
			{
				this.players.remove(p);
				Hub.toHub(p);
				p.getInventory().clear();
				p.getInventory().setContents(this.playerInventory.get(p));
				p.setGameMode(this.playerGamemode.get(p));
				p.setLevel(0);
				p.setExp((float) 0);
				p.updateInventory();		
			}
		}
		
		if(this.getState().equalsIgnoreCase("inprocess"))
		{
			if(this.players.size() == 0)
			{
				this.restart();
			}
		} 
		else if(this.getState().equalsIgnoreCase("lobby"))
		{
			if(this.players.size() == 0)
			{
				this.countdown.setTimer(30);
				this.countdown.setCanStart(false);
			}
		}
		signm.updateSign(this.id);
	}
	
	private int getElapseTime(long time)
	{
		return (int) ((time - this.startTime) / 1000);
	}
	
	private Game load()
	{
		if(this.config.get("spawn") != null)
		{
			this.spawn = this.utils.getLocation("spawn", this.config);
		}
		else 
		{
			this.state = "off";
			return this;
		}
		
		if(this.config.get("waitroom") != null)
		{
			this.waitroom = this.utils.getLocation("waitroom", this.config); 
		}
		else
		{
			this.state = "off";
			return this;
		}
		
		if(this.config.get("infos.maxPlayer") != null){
			this.maxPlayer = this.config.getInt("infos.maxPlayer");
		}
		else 
		{
			this.state = "off";
			return this;
		}
		
		if(this.config.get("infos.minPlayer") != null)
		{
			this.minPlayer = this.config.getInt("infos.minPlayer");
		}
		else 
		{
			this.state = "off";
			return this;
		}
		
		if(this.config.get("infos.name") != null)
		{
			this.name = this.config.getString("infos.name");
		}
		else {
			this.state = "off";
			return this;
		}
		
		if(this.config.get("infos.istoggled") != null)
		{
			this.isToggled = this.config.getBoolean("infos.istoggled");
		}
		else
		{
			this.isToggled = false;
		}

		return this;
	}
	
	/*
	 * Public methods
	 */
	
	public Game(int id)
	{
		this.config = sm.getConfig("Arena_" + id);
		this.id = id;
		if(this.config != null)
		{
			this.setState("lobby")
				.load();
		} else {
			this.state = "off";
			Maze.log.info("Empty config for game : "+ id);
		}
		int[] i = {1,2,3,4,5,10,15,20,30}; //When countdown is showing the remaining time.
		this.countdown = new CountDown(this, 30, i);
	}
	
	/**
	 * Method to stop the game
	 */
	public void stop()
	{
		for(Player player : this.players)
		{
			player.sendMessage(Maze.prefix + ChatColor.RED + "You game was stopped !");
			Maze.getHub().toHub(player);
		}
		this.countdown.stopCountDown();
	}

    /**
	 * Restart the game
	 */
	public Game restart()
	{	
		this.state = "lobby";
		for(Player player : this.players)
		{
			this.leavePlayer(player);
		}
		this.countdown.restartCountDown(this);
		this.load();

		return this;
	}
	
	/**
	 * Start the game
	 */
	public Game start()
	{
		this.startTime = System.currentTimeMillis();
		this.state = "inprocess";
		signm.updateSign(this.id);
		for(Player player : this.players)
		{
			this.playerutils.giveDoor(player);
			player.teleport(this.spawn);
			player.setLevel(0);
			player.setExp((float) 0);
		}

		return this;
	}
	
	/**
	 * Send message to all player in the Game
	 * 
	 * @param message Message to send
	 */
	public Game broadcast(String message)
	{
		for(Player player : this.players)
		{
			player.sendMessage(message);
		}

		return this;
	}

	/*
	 * Player interactions
	 */
	/**
	 * When a player leave the Game
	 * 
	 * @param p Object player
	 */
	public Game leavePlayer(Player p)
	{	
		p.sendMessage(Maze.prefix + ChatColor.YELLOW + "You left the game !");
		this.removePlayer(p);
		this.broadcast(Maze.prefix + ChatColor.YELLOW + p.getName() + " has left the game " + this.getPlayerAmount());

		return this;
	}
	
	/**
	 * When a player join the Game
	 * 
	 * @param p Object Player
	 */
	public Game joinPlayer(Player p)
	{
		if(this.getState().equalsIgnoreCase("off"))
		{
			p.sendMessage(Maze.prefix + ChatColor.RED + ChatColor.BOLD + "This game is offline !");
			return this;
		}
		if(!this.isfull())
		{
			if(!this.getState().equalsIgnoreCase("inprocess"))
			{
				if(!this.isPlayer(p)){
					this.addPlayer(p);
					this.broadcast(Maze.prefix + ChatColor.YELLOW + p.getName() + " has join the game " + this.getPlayerAmount());
					if(this.canStart())
					{
						this.countdown.setCanStart(true);
					}
					signm.updateSign(this.id);
				}		
			}
			else
			{
				p.sendMessage(Maze.prefix + ChatColor.RED + "Game in process !");
			}
		}
		else 
		{
			p.sendMessage(Maze.prefix + ChatColor.RED + "This game is full");
		}

		return this;
	}

	public Game checkGameMode()
	{
		for(Player player : this.players)
		{
			if(player.getGameMode() != GameMode.ADVENTURE)
			{
				player.setGameMode(GameMode.ADVENTURE);
			}
		}

		return this;
	}

	public void tpPad(Location location)
	{

	}

	/**
	 * When player finish the game
	 * 
	 * @param p Object Player
	 */
	public Game finishPlayer(Player p)
	{
		p.sendMessage(Maze.prefix + ChatColor.GREEN + "You finish the maze in : "+ this.getElapseTime(System.currentTimeMillis()) + " seconds");
		this.removePlayer(p);
		this.broadcast(Maze.prefix + ChatColor.GREEN + p.getName() + " has finish the maze in : "+ this.getElapseTime(System.currentTimeMillis()) + " seconds");

		return this;
	}
	/*
	 * Getters
	 */
	/**
	 * Get the GameID
	 * 
	 * @return Id of the Game
	 */
	public int getID()
	{
		return this.id;
	}
	
	/**
	 * Get The maximum amount player that can join the game
	 * 
	 * @return Maximum amount player
	 */
	public int getMaxPlayer()
	{
		return this.maxPlayer;
	}
	
	/**
	 * Get The minimum amount of player that have join the game
	 * 
	 * @return Minimum amount of player
	 */
	public int getMinPlayer()
	{
		return this.minPlayer;
	}
	
	/**
	 * Get the spawn Location of the Maze
	 * 
	 * @return Location of the spawn
	 */
	public Location getSpawn()
	{
		return this.spawn;
	}
	
	/**
	 * Get the waitroom Location of the Maze
	 * 
	 * @return Location of the waitroom
	 */
	public Location getWaitRoom()
	{
		return this.waitroom;
	}
	
	/**
	 * Getting the countdown of the game
	 */
	public CountDown getCountDown()
	{
		return this.countdown;
	}
	
	/**
	 * Get if the player is in the game
	 * 
	 * @param name Player name
	 * @return True / False
	 */
	public boolean isPlayer(String name)
	{
		for(Player p : this.players)
		{
			if(p.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	/**
	 * Get if the player is in the game
	 *
	 * @return True / False
	 */
	public boolean isPlayer(Player p)
	{
		return this.players.contains(p);
	}
	
	/**
	 * If the amount of player is enough to start
	 * 
	 * @return True / False
	 */
	public boolean canStart()
	{
		return this.players.size() >= this.minPlayer;
	}
	
	/**
	 * If the game is toggled
	 * 
	 * @return True / False
	 */
	public boolean isToggled()
	{
		return this.isToggled;
	}
	
	/**
	 * If the game is full
	 * 
	 * @return True / False
	 */
	public boolean isfull()
	{
		return this.players.size() == this.maxPlayer;
	}
	
	/**
	 * Get all players that are in the game
	 * 
	 * @return ArrayList of players
	 */
	public ArrayList<Player> getPlayer()
	{
		return this.players;
	}
	
	/**
	 * Get the State of the game
	 * 
	 * @return State of the game
	 */
	public String getState()
	{
		if(isToggled)
		{
			return "off";
		}
		return this.state;
	}
	
	/**
	 * Get the name of the Game
	 * 
	 * @return Name of the game
	 */
	public String getName()
	{
		return this.name;
	}
	/*
	 * Setters
	 */
	
	public Game setCountDown(CountDown countdown)
	{
		this.countdown = countdown;
		return this;
	}
	/**
	 * Toggle the Game (To true if false and to false if true)
	 */
	public Game toggle()
	{
		this.isToggled = !isToggled;
		this.config.set("infos.istoggled", this.isToggled);
		sm.saveConfig("Arena_" + this.id, this.config);
		signm.updateSign(this.id);

		return this;
	}
	
	/**
	 * Set the state of the game
	 * 
	 * @param state New state of the game
	 */
	public Game setState(String state)
	{
		this.state = state;
		signm.updateSign(this.id);

		return this;
	}
	
	/**
	 * Set the name of the Game
	 * 
	 * @param name New name of the game
	 */
	public Game setName(String name)
	{
		this.name = name;
		this.config.set("infos.name", name);
		sm.saveConfig("Arena_" + this.id, this.config);
		signm.updateSign(this.id);

		return this;
	}
	
	/**
	 * Set max amount of player
	 * 
	 * @param maxPlayer New max amount of player
	 */
	public Game setMaxPlayer(int maxPlayer)
	{
		this.maxPlayer = maxPlayer;
		this.config.set("infos.maxPlayer", maxPlayer);
		sm.saveConfig("Arena_" + this.id, this.config);
		signm.updateSign(this.id);

		return this;
	}
	
	/**
	 * Set min amount of player
	 * 
	 * @param minPlayer New min amount of player
	 */
	public Game setMinPlayer(int minPlayer)
	{
		this.minPlayer = minPlayer;
		this.config.set("infos.minPlayer", minPlayer);
		sm.saveConfig("Arena_" + this.id, this.config);

		return this;
	}
	
	/**
	 * Set a new spawn location
	 * 
	 * @param spawn Location of the new spawn
	 */
	public Game setSpawn(Location spawn)
	{
		this.spawn = spawn;
		this.utils.setConfigLocation("spawn", "Arena_" + this.id, this.config, spawn);

		return this;
	}
	
	/**
	 * Set a new waitroom location
	 * 
	 * @param waitroom Location of the new waitroom
	 */
	public Game setWaitRoom(Location waitroom)
	{
		this.waitroom = waitroom;
		this.utils.setConfigLocation("waitroom", "Arena_" + this.id, this.config, waitroom);

		return this;
	}
}
