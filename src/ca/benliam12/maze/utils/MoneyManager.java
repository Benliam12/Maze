package ca.benliam12.maze.utils;

import ca.benliam12.maze.Maze;
import org.bukkit.entity.Player;

/**
 * Created by Benliam12 on 2018-10-23.
 */
public class MoneyManager
{
    private static MoneyManager instance = new MoneyManager();
    public static MoneyManager getInstance() {return instance;}

    public MoneyManager(){}

    private boolean enabled = false;

    public void setup()
    {
        if(this.enabled)
        {

        }
    }

    public void enable() {this.enabled = true;}
    public void disable() {this.enabled = false;}
    public boolean isEnable() {return this.enabled;}

}
