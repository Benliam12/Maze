package ca.benliam12.maze.game;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.utils.SettingManager;
import ca.benliam12.maze.utils.Utils;

public class Game 
{
	private int id;
	private int maxPlayer;
	private int minPlayer;
	private int startTime;
	private Location spawn;
	private Location waitroom;
	private String state;
	private String name;
	private boolean isToggled;
	private FileConfiguration config;
	private ArrayList<Player> players = new ArrayList<Player>();
	private SettingManager sm = SettingManager.getInstance();
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
		}
	}
	
	public void removePlayer(Player p)
	{
		if(this.isPlayer(p))
		{
			this.players.remove(p);
			Maze.getHub().toHub(p);
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
	}
	
	private int getElapseTime(int time)
	{
		return time - this.startTime;
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
			this.countdown = new CountDown(this.id, 30);
			this.thread = new Thread(this.countdown);
			this.thread.start();
		} else {
			this.state = "off";
			Maze.log.info("Empty config for game : "+ id);
		}
	}
	
	public void stop()
	{
		for(Player player : this.players)
		{
			player.sendMessage(Maze.prefix + ChatColor.RED + "You game was stopped !");
			Maze.getHub().toHub(player);
		}
		this.countdown = null;
		this.thread = null;
	}
	
	public void restart()
	{	
		this.state = "lobby";
		for(Player player : this.players)
		{
			this.leavePlayer(player);
		}
		this.countdown.setCanStart(false);
		this.countdown.setTimer(30);
		this.countdown.setID(this.id);
		this.load();
	}
	
	public void start()
	{
		this.startTime = Math.round(System.currentTimeMillis() / 1000);
		this.state = "inprocess";
		for(Player player : this.players)
		{
			player.teleport(this.spawn);
		}
	}
	
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
	public void leavePlayer(Player p)
	{	
		p.sendMessage(Maze.prefix + ChatColor.YELLOW + "You left the game !");
		this.removePlayer(p);
		this.broadcast(Maze.prefix + ChatColor.YELLOW + p.getName() + " has left the game " + this.getPlayerAmount());
	}
	
	public void joinPlayer(Player p)
	{
		if(this.getState().equalsIgnoreCase("off"))
		{
			p.sendMessage(Maze.prefix + ChatColor.RED + ChatColor.BOLD + "This game is offline !");
			return;
		}
		
		if(!this.isPlayer(p)){
			this.addPlayer(p);
			p.teleport(this.waitroom);
			this.broadcast(Maze.prefix + ChatColor.YELLOW + p.getName() + " has join the game " + this.getPlayerAmount());
			if(this.canStart())
			{
				this.countdown.setCanStart(true);
			}
		}
	}
	
	public void finishPlayer(Player p)
	{
		p.sendMessage(Maze.prefix + ChatColor.GREEN + "You finish the maze in : "+ this.getElapseTime(Math.round(System.currentTimeMillis() / 1000)) + " seconds");
		this.removePlayer(p);
		this.broadcast(Maze.prefix + ChatColor.GREEN + p.getName() + " has finish the maze in : "+ this.getElapseTime(Math.round(System.currentTimeMillis() / 1000)) + " seconds");
	}
	
	/*
	 * Getters
	 */
	
	public int getID()
	{
		return this.id;
	}
	
	public int getMaxPlayer()
	{
		return this.maxPlayer;
	}
	
	public int getMinPlayer()
	{
		return this.minPlayer;
	}
	
	public Location getSpawn()
	{
		return this.spawn;
	}
	
	public Location getWaitRoom()
	{
		return this.waitroom;
	}
	
	public boolean isPlayer(String name)
	{
		for(Player p : this.players)
		{
			if(p.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public boolean isPlayer(Player p)
	{
		return this.players.contains(p);
	}
	
	public boolean canStart()
	{
		if(this.players.size() >= this.minPlayer)
		{
			return true;
		}
		return false;
	}
	
	public boolean isToggled()
	{
		return this.isToggled;
	}
	
	public ArrayList<Player> getPlayer()
	{
		return this.players;
	}
	
	public String getState()
	{
		if(isToggled)
		{
			return "off";
		}
		return this.state;
	}
	
	public String getName()
	{
		return this.name;
	}
	/*
	 * Setters
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
	}
	
	public void setState(String state)
	{
		this.state = state;
	}
	
	public void setName(String name)
	{
		this.name = name;
		this.config.set("infos.name", name);
		sm.saveConfig("Arena_" + this.id, this.config);
	}
	
	public void setMaxPlayer(int maxPlayer)
	{
		if(this.config == null)
		{
			Maze.log.info("YEP");
			return;
		}
		this.maxPlayer = maxPlayer;
		this.config.set("infos.maxPlayer", maxPlayer);
		sm.saveConfig("Arena_" + this.id, this.config);
	}
	
	public void setMinPlayer(int minPlayer)
	{
		this.minPlayer = minPlayer;
		this.config.set("infos.minPlayer", minPlayer);
		sm.saveConfig("Arena_" + this.id, this.config);
	}
	
	public void setSpawn(Location spawn)
	{
		this.spawn = spawn;
		this.utils.setConfigLocation("spawn", "Arena_" + this.id, this.config, spawn);
	}
	
	public void setWaitRoom(Location waitroom)
	{
		this.waitroom = waitroom;
		this.utils.setConfigLocation("waitroom", "Arena_" + this.id, this.config, spawn);
	}
}
