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
				if(args[0].equalsIgnoreCase("quit"))
				{
					gm.removePlayer(player);
				} 
				else if(args[0].equalsIgnoreCase("create"))
				{
					gm.createGame(player,player.getLocation());
				}
				else 
				{
					player.sendMessage(ChatColor.RED + "Usage : /maze <create/delete/join/quit> {args}");
				}
			} 
			else if(args.length == 2)
			{
				if(args[0].equalsIgnoreCase("join"))
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
				else if(args[0].equalsIgnoreCase("create"))
				{
					gm.createGame(player,player.getLocation(),args[1]);
				}
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
				else
				{
					player.sendMessage(ChatColor.RED + "Usage : /maze <create/delete/join/quit> {args}");
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
