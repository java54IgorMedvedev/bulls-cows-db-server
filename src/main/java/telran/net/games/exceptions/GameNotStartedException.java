package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class GameNotStartedException extends IllegalStateException {
	public GameNotStartedException(long gameId) {
		super("Not yet started game " + gameId);
	}
}