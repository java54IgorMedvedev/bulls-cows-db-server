package telran.net.games.controller;

import java.util.List;

import java.util.stream.Collectors;

import org.json.JSONObject;

import telran.net.Protocol;
import telran.net.Request;
import telran.net.Response;
import telran.net.ResponseCode;
import telran.net.games.model.*;
import telran.net.games.service.BullsCowsService;
public class BullsCowsProtocol implements Protocol {
	private BullsCowsService bcService;
	
	public BullsCowsProtocol(BullsCowsService bcService) {
		this.bcService = bcService;
	}

	@Override
	public Response getResponse(Request request) {
		String requestType = request.requestType();
		String requestData = request.requestData();
		Response response;
		try {
			response = switch (requestType) {
			case "createGame" -> createGame(requestData);
			case "startGame" -> startGame(requestData);
			case "registerGamer" -> registerGamer(requestData);
			case "gamerJoinGame" -> gamerJoinGame(requestData);
			case "getNotStartedGames" -> getNotStartedGames(requestData);
			case "moveProcessing" -> moveProcessing(requestData);
			case "gameOver" -> gameOver(requestData);
			case "getGameGamers" -> getGameGamers(requestData);
			case "getNotStartedGamesWithGamer" -> getNotStartedGamesWithGamer(requestData);
			case "getNotStartedGamesWithOutGamer" -> getNotStartedGamesWithOutGamer(requestData);
			case "getStartedGamesWithGamer" -> getStartedGamesWithGamer(requestData);
			case "loginGamer" -> loginGamer(requestData);
			default -> new Response(ResponseCode.WRONG_REQUEST_TYPE,
					requestType);
			};
		} catch (Exception e) {
			response = new Response(ResponseCode.WRONG_REQUEST_DATA,
					e.getMessage());
		}
		return response;
	}
	Response createGame(String requestData) {
		long gameId = bcService.createGame();
		String responseString = Long.toString(gameId);
		return getResponseOk(responseString );
	}
	Response startGame(String requestData) {
		long gameId = Long.parseLong(requestData);
		List<String> gamers = bcService.startGame(gameId);
		String responseString = resultsToJSON(gamers);
		return getResponseOk(responseString );
	}
	Response registerGamer(String requestData) {
		UsernameBirthdate nameDate = new UsernameBirthdate(new JSONObject(requestData));
		
		bcService.registerGamer(nameDate.username(), nameDate.birthDate());
		String responseString = "";
		return getResponseOk(responseString );
	}
	Response gamerJoinGame(String requestData) {
		GameGamerDto gameGamer = new GameGamerDto(new JSONObject(requestData)) ;
		bcService.gamerJoinGame(gameGamer.gameId(), gameGamer.username());
		String responseString = "";
		return getResponseOk(responseString );
	}
	Response getNotStartedGames(String requestData) {
		List<Long> ids = bcService.getNotStartedGames();
		String responseString = resultsToJSON(ids);
		return getResponseOk(responseString );
	}
	Response moveProcessing(String requestData) {
		SequenceGameGamerDto sggd =
				new SequenceGameGamerDto(new JSONObject(requestData));
		String moveSequence = sggd.sequence();
		long gameId = sggd.gameId();
		String username = sggd.username();
		List<MoveData> results = bcService.moveProcessing(moveSequence, gameId, username);
		String responseString = resultsToJSON(results);
		return getResponseOk(responseString );
	}
	Response gameOver(String requestData) {
		long gameId = Long.parseLong(requestData);
		boolean res = bcService.gameOver(gameId);
		String responseString = Boolean.toString(res);
		return getResponseOk(responseString );
	}
	Response getGameGamers(String requestData) {
		long gameId = Long.parseLong(requestData);
		List<String> gamers = bcService.getGameGamers(gameId);
		String responseString = resultsToJSON(gamers);
		return getResponseOk(responseString );
	}
	Response getNotStartedGamesWithGamer(String requestData) {
		String username = requestData;
		List<Long> ids = bcService.getNotStartedGamesWithGamer(username);
		String responseString = resultsToJSON(ids);
		return getResponseOk(responseString );
	}
	Response getNotStartedGamesWithOutGamer(String requestData) {
		String username = requestData;
		List<Long> ids = bcService.getNotStartedGamesWithOutGamer(username);
		String responseString = resultsToJSON(ids);
		return getResponseOk(responseString );
	}
	Response getStartedGamesWithGamer(String requestData) {
		String username = requestData;
		List<Long> ids = bcService.getStartedGamesWithGamer(username);
		String responseString = resultsToJSON(ids);
		return getResponseOk(responseString );
	}
	Response loginGamer(String requestData) {
		String username = requestData;
		String usernameFromServer = bcService.loginGamer(username);
		String responseString = usernameFromServer;
		return getResponseOk(responseString );
	}

	private Response getResponseOk(String responseString) {
		
		return new Response(ResponseCode.OK, responseString);
	}
private <T> String resultsToJSON(List<T> res) {
		
		return res.stream().map(T::toString)
				.collect(Collectors.joining(";"));
	}
}