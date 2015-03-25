package ca.benliam12.maze.commands;
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
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Must be a player to perform this command !");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(label.equalsIgnoreCase("maze"))
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
					gm.createGame(player,player.getLocation());
				} 
				// List game command
				else if(args[0].equalsIgnoreCase("list"))
				{
					gm.getGames(player);
				}
				else 
				{
					player.sendMessage(ChatColor.RED + "Usage : /maze <create/delete/join/quit> {args}");
				}
			} 
			else if(args.length == 2)
			{
				// Join command
				if(args[0].equalsIgnoreCase("join"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						this.gm.addPlayer(GameID,player);
					}
					catch(Exception ex)
					{
						player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
						ex.printStackTrace();
					}
				} 
				// Create command
				else if(args[0].equalsIgnoreCase("create"))
				{
					gm.createGame(player,player.getLocation(),args[1]);
				}
				// Delete Command
				else if(args[0].equalsIgnoreCase("delete"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						gm.deleteGame(player, GameID);
					}
					catch(Exception ex)
					{
						player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
					}
				}
				else if(args[0].equalsIgnoreCase("toggle"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
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
					catch(Exception ex)
					{
						player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
					}	
				}
				else
				{
					player.sendMessage(ChatColor.RED + "Usage : /maze <create/delete/join/quit> {args}");
				}
			}
			else if(args.length == 3)
			{
				if(args[0].equalsIgnoreCase("setminplayer"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						int minPlayer = Integer.parseInt(args[2]);
						gm.getGame(GameID).setMinPlayer(minPlayer);
						player.sendMessage(Maze.prefix + ChatColor.YELLOW + "Min player is now : " + ChatColor.GOLD + minPlayer);
					}
					catch(Exception ex)
					{
						player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
					}
				}
				else if(args[0].equalsIgnoreCase("setmaxplayer"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						int maxPlayer = Integer.parseInt(args[2]);
						gm.getGame(GameID).setMaxPlayer(maxPlayer);
						player.sendMessage(Maze.prefix + ChatColor.YELLOW + "Max player is now : " + ChatColor.GOLD + maxPlayer);
					}
					catch(Exception ex)
					{
						player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
					}
				}
				else if(args[0].equalsIgnoreCase("setname"))
				{
					try{
						int GameID = Integer.parseInt(args[1]);
						gm.getGame(GameID).setName(args[2]);
						player.sendMessage(Maze.prefix + ChatColor.YELLOW + "Name is now : " + ChatColor.GOLD + args[2]);
					}
					catch(Exception ex)
					{
						player.sendMessage(Maze.prefix + ChatColor.RED + "Invalid ID");
					}
				} 
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Usage : /maze <create/delete/join/quit> {args}");
			}
		}
		return false;
	}
}
