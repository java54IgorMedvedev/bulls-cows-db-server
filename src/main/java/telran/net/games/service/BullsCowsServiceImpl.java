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
		if(bcRepository.isGameStarted(gameId)) {
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
	    if (bcRepository.getGamer(username) != null) {
	        throw new IllegalArgumentException("Gamer already exists: " + username);
	    }
	    bcRepository.createNewGamer(username, birthDate);
	}

	@Override
	public void gamerJoinGame(long gameId, String username) {
	    if (bcRepository.isGameStarted(gameId)) {
	        throw new IllegalStateException("Game already started: " + gameId);
	    }
	    if (bcRepository.getGamer(username) == null) {
	        throw new IllegalArgumentException("Gamer not found: " + username);
	    }
	    bcRepository.createGameGamer(gameId, username);
	}

	@Override
	public List<Long> getNotStartedGames() {
	    return bcRepository.getGameIdsNotStarted();
	}

	@Override
	public List<MoveData> moveProcessing(String sequence, long gameId, String username) {
	    Game game = bcRepository.getGame(gameId);
	    if (game == null) {
	        throw new GameNotFoundException(gameId);
	    }
	    if (!bcRepository.isGameStarted(gameId)) {
	        throw new GameNotStartedException(gameId);
	    }
	    if (bcRepository.isGameFinished(gameId)) {
	        throw new GameFinishedException(gameId);
	    }
	    
	    if (!bcRunner.checkGuess(sequence)) {
	        throw new IncorrectMoveSequenceException(sequence);
	    }
	    
	    MoveData moveData = bcRunner.moveProcessing(sequence, game.getSequence());
	    
	    bcRepository.createGameGamerMove(new MoveDto(gameId, username, sequence, moveData.bulls(), moveData.cows()));
	    
	    if (bcRunner.checkGameFinished(moveData)) {
	        bcRepository.setIsFinished(gameId);
	        bcRepository.setWinner(gameId, username);
	    }
	    
	    return bcRepository.getAllGameGamerMoves(gameId, username);
	}
	
	@Override
	public boolean gameOver(long gameId) {
	    return bcRepository.isGameFinished(gameId);
	}

	@Override
	public List<String> getGameGamers(long gameId) {
	    List<String> gamers = bcRepository.getGameGamers(gameId);
	    if (gamers == null || gamers.isEmpty()) {
	        throw new IllegalArgumentException("Game not found: " + gameId);
	    }
	    return gamers;
	}

	String getSequence(long gameId) {
		Game game = bcRepository.getGame(gameId);
		return game.getSequence();
	}
	
}