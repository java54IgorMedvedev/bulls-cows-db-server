package telran.net.games;
import jakarta.persistence.*;
@Entity
@Table(name="move")
public class Move {
	@Id
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
	@Override
	public String toString() {
		return "Move [id=" + id + ", sequence=" + sequence + ", bulls=" + bulls + ", cows=" + cows + "]";
	}
	
	
}