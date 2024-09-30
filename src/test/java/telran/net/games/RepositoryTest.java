package telran.net.games;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import telran.net.games.entities.Game;
import telran.net.games.entities.Gamer;
import telran.net.games.exceptions.*;
import telran.net.games.model.MoveData;
import telran.net.games.model.MoveDto;
import telran.net.games.repo.BullsCowsRepository;
import telran.net.games.repo.BullsCowsRepositoryJpa;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryTest {
	static BullsCowsRepository repository;
	static {
		HashMap<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.put("hibernate.hbm2ddl.auto", "create");
		repository = new BullsCowsRepositoryJpa
				(new BullsCowsTestPersistenceUnitInfo(), hibernateProperties);
	}
	static long gameId;
	static String gamerUsername = "gamer1";
	@Test
	@Order(1)
	void createGameTest() {
		gameId = repository.createNewGame("1234");
		Game game = repository.getGame(gameId);
		assertNotNull(game);
		assertNull(game.getDate());
		assertFalse(game.isfinished());
		
		
	}
	
	@Test
	@Order(2)
	void createGamerTest() {
		repository.createNewGamer(gamerUsername, LocalDate.of(2000, 1, 1));
		Gamer gamer = repository.getGamer(gamerUsername);
		assertNotNull(gamer);
		assertThrowsExactly(GamerAlreadyExistsdException.class, () ->
				repository.createNewGamer(gamerUsername, LocalDate.of(2000, 1, 1)));
		
	}
	@Test
	@Order(3)
	void getIdsNonStartedNoGamerTest() {
		List<Long> ids =
				repository.getIdsNonStartedGamesNoGamer(gamerUsername);
		assertEquals(gameId, ids.get(0));
		ids = repository.getIdsNonStartedGamesGamer(gamerUsername);
		assertTrue(ids.isEmpty());
	}
	@Order(4)
	@Test
	void createGameGamerTest() {
		repository.createGameGamer(gameId, gamerUsername);
		List<String> gamers = repository.getGameGamers(gameId);
		assertEquals(1, gamers.size());
		assertEquals(gamerUsername, gamers.get(0));
	}
	@Order(5)
	@Test
	void getIdsNonStartedGamerTest() {
		List<Long> ids =
				repository.getIdsNonStartedGamesGamer(gamerUsername);
		assertEquals(gameId, ids.get(0));
		ids = repository.getIdsNonStartedGamesNoGamer(gamerUsername);
		assertTrue(ids.isEmpty());
	}
	@Order(6)
	@Test
	void isGameStartedTest() {
		assertFalse(repository.isGameStarted(gameId));
	}
	@Order(7)
	@Test
	void setStartDateTest() {
		repository.setStartDate(gameId, LocalDateTime.now());
		assertTrue(repository.isGameStarted(gameId));
	}
	
	@Order(8)
	@Test
	void createGameGamerMoveAllGameGamersMovesTest() {
		repository.createGameGamerMove(new MoveDto(gameId, gamerUsername, "1243", 2, 2));
		repository.createGameGamerMove(new MoveDto(gameId, gamerUsername, "1234", 4, 0));
		
		List<MoveData> moves = repository.getAllGameGamerMoves(gameId, gamerUsername);
		assertEquals(new MoveData("1243", 2, 2), moves.get(0));
		assertEquals(new MoveData("1234", 4, 0), moves.get(1));
	}
	@Order(9)
	@Test
	void isGameFinishedTest() {
		assertFalse(repository.isGameFinished(gameId));
	}
	@Order(10)
	@Test
	void getIdsStartedGamesGamer() {
		List<Long> ids = repository.getIdsStartedGamesGamer(gamerUsername);
		assertEquals(gameId, ids.get(0));
	}
	@Order(11)
	@Test
	void setIsFinishedTest() {
		repository.setIsFinished(gameId);
		assertTrue(repository.isGameFinished(gameId));
	}
	
	@Order(12)
	@Test
	void isWinnerTest() {
		assertFalse(repository.isWinner(gameId, gamerUsername));
	}
	@Order(13)
	@Test
	void setWinnerTest() {
		repository.setWinner(gameId, gamerUsername);
		assertTrue(repository.isWinner(gameId, gamerUsername));
	}
	@Test
	void gameNotFoundTest() {
		assertThrowsExactly(GameNotFoundException.class,
				()->repository.getGame(1000));
	}
	@Test
	void gamerNotFoundTest() {
		assertThrowsExactly(GamerNotFoundException.class,
				()->repository.getGamer("kuku"));
	}
	@Order(11)
	@Test
	void getGameIdsNotStartedTest() {
		List<Long> ids = repository.getGameIdsNotStarted();
		assertTrue(ids.isEmpty());
		List<Long> expected = new ArrayList<>(3);
		expected.add(repository.createNewGame("1234"));
		expected.add(repository.createNewGame("1234"));
		List<Long>actual = repository.getGameIdsNotStarted();
		assertIterableEquals(expected, actual);
		
	}
	
	@Test
	void gameGamerDuplicationTest() {
		assertThrowsExactly(GameGamerAlreadyExistsException.class,
				() ->repository.createGameGamer(gameId, gamerUsername));
	}
	@Test
	void GameGamerNotFoundTest() {
		assertThrowsExactly (GameGamerNotFoundException.class, ()-> repository.createGameGamerMove
		(new MoveDto(1000l, gamerUsername, "1243", 2, 2)));
	}
	
	


}