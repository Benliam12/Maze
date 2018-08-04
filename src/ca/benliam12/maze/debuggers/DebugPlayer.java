package ca.benliam12.maze.debuggers;

import org.bukkit.entity.Player;

/**
 * Created by Benliam12 on 2018-07-20.
 */
public class DebugPlayer
{
    private Player player;
    private boolean hasDebugAccess = false;

    public DebugPlayer(Player player)
    {
        this.player = player;
        if(player.hasPermission("Maze.Debug.access"))
        {
            this.hasDebugAccess = true;
        }
    }
    public boolean hasDebugAccess() { return this.hasDebugAccess;}
    public Player getPlayer(){return this.player;}
}
