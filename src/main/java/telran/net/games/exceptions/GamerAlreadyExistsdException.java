package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class GamerAlreadyExistsdException extends IllegalStateException {
	public GamerAlreadyExistsdException(String username) {
		super("Already exists gamer " + username);
	}
}