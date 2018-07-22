package ca.benliam12.maze.exception;

/**
 * Created by Benliam12 on 2018-07-21.
 */
public class DebugException extends Exception
{
    private String message;
    public DebugException(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }


}
