package ca.benliam12.maze.debuggers;

import org.bukkit.entity.Player;

/**
 * Created by Benliam12 on 2018-07-20.
 */
public class DebugPlayer
{
    private Player player;
    private boolean debug= false;


    public DebugPlayer(Player player)
    {
        this.player = player;
    }

    public boolean isDebugging() { return this.debug;}
    public Player getPlayer(){return this.player;}
}
