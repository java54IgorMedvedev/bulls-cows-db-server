package telran.net.games.model;

public record MoveDto(Long gameId, String username, String sequence,
		Integer bulls, Integer cows) {

}