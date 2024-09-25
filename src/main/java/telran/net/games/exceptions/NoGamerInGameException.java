package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class NoGamerInGameException extends IllegalStateException {
	public NoGamerInGameException(long gameId) {
		super("No gamers in game " + gameId);
	}
}