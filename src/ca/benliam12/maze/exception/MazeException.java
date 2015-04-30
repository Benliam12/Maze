package ca.benliam12.maze.exception;

import ca.benliam12.maze.game.Game;

@SuppressWarnings("serial")
public class MazeException extends Exception
{
	private String message;
	private Game game;
	
	public MazeException(String message, Game game)
	{
		this.message = message;
		this.game = game;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public Game getGame()
	{
		return this.game;
	}
}
