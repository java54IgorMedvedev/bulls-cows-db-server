package telran.net.games.exceptions;

public class GameNotStartedException extends IllegalStateException {
    public GameNotStartedException(long gameId) {
        super("Game with ID " + gameId + " has not started yet.");
    }
}
