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
    private int id;
    private int linkedTpPad;
    private int gameId;

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

    public void tpPlayer(Player player)
    {
        player.teleport(this.location);
    }

    public Game getGame()
    {
        Game game = GameManager.getInstance().getGame(this.gameId);
        if(game == null)
            Maze.getMaze().Logger(Maze.loggerPrefix + "Tp pad Game ID can't be found", 3);

        return game;
    }

}
