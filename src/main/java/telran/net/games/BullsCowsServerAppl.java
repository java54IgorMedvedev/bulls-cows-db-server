package telran.net.games;

import java.util.HashMap;

import telran.net.Protocol;
import telran.net.TcpServer;
import telran.net.games.config.BullsCowsPersistenceUnitInfo;
import telran.net.games.controller.BullsCowsProtocol;
import telran.net.games.repo.*;
import telran.net.games.service.*;

public class BullsCowsServerAppl {

	private static final int PORT = 5000;
	private static final int N_DIGITS = 0;

	public static void main(String[] args) {
		Protocol bullsCowsProtocol = getBullsCowsProtocol();
		TcpServer server = new TcpServer(bullsCowsProtocol , PORT);
		server.run();

	}

	private static Protocol getBullsCowsProtocol() {
		HashMap<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
		BullsCowsRepository repository = new BullsCowsRepositoryJpa
				(new BullsCowsPersistenceUnitInfo(), hibernateProperties);
		BullsCowsGameRunner bcRunner = new BullsCowsGameRunner(N_DIGITS);
		BullsCowsService bcService = new BullsCowsServiceImpl(repository, bcRunner);
		return new BullsCowsProtocol(bcService);
	}

}