package telran.net.games;

public record MoveDto(Long gameId, String username, String sequence, 
		Integer bulls, Integer cows) {
}
