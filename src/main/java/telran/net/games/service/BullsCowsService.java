package telran.net.games.service;

import java.time.*;
import java.util.*;

import telran.net.games.model.MoveData;

public interface BullsCowsService {
long createGame();//returns ID of the created game
List<String> startGame(long gameId); //returns list of user (gamer) names
void registerGamer(String username, LocalDate birthDate);
void gamerJoinGame(long gameId, String username);
List<Long> getNotStartedGames();
List<MoveData> moveProcessing(String sequence, long gameId, String username);
boolean gameOver(long gameId);
List<String> getGameGamers(long gameId);
List<Long> getNotStartedGamesWithGamer(String username);  
List<Long> getNotStartedGamesWithNoGamer(String username);  
List<Long> getStartedGamesWithGamer(String username);  

String loginGamer(String username);
}