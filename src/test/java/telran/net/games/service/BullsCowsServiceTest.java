package telran.net.games.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import telran.net.games.BullsCowsTestPersistenceUnitInfo;
import telran.net.games.entities.Game;
import telran.net.games.exceptions.*;
import telran.net.games.model.MoveData;
import telran.net.games.repo.BullsCowsRepository;
import telran.net.games.repo.BullsCowsRepositoryJpa;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BullsCowsServiceTest {
    private static final int N_DIGITS = 4;
    static BullsCowsRepository repository;
    static BullsCowsService bcService;
    static long gameId;
    static String gamerUsername = "gamer1";

    static {
        HashMap<String, Object> hibernateProperties = new HashMap<>();
        hibernateProperties.put("hibernate.hbm2ddl.auto", "create");
        repository = new BullsCowsRepositoryJpa(new BullsCowsTestPersistenceUnitInfo(), hibernateProperties);
        BullsCowsGameRunner bcRunner = new BullsCowsGameRunner(N_DIGITS);
        bcService = new BullsCowsServiceImpl(repository, bcRunner);
    }

    @Order(1)
    @Test
    void createGameTest() {
        gameId = bcService.createGame();
        Game game = repository.getGame(gameId);
        assertNotNull(game);
        assertNull(game.getDate());
        assertFalse(game.isfinished());
    }

    @Order(2)
    @Test
    void registerGamerTest() {
        String gamerName = "gamer1";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        bcService.registerGamer(gamerName, birthDate);
        Assertions.assertDoesNotThrow(() -> bcService.gamerJoinGame(gameId, gamerName));
    }

    @Order(3)
    @Test
    void gamerJoinGameTest() {
        bcService.gamerJoinGame(gameId, gamerUsername);
        List<String> gamers = repository.getGameGamers(gameId);
        assertEquals(1, gamers.size());
        assertEquals(gamerUsername, gamers.get(0));
    }

    @Order(4)
    @Test
    void startGameTest() {
        List<String> gamersBeforeStart = repository.getGameGamers(gameId);
        assertEquals(1, gamersBeforeStart.size());
        assertEquals(gamerUsername, gamersBeforeStart.get(0));
        List<String> gamersAfterStart = bcService.startGame(gameId);
        assertEquals(1, gamersAfterStart.size());
        assertEquals(gamerUsername, gamersAfterStart.get(0));
    }


    @Order(5)
    @Test
    void moveProcessingTest() {
        bcService.startGame(gameId); 
        List<MoveData> moveDataList = bcService.moveProcessing("1234", gameId, gamerUsername);
        assertFalse(moveDataList.isEmpty());
        MoveData moveData = moveDataList.get(0);
        assertEquals("1234", moveData.sequence());
        assertNotNull(moveData);
    }

    @Order(6)
    @Test
    void gameAlreadyStartedExceptionTest() {
        bcService.startGame(gameId);
        assertThrows(GameAlreadyStartedException.class, () -> {
            bcService.startGame(gameId);
        });
    }

    @Order(7)
    @Test
    void noGamerInGameExceptionTest() {
        long newGameId = bcService.createGame();
        assertThrows(NoGamerInGameException.class, () -> {
            bcService.startGame(newGameId);
        });
    }

    @Order(8)
    @Test
    void incorrectMoveSequenceExceptionTest() {
        bcService.startGame(gameId); // Ensure game is started
        bcService.moveProcessing("1234", gameId, gamerUsername);
        assertThrows(IncorrectMoveSequenceException.class, () -> {
            bcService.moveProcessing("5678", gameId, gamerUsername);
        });
    }

    @Order(9)
    @Test
    void gameNotStartedExceptionTest() {
        long newGameId = bcService.createGame();
        assertThrows(GameNotStartedException.class, () -> {
            bcService.moveProcessing("1234", newGameId, gamerUsername);
        });
    }

    @Order(10)
    @Test
    void gameFinishedExceptionTest() {
        bcService.startGame(gameId); 
        bcService.moveProcessing("1234", gameId, gamerUsername);
        assertThrows(GameFinishedException.class, () -> {
            bcService.moveProcessing("1234", gameId, gamerUsername);
        });
    }
}
