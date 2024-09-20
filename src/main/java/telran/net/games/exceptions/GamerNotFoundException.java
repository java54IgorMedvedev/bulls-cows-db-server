package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class GamerNotFoundException extends IllegalArgumentException {
	public GamerNotFoundException(String username) {
		super("Not found gamer " + username);
	}
}