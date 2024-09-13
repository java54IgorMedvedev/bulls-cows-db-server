package telran.net.games;

import java.util.HashMap;
import org.hibernate.jpa.HibernatePersistenceProvider;
import jakarta.persistence.*;

public class InitialAppl {

    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("hibernate.hbm2ddl.auto", "update");
        map.put("hibernate.show_sql", true);
        map.put("hibernate.format_sql", true);
        EntityManagerFactory emFactory = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new BullsCowsPersistenceUnitInfo(), map);
        EntityManager em = emFactory.createEntityManager();

        int gameId = 1001;  
        Game game = em.find(Game.class, gameId);
        if (game != null) {
            System.out.println(game);
        } else {
            System.out.println("Игра с ID " + gameId + " не найдена.");
        }

        em.close();
        emFactory.close();
    }
}
