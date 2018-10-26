package ca.benliam12.maze.utils;
import ca.benliam12.maze.Maze;
import net.md_5.bungee.api.ChatColor;
import java.util.HashMap;

/**
 * Created by Benliam12 on 2018-07-15.
 */
public class MessageUtils
{
    private static MessageUtils instance = new MessageUtils();
    public static MessageUtils getInstance(){return instance;}

    private HashMap<String, String> messages = new HashMap<>();

    public void setup()
    {
        this.addMessage("BadCommand", Maze.prefix + ChatColor.RED + "Use " + ChatColor.YELLOW + "/maze help" + ChatColor.RED + " to get all available commands");
        this.addMessage("InvalidID", Maze.prefix + ChatColor.RED + "Invalid ID");
        this.addMessage("noPerm", Maze.prefix + ChatColor.RED + "You don't have the permission to perform this action!");
    }

    /**
     * Get presetMessage()
     * @param message Message Name
     * @return Message, Returns : ERROR if no message found
     */
    public String getMessage(String message)
    {
        if(this.isMessage(message))
        {
            return this.messages.get(message);
        }
        else
        {
            return Maze.prefix + ChatColor.RED + "ERROR message (" + message + ") not found!";
        }
    }

    /*
    Private methods
     */
    private boolean isMessage(String message)
    {
        return this.messages.containsKey(message);
    }

    private void addMessage(String name, String message)
    {
        if(!isMessage(name))
        {
            this.messages.put(name, message);
            Maze.log.info("[Maze] Adding message : " + name);
        }
        else
        {
            Maze.log.warning("[Maze] MESSAGE REGISTER TWICE on NAME : " + name + ". PLEASE CONTACT PLUGIN DEVELOPER");
        }
    }
}
