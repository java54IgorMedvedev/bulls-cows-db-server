package telran.net.games;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class JpqlQueriesRepository {
	private EntityManager em;

	public JpqlQueriesRepository(EntityManager em) {
		this.em = em;
	}

	public List<Game> getGamesFinished(boolean isFinished) {
		TypedQuery<Game> query = em.createQuery(
				"select game from Game game where is_finished=?1",
				Game.class);
		List<Game> res = query.setParameter(1, isFinished).getResultList();
		return res;
	}

	public List<DateTimeSequence> getDateTimeSequence(LocalTime time) {
		TypedQuery<DateTimeSequence> query = em.createQuery(
				"select date, sequence "
				+ "from Game where cast(date as time) < :time",
				DateTimeSequence.class);
		List<DateTimeSequence> res = query.setParameter("time", time).getResultList();
		return res;
	}

	public List<Integer> getBullsInMovesGamersBornAfter(LocalDate afterDate) {
		TypedQuery<Integer> query = em.createQuery(
				"select bulls from Move where gameGamer.gamer.birthdate > ?1",
				Integer.class);
		List<Integer> res = query.setParameter(1, afterDate).getResultList();
		return res;
	}

	public List<MinMaxAmount> getDistributionGamesMoves(int interval) {
		TypedQuery<MinMaxAmount> query = em.createQuery(
				"select floor(game_moves / :interval) * :interval as min_moves, "
				+ "floor(game_moves / :interval) * :interval + (:interval - 1) as max_moves, "
				+ "count(*) as amount "
				+ "from (select count(*) as game_moves from Move "
				+ "group by gameGamer.game.id) "
				+ "group by min_moves, max_moves order by min_moves",
				MinMaxAmount.class);
		List<MinMaxAmount> res = query.setParameter("interval", interval).getResultList();
		return res;
	}

	public List<Game> getGamesWithAverageAgeMoreThan(int minAge) {
	    List<Object[]> results = em.createQuery(
	            "select gg.game.id, g.birthdate from GameGamer gg "
	            + "join Gamer g on gg.gamer.username = g.username", 
	            Object[].class).getResultList();

	    Map<Long, List<LocalDate>> gameBirthdates = new HashMap<>();
	    for (Object[] result : results) {
	        Long gameId = (Long) result[0];
	        LocalDate birthdate = (LocalDate) result[1]; 
	        gameBirthdates.computeIfAbsent(gameId, k -> new ArrayList<>()).add(birthdate);
	    }

	    List<Game> games = new ArrayList<>();
	    for (Map.Entry<Long, List<LocalDate>> entry : gameBirthdates.entrySet()) {
	        Long gameId = entry.getKey();
	        List<LocalDate> birthdates = entry.getValue();
	        double averageAge = birthdates.stream()
	                .mapToInt(bd -> Period.between(bd, LocalDate.now()).getYears())
	                .average().orElse(0.0);
	        if (averageAge > minAge) {
	            Game game = em.find(Game.class, gameId);
	            if (game != null) {
	                games.add(game);
	            }
	        }
	    }

	    return games;
	}




	public List<Object[]> getWinnerMovesLessThan(int moves) {
		TypedQuery<Object[]> query = em.createQuery(
				"select gg.game.id, count(m) from GameGamer gg "
				+ "join Move m on m.gameGamer.id = gg.id "
				+ "where gg.is_winner = true "
				+ "group by gg.game.id having count(m) < :moves", 
				Object[].class);
		return query.setParameter("moves", moves).getResultList();
	}

	public List<String> getGamersWithMovesLessThan(int moves) {
		TypedQuery<String> query = em.createQuery(
				"select distinct gg.gamer.username from GameGamer gg "
				+ "join Move m on m.gameGamer.id = gg.id "
				+ "group by gg.game.id, gg.gamer.username "
				+ "having count(m) < :moves", 
				String.class);
		return query.setParameter("moves", moves).getResultList();
	}

	public List<Object[]> getAverageMovesPerGamer() {
	    String jpql = "select gg.game.id, round(avg(moveCount), 1) " +
	                  "from ( " +
	                  "    select gg.game.id as gameId, gg.gamer.username as gamerId, count(m.id) as moveCount " +
	                  "    from GameGamer gg " +
	                  "    join Move m on m.gameGamer.id = gg.id " +
	                  "    group by gg.game.id, gg.gamer.username " +
	                  ") as subquery " +
	                  "group by subquery.gameId";

	    TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
	    return query.getResultList();
	}


}
