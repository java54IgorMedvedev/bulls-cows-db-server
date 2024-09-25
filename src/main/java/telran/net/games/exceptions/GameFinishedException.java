package telran.net.games.exceptions;

public class GameFinishedException extends IllegalStateException {
    public GameFinishedException(long gameId) {
        super("Game with ID " + gameId + " is already finished.");
    }
}
