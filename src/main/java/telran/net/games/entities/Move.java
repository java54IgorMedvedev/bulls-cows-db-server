package telran.net.games.entities;
import jakarta.persistence.*;
@Entity
@Table(name="move")
public class Move {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String sequence;
	private int bulls;
	private int cows;
	@ManyToOne
	@JoinColumn(name="game_gamer_id")
	private GameGamer gameGamer;
	public long getId() {
		return id;
	}
	public String getSequence() {
		return sequence;
	}
	public int getBulls() {
		return bulls;
	}
	public int getCows() {
		return cows;
	}
	public GameGamer getGameGamer() {
		return gameGamer;
	}
	public Move() {
	}
	public Move(String sequence, int bulls, int cows, GameGamer gameGamer) {
		this.sequence = sequence;
		this.bulls = bulls;
		this.cows = cows;
		this.gameGamer = gameGamer;
	}	
}