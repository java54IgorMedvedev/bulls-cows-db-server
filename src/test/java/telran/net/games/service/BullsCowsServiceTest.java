package telran.net.games.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import telran.net.games.BullsCowsTestPersistenceUnitInfo;
import telran.net.games.entities.*;
import telran.net.games.exceptions.*;
import telran.net.games.model.MoveData;
import telran.net.games.repo.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BullsCowsServiceTest {
	private static final int N_DIGITS = 4;
	static BullsCowsRepository repository;
	static BullsCowsService bcService;
	static long gameIdNormalFlow;
	static String username = "gamer1";
	static String toBeGuessedSequence;
	static BullsCowsGameRunner bcRunner;
	static LocalDate birthDate = LocalDate.of(2000, 1, 1);
	static long gameIdAltFlow;
	static {
		HashMap<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.put("hibernate.hbm2ddl.auto", "create");
		repository = new BullsCowsRepositoryJpa
				(new BullsCowsTestPersistenceUnitInfo(), hibernateProperties);
		bcRunner = new BullsCowsGameRunner(N_DIGITS);
		bcService = new BullsCowsServiceImpl(repository, bcRunner);
		
	}
	//Normal service states flow
	@Order(1)
	@Test
	void createGameTest() {
		gameIdNormalFlow = bcService.createGame();
		Game game = repository.getGame(gameIdNormalFlow);
		assertNull(game.getDate());
		assertFalse(game.isfinished());
		toBeGuessedSequence = ((BullsCowsServiceImpl)bcService).getSequence(gameIdNormalFlow);
		assertTrue(bcRunner.checkGuess(toBeGuessedSequence));
		
	}
	@Order(2)
	@Test
	void getNotStartedGamesTest() {
		List<Long> ids = bcService.getNotStartedGames();
		assertEquals(1, ids.size());
		assertEquals(gameIdNormalFlow, ids.get(0));
	}
	@Order(3)
	@Test
	void registerGamerTest() {
		bcService.registerGamer(username, birthDate);
		Gamer gamer = repository.getGamer(username);
		assertEquals(username, gamer.getUsername());
		assertEquals(birthDate, gamer.getBirthdate());
	}
	@Order(4)
	@Test
	void joinGameTest() {
		bcService.gamerJoinGame(gameIdNormalFlow, username);
		List<String> gamers = repository.getGameGamers(gameIdNormalFlow);
		runGamersTest(gamers);
	}
	
	
	@Order(5)
	@Test
	void getGameGamersTest() {
		List<String> gamers = bcService.getGameGamers(gameIdNormalFlow);
		runGamersTest(gamers);
	}
	@Order(6)
	@Test
	void startGameTest() {
		bcService.startGame(gameIdNormalFlow);
		assertTrue(repository.getGameIdsNotStarted().isEmpty());
		assertThrowsExactly(GameAlreadyStartedException.class,
				() -> bcService.startGame(gameIdNormalFlow));
	}
	@Order(7) 
	@Test
	void notWinnerMoveTest() {
		String twoBullsTwoCowsSequence = getTwoBullsTwoCowsSequence();
		List<MoveData> moves = 
				bcService.moveProcessing(twoBullsTwoCowsSequence,
						gameIdNormalFlow, username);
		MoveData moveDataExpected = new MoveData(twoBullsTwoCowsSequence,
				2, 2);
		assertEquals(1, moves.size());
		assertEquals(moveDataExpected, moves.get(0));
		assertFalse(repository.isGameFinished(gameIdNormalFlow));
		assertFalse(repository.isWinner(gameIdNormalFlow, username));
		assertFalse(bcService.gameOver(gameIdNormalFlow));
	}
	@Order(8)
	@Test
	void winnerMoveTest() {
		List<MoveData> moves = 
				bcService.moveProcessing(toBeGuessedSequence,
						gameIdNormalFlow, username);
		MoveData moveDataExpected = new MoveData(toBeGuessedSequence,
				N_DIGITS, 0);
		assertEquals(2, moves.size());
		assertEquals(moveDataExpected, moves.get(1));
		assertTrue(repository.isGameFinished(gameIdNormalFlow));
		assertTrue(repository.isWinner(gameIdNormalFlow, username));
		assertTrue(bcService.gameOver(gameIdNormalFlow));
	}
	
	private void runGamersTest(List<String> gamers) {
		assertEquals(1, gamers.size());
		assertEquals(username, gamers.get(0));
	}
	private String getTwoBullsTwoCowsSequence() {
		char[] array = toBeGuessedSequence.toCharArray();
		char tmp = array[N_DIGITS - 1];
		array[N_DIGITS - 1] = array[0];
		array[0] = tmp;
		return new String(array);
	}
	//Alternative states flow
	@Order(9)
	@Test
	void noGamerExceptionTest() {
		gameIdAltFlow = bcService.createGame();
		toBeGuessedSequence = ((BullsCowsServiceImpl)bcService).getSequence(gameIdAltFlow);
		assertThrowsExactly(NoGamerInGameException.class,
				() -> bcService.startGame(gameIdAltFlow));
		
	}
	
	@Order(10)
	@Test
	void moveProcessingExceptions() {
		bcService.gamerJoinGame(gameIdAltFlow, username);
		bcService.startGame(gameIdAltFlow);
		assertThrowsExactly(IncorrectMoveSequenceException.class,
				() -> bcService.moveProcessing("1123", gameIdAltFlow, username));
		assertThrowsExactly(IncorrectMoveSequenceException.class,
				() -> bcService.moveProcessing("123", gameIdAltFlow, username));
		assertThrowsExactly(IncorrectMoveSequenceException.class,
				() -> bcService.moveProcessing("12345", gameIdAltFlow, username));
		bcService.moveProcessing(toBeGuessedSequence, gameIdAltFlow, username);
		assertThrowsExactly(GameFinishedException.class,
				() -> bcService.moveProcessing(toBeGuessedSequence, gameIdAltFlow, username));
	}
	@Order(11)
	@Test
	void joinGamerGameStarted() {
		assertThrowsExactly(GameAlreadyStartedException.class,
				() -> bcService.gamerJoinGame(gameIdAltFlow, username + "1") );
	}
	@Order(12)
	@Test
	void noGameFoundExceptionTest() {
		assertThrowsExactly(GameNotFoundException.class, () -> bcService.getGameGamers(1000000));
	}
}