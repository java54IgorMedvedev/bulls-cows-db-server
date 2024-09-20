package telran.net.games;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import org.hibernate.jpa.HibernatePersistenceProvider;
import jakarta.persistence.*;
import jakarta.persistence.spi.*;
import telran.net.games.exceptions.GameNotFoundException;
import telran.net.games.exceptions.GamerAlreadyExistsdException;
import telran.net.games.exceptions.GamerNotFoundException;

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

    @Override
    public void createNewGamer(String username, LocalDate birthdate) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Gamer gamer = new Gamer(username, birthdate);
            em.persist(gamer);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new GamerAlreadyExistsdException(username);
        }
    }

    @Override
    public boolean isGameStarted(long id) {
        TypedQuery<Boolean> query = em.createQuery(
                "select (g.dateTime is not null) from Game g where g.id = :id", Boolean.class);
        query.setParameter("id", id);
        return query.getSingleResult();
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
        TypedQuery<Boolean> query = em.createQuery(
                "select g.isFinished from Game g where g.id = :id", Boolean.class);
        query.setParameter("id", id);
        return query.getSingleResult();
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
    public void setGameGamerWinner(long gameId, String userName) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            TypedQuery<GameGamer> query = em.createQuery(
                    "select gg from GameGamer gg where gg.game.id = :gameId and gg.gamer.username = :userName",
                    GameGamer.class);
            query.setParameter("gameId", gameId);
            query.setParameter("userName", userName);
            GameGamer gameGamer = query.getSingleResult();
            gameGamer.setWinner(true); 
            em.merge(gameGamer); 
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e; 
        }
    }

    @Override
    public List<Long> getGameIdsNotStarted() {
        TypedQuery<Long> query = em.createQuery(
                "select g.id from Game g where g.dateTime is null", Long.class);
        return query.getResultList();
    }

    @Override
    public List<String> getGameGamers(long id) {
        TypedQuery<String> query = em.createQuery(
                "select gg.gamer.username from GameGamer gg where gg.game.id = :gameId", String.class);
        query.setParameter("gameId", id);
        return query.getResultList();
    }

    @Override
    public void createGameGamer(long gameId, String username) {
        Game game = getGame(gameId);
        Gamer gamer = getGamer(username);
        GameGamer gameGamer = new GameGamer(false, game, gamer);
        createObject(gameGamer);
    }

    @Override
    public void createGameGamerMove(MoveDto moveDto) {
        GameGamer gameGamer = em.createQuery(
                "select gg from GameGamer gg where gg.game.id = :gameId and gg.gamer.username = :username",
                GameGamer.class)
                .setParameter("gameId", moveDto.gameId())
                .setParameter("username", moveDto.username())
                .getSingleResult();
        Move move = new Move(moveDto.sequence(), moveDto.bulls(), moveDto.cows(), gameGamer);
        createObject(move);
    }

    @Override
    public List<MoveData> getAllGameGamerMoves(long gameId, String username) {
        TypedQuery<MoveData> query = em.createQuery(
                "select new telran.net.games.MoveData(m.sequence, m.bulls, m.cows) from Move m where m.gameGamer.game.id = :gameId and m.gameGamer.gamer.username = :username",
                MoveData.class);
        query.setParameter("gameId", gameId);
        query.setParameter("username", username);
        return query.getResultList();
    }

    @Override
    public void setWinner(long gameId, String username) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            GameGamer gameGamer = em.createQuery(
                    "SELECT gg FROM GameGamer gg WHERE gg.game.id = :gameId AND gg.gamer.username = :username", 
                    GameGamer.class)
                    .setParameter("gameId", gameId)
                    .setParameter("username", username)
                    .getSingleResult();

            gameGamer.setWinner(true);
            em.merge(gameGamer); 
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e; 
        }
    }

    @Override
    public boolean isWinner(long gameId, String username) {
        TypedQuery<Boolean> query = em.createQuery(
                "select gg.isWinner from GameGamer gg where gg.game.id = :gameId and gg.gamer.username = :username",
                Boolean.class);
        query.setParameter("gameId", gameId);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    private <T> void createObject(T obj) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(obj);
        transaction.commit();
    }
}
