package telran.net.games.exceptions;

import java.util.NoSuchElementException;

@SuppressWarnings("serial")
public class GameGamerNotFoundException extends NoSuchElementException {
	public GameGamerNotFoundException(long gameId, String username) {
		super(String.format("Not found gamer %s in game %d", username, gameId));
	}
}