package ca.benliam12.maze.debuggers;

import java.util.ArrayList;

import ca.benliam12.maze.Maze;
import ca.benliam12.maze.exception.DebugException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Benliam12 on 2018-07-20.
 */
public class DebugPlayerManager
{
    private static DebugPlayerManager instance = new DebugPlayerManager();
    public static DebugPlayerManager getInstance() {return instance;}
    public DebugPlayerManager() {}
    private ArrayList<DebugPlayer> players = new ArrayList<>();
    public ArrayList<DebugPlayer> getPlayers()
    {
        return this.players;
    }

    public void setup()
    {

    }
    /**
     * Get a player using Player Object
     * @param player Player Object
     * @return Player Object
     * @throws DebugException If no player found
     */
    public DebugPlayer getPlayer(Player player) throws DebugException
    {
        for(DebugPlayer p : this.players)
        {
            if(p.getPlayer().getName().equalsIgnoreCase(player.getName()))
            {
                return p;
            }
        }
        throw new DebugException("Player not found !");
    }

    public DebugPlayer getPlayer (String name) throws DebugException
    {
        boolean found = false;
        for(DebugPlayer p : this.players)
        {
            if(p.getPlayer().getName().equalsIgnoreCase(name))
            {
                found = true;
                return p;
            }
        }
        throw new DebugException("Player not found");
    }

    public void addPlayer(Player player)
    {
        DebugPlayer debugPlayer = new DebugPlayer(player);
        this.players.add(debugPlayer);
    }

    public void leavePlayer(Player player)
    {
        try
        {
            this.players.remove(this.getPlayer(player));
        }
        catch(DebugException e)
        {
            Maze.log.info(e.getMessage());
        }
    }

    public boolean isPlayer(Player player)
    {
        for(DebugPlayer p : this.players)
        {
            if(p.getPlayer().getName().equalsIgnoreCase(player.getName()))
            {
                return true;
            }
        }
        return false;
    }

    public void broadcast(String message)
    {
        for(DebugPlayer p: this.players)
        {
            if(p.isDebugging())
            p.getPlayer().sendMessage(Maze.prefix + ChatColor.WHITE + message);
        }
    }

}
