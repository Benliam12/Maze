package ca.benliam12.maze.game;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.signs.SignManager;
import ca.benliam12.maze.utils.PlayerUtils;
import ca.benliam12.maze.utils.SettingManager;
import ca.benliam12.maze.utils.Utils;

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
	private ArrayList<Player> players = new ArrayList<Player>();
	private SettingManager sm = SettingManager.getInstance();
	private SignManager signm = SignManager.getInstance();
	private PlayerUtils playerutils = PlayerUtils.getInstance();
	private Utils utils = Utils.getInstance();
	private CountDown countdown;
	private Thread thread;
	
	/*
	 * Private methods
	 */
	
	private String getPlayerAmount()
	{
		return ChatColor.GRAY  + "[" + ChatColor.RED + this.players.size() + ChatColor.GRAY + "/" + ChatColor.RED + this.maxPlayer + ChatColor.GRAY + "]";
	}
	
	private void addPlayer(Player p)
	{
		if(!this.isPlayer(p)){
			this.players.add(p);
			p.getInventory().clear();
			p.getInventory().setHeldItemSlot(0);
			this.playerutils.giveDoor(p);
			p.updateInventory();
			p.setGameMode(GameMode.ADVENTURE);
		}
	}
	
	public void removePlayer(Player p)
	{
		if(this.isPlayer(p))
		{
			this.players.remove(p);
			Maze.getHub().toHub(p);
			p.getInventory().clear();
			p.setLevel(0);
			p.setExp((float) 0);
			p.updateInventory();
		}
		
		if(this.getState().equalsIgnoreCase("inprocess"))
		{
			if(this.players.size() == 0)
			{
				this.setState("lobby");
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
	
	private double getElapseTime(long time)
	{
		double rTime = (time - this.startTime) / 1000;
		return rTime;
	}
	
	private void load()
	{
		if(this.config.get("spawn") != null)
		{
			this.spawn = this.utils.getLocation("spawn", this.config);
		}
		else 
		{
			this.state = "off";
			return;
		}
		
		if(this.config.get("waitroom") != null)
		{
			this.waitroom = this.utils.getLocation("waitroom", this.config); 
		}
		else
		{
			this.state = "off";
			return;
		}
		
		if(this.config.get("infos.maxPlayer") != null){
			this.maxPlayer = this.config.getInt("infos.maxPlayer");
		}
		else 
		{
			this.state = "off";
			return;
		}
		
		if(this.config.get("infos.minPlayer") != null)
		{
			this.minPlayer = this.config.getInt("infos.minPlayer");
		}
		else 
		{
			this.state = "off";
			return;
		}
		
		if(this.config.get("infos.name") != null)
		{
			this.name = this.config.getString("infos.name");
		}
		else {
			this.state = "off";
			return;
		}
		
		if(this.config.get("infos.istoggled") != null)
		{
			this.isToggled = this.config.getBoolean("infos.istoggled");
		}
		else
		{
			this.isToggled = false;
		}
		
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
			this.setState("lobby");
			this.load();
		} else {
			this.state = "off";
			Maze.log.info("Empty config for game : "+ id);
		}
		int[] i = {1,2,3,4,5,10,15,20,30};
		this.countdown = new CountDown(this, 30, i);
		this.thread = new Thread(this.countdown);
		this.thread.start();
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
		this.thread.interrupt();
	}
	
	/**
	 * Restart the game
	 */
	public void restart()
	{	
		this.state = "lobby";
		for(Player player : this.players)
		{
			this.leavePlayer(player);
		}
		this.countdown.restartCountDown(this);
		this.load();
	}
	
	/**
	 * Start the game
	 */
	public void start()
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
	}
	
	/**
	 * Send message to all player in the Game
	 * 
	 * @param message Message to send
	 */
	public void broadcast(String message)
	{
		for(Player player : this.players)
		{
			player.sendMessage(message);
		}
	}
	/*
	 * Player interactions
	 */
	/**
	 * When a player leave the Game
	 * 
	 * @param p Object player
	 */
	public void leavePlayer(Player p)
	{	
		p.sendMessage(Maze.prefix + ChatColor.YELLOW + "You left the game !");
		this.removePlayer(p);
		this.broadcast(Maze.prefix + ChatColor.YELLOW + p.getName() + " has left the game " + this.getPlayerAmount());
	}
	
	/**
	 * When a player join the Game
	 * 
	 * @param p Object Player
	 */
	public void joinPlayer(Player p)
	{
		if(this.getState().equalsIgnoreCase("off"))
		{
			p.sendMessage(Maze.prefix + ChatColor.RED + ChatColor.BOLD + "This game is offline !");
			return;
		}
		if(!this.isfull())
		{
			if(!this.isPlayer(p)){
				this.addPlayer(p);
				p.teleport(this.waitroom);
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
			p.sendMessage(Maze.prefix + ChatColor.RED + "This game is full");
		}
	}
	
	/**
	 * When player finish the game
	 * 
	 * @param p Object Player
	 */
	public void finishPlayer(Player p)
	{
		p.sendMessage(Maze.prefix + ChatColor.GREEN + "You finish the maze in : "+ this.getElapseTime(System.currentTimeMillis()) + " seconds");
		this.removePlayer(p);
		this.broadcast(Maze.prefix + ChatColor.GREEN + p.getName() + " has finish the maze in : "+ this.getElapseTime(System.currentTimeMillis()) + " seconds");

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
	 * Get The minimum amount of playe that can join the game
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
	 * @param name Object player
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
	/**
	 * Toggle the Game (To true if false and to false if true)
	 */
	public void toggle()
	{
		if(this.isToggled)
		{
			this.isToggled = false;
		} 
		else
		{
			this.isToggled = true;
		}
		this.config.set("infos.istoggled", this.isToggled);
		sm.saveConfig("Arena_" + this.id, this.config);
		signm.updateSign(this.id);
	}
	
	/**
	 * Set the state of the game
	 * 
	 * @param state New state of the game
	 */
	public void setState(String state)
	{
		this.state = state;
		signm.updateSign(this.id);
	}
	
	/**
	 * Set the name of the Game
	 * 
	 * @param name New name of the game
	 */
	public void setName(String name)
	{
		this.name = name;
		this.config.set("infos.name", name);
		sm.saveConfig("Arena_" + this.id, this.config);
		signm.updateSign(this.id);
	}
	
	/**
	 * Set max amount of player
	 * 
	 * @param maxPlayer New max amount of player
	 */
	public void setMaxPlayer(int maxPlayer)
	{
		this.maxPlayer = maxPlayer;
		this.config.set("infos.maxPlayer", maxPlayer);
		sm.saveConfig("Arena_" + this.id, this.config);
		signm.updateSign(this.id);
	}
	
	/**
	 * Set min amount of player
	 * 
	 * @param minPlayer New min amount of player
	 */
	public void setMinPlayer(int minPlayer)
	{
		this.minPlayer = minPlayer;
		this.config.set("infos.minPlayer", minPlayer);
		sm.saveConfig("Arena_" + this.id, this.config);
	}
	
	/**
	 * Set a new spawn location
	 * 
	 * @param spawn Location of the new spawn
	 */
	public void setSpawn(Location spawn)
	{
		this.spawn = spawn;
		this.utils.setConfigLocation("spawn", "Arena_" + this.id, this.config, spawn);
	}
	
	/**
	 * Set a new waitroom location
	 * 
	 * @param spawn Location of the new waitroom
	 */
	public void setWaitRoom(Location waitroom)
	{
		this.waitroom = waitroom;
		this.utils.setConfigLocation("waitroom", "Arena_" + this.id, this.config, waitroom);
	}
}
