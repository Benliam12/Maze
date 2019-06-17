package ca.benliam12.maze.game;

import org.bukkit.Location;

/**
 * Created by Benliam12 on 2019-06-12.
 */
public class TpPad
{
    private Location location;
    private int id;
    private int linkedTpPad;


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


}
