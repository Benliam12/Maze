package ca.benliam12.maze.game;

import ca.benliam12.maze.Maze;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Benliam12 on 2019-06-12.
 */
public class TpPad
{
    private Location location;
    private Location targetLocation;
    private int id;
    private Game game;

    public void setLocation(Location loc)
    {
        this.location = loc;
    }


    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public Game getGame(){return this.game;}

    public Location getLocation()
    {
        return this.location;
    }

    public void tpPlayer(Player player)
    {
        player.teleport(this.targetLocation);
    }

    public TpPad(int id, Game game, Location location, Location targetLocation)
    {
        this.id = id;
        this.game = game;
        this.location = location;
        this.targetLocation = targetLocation;
    }
}
