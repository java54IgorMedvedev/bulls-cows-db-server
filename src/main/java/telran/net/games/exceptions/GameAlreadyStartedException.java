package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class GameAlreadyStartedException extends IllegalStateException {
	public GameAlreadyStartedException(long gameId) {
		super("Already started game " + gameId);
	}
}