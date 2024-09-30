package telran.net.games.repo;

import java.time.*;
import java.util.*;

import org.hibernate.jpa.HibernatePersistenceProvider;

import jakarta.persistence.*;
import jakarta.persistence.spi.*;
import telran.net.games.entities.Game;
import telran.net.games.entities.GameGamer;
import telran.net.games.entities.Gamer;
import telran.net.games.entities.Move;
import telran.net.games.exceptions.GameGamerAlreadyExistsException;
import telran.net.games.exceptions.GameGamerNotFoundException;
import telran.net.games.exceptions.GameNotFoundException;
import telran.net.games.exceptions.GamerAlreadyExistsdException;
import telran.net.games.exceptions.GamerNotFoundException;
import telran.net.games.model.MoveData;
import telran.net.games.model.MoveDto;

public class BullsCowsRepositoryJpa implements BullsCowsRepository {
	private EntityManager em;

	public BullsCowsRepositoryJpa(PersistenceUnitInfo persistenceUnit, HashMap<String, Object> hibernateProperties) {
		EntityManagerFactory emf = new HibernatePersistenceProvider()
				.createContainerEntityManagerFactory(persistenceUnit, hibernateProperties);
		em = emf.createEntityManager();

	}

	@Override
	public Game getGame(long id) {
		Game game = em.find(Game.class, id);
		if (game == null) {
			throw new GameNotFoundException(id);
		}
		return game;
	}

	@Override
	public Gamer getGamer(String username) {
		Gamer gamer = em.find(Gamer.class, username);
		if (gamer == null) {
			throw new GamerNotFoundException(username);
		}
		return gamer;
	}

	@Override
	public long createNewGame(String sequence) {
		Game game = new Game(null, false, sequence);
		createObject(game);
		return game.getId();
	}

	private <T> void createObject(T obj) {
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			em.persist(obj);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}
	}

	@Override
	public void createNewGamer(String username, LocalDate birthdate) {
		try {
			Gamer gamer = new Gamer(username, birthdate);
			createObject(gamer);
		} catch (Exception e) {
			throw new GamerAlreadyExistsdException(username);
		}

	}

	@Override
	public boolean isGameStarted(long id) {
		Game game = getGame(id);
		return game.getDate() != null;
	}

	@Override
	public void setStartDate(long gameId, LocalDateTime dateTime) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		Game game = getGame(gameId);
		game.setDate(dateTime);
		transaction.commit();

	}

	@Override
	public boolean isGameFinished(long id) {
		Game game = getGame(id);
		return game.isfinished();
	}

	@Override
	public void setIsFinished(long gameId) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		Game game = getGame(gameId);
		game.setfinished(true);
		transaction.commit();

	}

	@Override
	public List<Long> getGameIdsNotStarted() {
		TypedQuery<Long> query = em.createQuery("select id from Game where dateTime is null", Long.class);
		return query.getResultList();
	}

	@Override
	public List<String> getGameGamers(long id) {
		TypedQuery<String> query = em.createQuery("select gamer.username from GameGamer where game.id=?1",
				String.class);
		return query.setParameter(1, id).getResultList();
	}

	@Override
	public void createGameGamer(long gameId, String username) {

		Game game = getGame(gameId);
		Gamer gamer = getGamer(username);
		try {
			GameGamer gameGamer = new GameGamer(false, game, gamer);
			createObject(gameGamer);
		} catch (Exception e) {
			throw new GameGamerAlreadyExistsException(gameId, username);
		}

	}

	@Override
	public void createGameGamerMove(MoveDto moveDto) {
		long gameId = moveDto.gameId();
		String username = moveDto.username();
		GameGamer gameGamer = getGameGamer(gameId, username);
		Move move = new Move(moveDto.sequence(), moveDto.bulls(), moveDto.cows(), gameGamer);
		createObject(move);

	}

	private GameGamer getGameGamer(Long gameId, String username) {
		TypedQuery<GameGamer> query = em.createQuery(
				"select gameGamer from GameGamer gameGamer" + " where game.id = ?1 and gamer.username = ?2",
				GameGamer.class);
		GameGamer gameGamer = query.setParameter(1, gameId).setParameter(2, username).getSingleResultOrNull();
		if (gameGamer == null) {
			throw new GameGamerNotFoundException(gameId, username);
		}
		return gameGamer;
	}

	@Override
	public List<MoveData> getAllGameGamerMoves(long gameId, String username) {
		GameGamer gameGamer = getGameGamer(gameId, username);
		TypedQuery<MoveData> query = em.createQuery("select sequence, bulls, cows from Move where gameGamer.id = ?1",
				MoveData.class);
		return query.setParameter(1, gameGamer.getId()).getResultList();
	}

	@Override
	public void setWinner(long gameId, String username) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		GameGamer gameGamer = getGameGamer(gameId, username);
		gameGamer.setWinner(true);
		transaction.commit();

	}

	@Override
	public boolean isWinner(long gameId, String username) {
		GameGamer gameGamer = getGameGamer(gameId, username);
		return gameGamer.isWinner();
	}

	@Override
	public List<Long> getIdsNonStartedGamesGamer(String username) {
		TypedQuery<Long> query = em.createQuery(
				"select game.id from GameGamer"
				+ " where game.dateTime is null "
				+ "and gamer.username = ?1", Long.class);
		return query.setParameter(1, username).getResultList();
	}

	@Override
	public List<Long> getIdsNonStartedGamesNoGamer(String username) {
		TypedQuery<Long> query = em.createQuery(
				"select game.id from Game game left join GameGamer gameGamer "
				+ "on gameGamer.game.id = game.id"
				+ " where gameGamer.game.id is null or (gameGamer.game.dateTime is null "
				+ "and gameGamer.gamer.username != ?1)", Long.class);
		return query.setParameter(1, username).getResultList();
	}

	@Override
	public List<Long> getIdsStartedGamesGamer(String username) {
		TypedQuery<Long> query = em.createQuery(
				"select game.id from GameGamer"
				+ " where game.dateTime is not null and game.isFinished = false "
				+ "and gamer.username = ?1", Long.class);
		return query.setParameter(1, username).getResultList();
	}

}