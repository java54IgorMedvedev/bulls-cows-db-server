package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class GamerAlreadyExistsdException extends IllegalArgumentException {
	public GamerAlreadyExistsdException(String username) {
		super("Already exists gamer " + username);
	}
}