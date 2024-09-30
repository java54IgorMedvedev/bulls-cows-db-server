package telran.net.games.repo;

import java.time.*;
import java.util.List;

import telran.net.games.entities.Game;
import telran.net.games.entities.Gamer;
import telran.net.games.model.MoveData;
import telran.net.games.model.MoveDto;

public interface BullsCowsRepository {
	Game getGame(long id);
	Gamer getGamer(String username);
	long createNewGame(String sequence);
	void createNewGamer(String username, LocalDate birthdate);
	boolean isGameStarted(long id);
	void setStartDate(long gameId, LocalDateTime dateTime);
	boolean isGameFinished(long id);
	void setIsFinished(long gameId);
	List<Long> getGameIdsNotStarted();
	List<String> getGameGamers(long id);
	void createGameGamer(long gameId, String username);
	void createGameGamerMove(MoveDto moveDto);
	List<MoveData> getAllGameGamerMoves(long gameId, String username);
	void setWinner(long gameId, String username);
	boolean isWinner(long gameId, String username);
	List<Long> getIdsNonStartedGamesGamer(String username);
	List<Long> getIdsNonStartedGamesNoGamer(String username);
	List<Long> getIdsStartedGamesGamer(String username);
	
}