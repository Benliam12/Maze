package ca.benliam12.maze.commands;
import ca.benliam12.maze.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ca.benliam12.maze.Maze;
import ca.benliam12.maze.game.GameManager;

public class Commands implements CommandExecutor
{
	private GameManager gm = GameManager.getInstance();
	private MessageUtils mu = MessageUtils.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Must be a player to perform this command !");
			return true;
		}
		
		Player player = (Player) sender;

		if(label.equalsIgnoreCase("maze")) // Should be able to remove this line as it is linked by CommandExecutor from Main function
		{
			if(args.length == 1)
			{
				// Quit game command
				if(args[0].equalsIgnoreCase("quit") || args[0].equalsIgnoreCase("leave"))
				{
					gm.removePlayer(player);
				} 
				// Create command
				else if(args[0].equalsIgnoreCase("create"))
				{
					if(player.hasPermission("maze.admin.create"))
					{
						gm.createGame(player,player.getLocation());
					}
					else
					{
						player.sendMessage(mu.getMessage("noPerm"));
					}
				} 
				// List game command
				else if(args[0].equalsIgnoreCase("list"))
				{
					gm.getGames(player);
				}
				// Force start Maze Game, Player has to be IG
				else if(args[0].equalsIgnoreCase("start"))
				{
					if(player.hasPermission("maze.admin.forcestart"))
					{
						if(gm.getGame(player) != null)
						{
							gm.getGame(player).broadcast(Maze.prefix + ChatColor.GREEN + "Game has started !");
							gm.getGame(player).start();
						}
						else
						{
							player.sendMessage(Maze.prefix + ChatColor.RED + "You must be in game to start the game");
						}
					}
					else
					{
						player.sendMessage(mu.getMessage("noPerm"));
					}

				}
				else if (args[0].equalsIgnoreCase("help"))
				{
					//TODO: Make an acutal help section
					player.sendMessage(Maze.prefix + ChatColor.RED + "Help section still on Development!");
				}
				else 
				{
					player.sendMessage(mu.getMessage("BadCommand"));
				}
			} 
			else if(args.length == 2)
			{
				// Join command
				if(args[0].equalsIgnoreCase("join"))
				{
					if(player.hasPermission("maze.join"))
					{
						try{
							int GameID = Integer.parseInt(args[1]);
							this.gm.addPlayer(GameID,player);
						}
						catch(Exception ex)
						{
							player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
						}
					}
					else
					{
						player.sendMessage(mu.getMessage("noPerm"));
					}
				} 
				// Create command
				else if(args[0].equalsIgnoreCase("create"))
				{
					if(player.hasPermission("maze.admin.create"))
					{
						gm.createGame(player,player.getLocation(),args[1]);
					}
					else
					{
						player.sendMessage(mu.getMessage("noPerm"));
					}
				}
				// Delete Command
				else if(args[0].equalsIgnoreCase("delete"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						if(player.hasPermission("maze.admin.delete"))
						{
							gm.deleteGame(player, GameID);
						}
						else
						{
							player.sendMessage(mu.getMessage("noPerm"));
						}
						
					}
					catch(Exception ex)
					{
						player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
					}
				}
				else if(args[0].equalsIgnoreCase("setwaitroom"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						if(player.hasPermission("maze.admin.edit"))
						{
							gm.getGame(GameID).setWaitRoom(player.getLocation());
							player.sendMessage(Maze.prefix + ChatColor.GREEN + "Wait room set !");
						}
						else
						{
							player.sendMessage(mu.getMessage("noPerm"));
						}
				
					}
					catch(Exception ex)
					{
						player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
					}	
				}
				else if(args[0].equalsIgnoreCase("setspawn"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						if(player.hasPermission("maze.admin.edit.spawn"))
						{
							gm.getGame(GameID).setSpawn(player.getLocation());
							player.sendMessage(Maze.prefix + ChatColor.GREEN + "Spawn set !");
						}
						else
						{
							player.sendMessage(mu.getMessage("noPerm"));
						}
					}
					catch(Exception ex)
					{
						player.sendMessage(mu.getMessage("InvalidID"));
					}	
				}
				else if(args[0].equalsIgnoreCase("toggle"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						if(player.hasPermission("maze.admin.edit.toggle"))
						{
							gm.getGame(GameID).toggle();
							if(gm.getGame(GameID).isToggled())
							{
								player.sendMessage(Maze.prefix + ChatColor.YELLOW + "Toggled to :" + ChatColor.GREEN + " true");
							}
							else
							{
								player.sendMessage(Maze.prefix + ChatColor.YELLOW + "Toggled to :" + ChatColor.RED + " false");	
							}
						}
						else
						{
							player.sendMessage(mu.getMessage("noPerm"));
						}
					
					}
					catch(Exception ex)
					{
						player.sendMessage(mu.getMessage("InvalidID"));
					}	
				}
				else
				{
					player.sendMessage(MessageUtils.getInstance().getMessage("BadCommand"));
				}
			}
			else if(args.length == 3)
			{
				if(args[0].equalsIgnoreCase("setminplayer"))
				{
					if(player.hasPermission("maze.admin.edit.minplayer"))
					{
						try{
							int GameID = Integer.parseInt(args[1]);
							int minPlayer = Integer.parseInt(args[2]);
							gm.getGame(GameID).setMinPlayer(minPlayer);
							player.sendMessage(Maze.prefix + ChatColor.YELLOW + "Min player is now : " + ChatColor.GOLD + minPlayer);
						}
						catch(Exception ex)
						{
							player.sendMessage(mu.getMessage("InvalidID"));
						}
					}
					else
					{
						player.sendMessage(mu.getMessage("noPerm"));
					}

				}
				else if(args[0].equalsIgnoreCase("setmaxplayer"))
				{
					if(player.hasPermission("maze.admin.edit.maxplayer"))
					{
						try{
							int GameID = Integer.parseInt(args[1]);
							int maxPlayer = Integer.parseInt(args[2]);
							gm.getGame(GameID).setMaxPlayer(maxPlayer);
							player.sendMessage(Maze.prefix + ChatColor.YELLOW + "Max player is now : " + ChatColor.GOLD + maxPlayer);
						}
						catch(Exception ex)
						{
							player.sendMessage(mu.getMessage("InvalidID"));
						}
					}
					else
					{
						player.sendMessage(mu.getMessage("noPerm"));
					}

				}
				else if(args[0].equalsIgnoreCase("setname"))
				{
					if(player.hasPermission("maze.admin.edit.name"))
					{
						try{
							int GameID = Integer.parseInt(args[1]);
							gm.getGame(GameID).setName(args[2]);
							player.sendMessage(Maze.prefix + ChatColor.YELLOW + "Name is now : " + ChatColor.GOLD + args[2]);
						}
						catch(Exception ex)
						{
							player.sendMessage(mu.getMessage("InvalidID"));
						}
					}
					else
					{
						player.sendMessage(mu.getMessage("noPerm"));
					}

				}
				else if(args[0].equalsIgnoreCase("fjoin"))
				{
					if(player.hasPermission("maze.admin.forcejoin"))
					{
						Player target = Bukkit.getPlayer(args[1]);
						if(target != null)
						{
							try{
								int GameID = Integer.parseInt(args[2]);
								this.gm.addPlayer(GameID,target);
								player.sendMessage(Maze.prefix + ChatColor.GREEN + "Player : " + target.getName() + " was forced to join the game");
								target.sendMessage(Maze.prefix + ChatColor.GREEN + "You were forced to join a maze game.");
							}
							catch(Exception ex)
							{
								player.sendMessage(mu.getMessage("InvalidID"));
							}
						}
						else
						{
							player.sendMessage(Maze.prefix + ChatColor.RED + "This player is not online !");
						}
					}
					else
					{
						player.sendMessage(mu.getMessage("noPerm"));
					}
				}
			}
			else
			{
				player.sendMessage(mu.getMessage("BadCommand"));
			}
		}
		return false;
	}
}
