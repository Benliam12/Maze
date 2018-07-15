package ca.benliam12.maze.debuggers;

/**
 * Created by Benliam12 on 2018-07-14.
 */
public class Debugger
{

    public static Debugger instance = new Debugger();

    public static Debugger getInstance()
    {
        return instance;
    }

    private boolean enable = false;

    public void enable()
    {
        this.enable = true;
    }

    public void disable()
    {
        this.enable = false;
    }

    public void set(boolean enable)
    {
        if(enable)
        {
            this.enable = enable;
        }
        this.enable = false;
    }

    public boolean isEnable()
    {
        return this.enable;
    }
}
