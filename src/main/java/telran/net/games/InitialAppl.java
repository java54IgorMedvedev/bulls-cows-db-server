package telran.net.games;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

public class InitialAppl {

    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("hibernate.hbm2ddl.auto", "update");
        map.put("hibernate.show_sql", true);
        map.put("hibernate.format_sql", true);

        EntityManagerFactory emFactory = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new BullsCowsPersistenceUnitInfo(), map);
        EntityManager em = emFactory.createEntityManager();
        JpqlQueriesRepository repository = new JpqlQueriesRepository(em);

        List<Game> games = repository.getGamesWithAverageAgeMoreThan(60);
        displayResult(games);

        List<Object[]> winnerMoves = repository.getWinnerMovesLessThan(5);
        displayResult(winnerMoves);

        List<String> gamersWithMoves = repository.getGamersWithMovesLessThan(4);
        displayResult(gamersWithMoves);

        List<Object[]> avgMovesPerGamer = repository.getAverageMovesPerGamer();
        displayResult(avgMovesPerGamer);

        List<Game> finishedGames = repository.getGamesFinished(true);
        displayResult(finishedGames);

        List<DateTimeSequence> dateTimeSequences = repository.getDateTimeSequence(LocalTime.of(12, 0));
        displayResult(dateTimeSequences);

        List<Integer> bullsList = repository.getBullsInMovesGamersBornAfter(LocalDate.of(1990, 1, 1));
        displayResult(bullsList);

        List<MinMaxAmount> distributionGamesMoves = repository.getDistributionGamesMoves(6);
        displayResult(distributionGamesMoves);

        em.close();
        emFactory.close();
    }

    private static <T> void displayResult(List<T> list) {
        list.forEach(System.out::println);
    }
}
