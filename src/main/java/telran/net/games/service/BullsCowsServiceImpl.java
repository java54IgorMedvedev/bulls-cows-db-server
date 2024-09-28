package telran.net.games.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import telran.net.games.entities.*;
import telran.net.games.exceptions.*;
import telran.net.games.model.MoveData;
import telran.net.games.model.MoveDto;
import telran.net.games.repo.BullsCowsRepository;

public class BullsCowsServiceImpl implements BullsCowsService {
    private BullsCowsRepository bcRepository;
    private BullsCowsGameRunner bcRunner;

    public BullsCowsServiceImpl(BullsCowsRepository bcRepository, BullsCowsGameRunner bcRunner) {
        this.bcRepository = bcRepository;
        this.bcRunner = bcRunner;
    }

    @Override
    public long createGame() {
        return bcRepository.createNewGame(bcRunner.getRandomSequence());
    }

    @Override
    public List<String> startGame(long gameId) {
        if (bcRepository.isGameStarted(gameId)) {
            throw new GameAlreadyStartedException(gameId);
        }
        List<String> result = bcRepository.getGameGamers(gameId);
        if (result.isEmpty()) {
            throw new NoGamerInGameException(gameId);
        }
        bcRepository.setStartDate(gameId, LocalDateTime.now());
        return result;
    }

    @Override
    public void registerGamer(String username, LocalDate birthDate) {
        bcRepository.createNewGamer(username, birthDate);
    }

    @Override
    public void gamerJoinGame(long gameId, String username) {
        if (bcRepository.isGameStarted(gameId)) {
            throw new GameAlreadyStartedException(gameId);
        }
        bcRepository.createGameGamer(gameId, username);
    }

    @Override
    public List<Long> getNotStartedGames() {
        return bcRepository.getGameIdsNotStarted();
    }

    @Override
    public List<MoveData> moveProcessing(String moveSequence, long gameId, String username) {
        if (!bcRunner.checkGuess(moveSequence)) {
            throw new IncorrectMoveSequenceException(moveSequence, bcRunner.nDigits);
        }
        bcRepository.getGamer(username);
        if (!bcRepository.isGameStarted(gameId)) {
            throw new GameNotStartedException(gameId);
        }
        if (bcRepository.isGameFinished(gameId)) {
            throw new GameFinishedException(gameId);
        }

        String toBeGuessedSequence = getSequence(gameId);
        MoveData moveData = bcRunner.moveProcessing(moveSequence, toBeGuessedSequence);
        MoveDto moveDto = new MoveDto(gameId, username, moveSequence, moveData.bulls(), moveData.cows());
        bcRepository.createGameGamerMove(moveDto);
        if (bcRunner.checkGameFinished(moveData)) {
            finishGame(gameId, username);
        }
        return bcRepository.getAllGameGamerMoves(gameId, username);
    }

    private void finishGame(long gameId, String username) {
        bcRepository.setIsFinished(gameId);
        bcRepository.setWinner(gameId, username);
    }

    @Override
    public boolean gameOver(long gameId) {
        return bcRepository.isGameFinished(gameId);
    }

    @Override
    public List<String> getGameGamers(long gameId) {
        bcRepository.getGame(gameId);
        return bcRepository.getGameGamers(gameId);
    }

    String getSequence(long gameId) {
        Game game = bcRepository.getGame(gameId);
        return game.getSequence();
    }

    @Override
    public String loginGamer(String username) {
        Gamer gamer = bcRepository.getGamer(username);
        return gamer.getUsername();
    }

    @Override
    public List<Long> getNotStartedGamesWithGamer(String username) {
        return bcRepository.getNotStartedGamesWithGamer(username);
    }

    @Override
    public List<Long> getNotStartedGamesWithNoGamer(String username) {
        return bcRepository.getNotStartedGamesWithNoGamer(username);
    }

    @Override
    public List<Long> getStartedGamesWithGamer(String username) {
        return bcRepository.getStartedGamesWithGamer(username);
    }
}
