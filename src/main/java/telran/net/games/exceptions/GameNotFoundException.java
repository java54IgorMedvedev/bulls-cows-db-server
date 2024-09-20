package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class GameNotFoundException extends IllegalArgumentException {
	public GameNotFoundException(long gameId) {
		super("Not found game " + gameId);
	}
}