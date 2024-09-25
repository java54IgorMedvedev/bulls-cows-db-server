package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class GameGamerAlreadyExistsException extends IllegalStateException {
     public GameGamerAlreadyExistsException(long gameId, String username) {
    	 super(String.format("Gamer %s already exists in game %d",
    			 username, gameId));
     }
}