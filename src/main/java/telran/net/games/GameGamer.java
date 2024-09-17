package telran.net.games;
import jakarta.persistence.*;
@Entity
@Table(name="game_gamer")
public class GameGamer {
	@Id
	private long id;
	private boolean is_winner;
	@ManyToOne
	@JoinColumn(name = "game_id")
	private Game game;
	@ManyToOne
	@JoinColumn(name = "gamer_id")
	private Gamer gamer;
	public long getId() {
		return id;
	}
	public boolean isIs_winner() {
		return is_winner;
	}
	public Game getGame() {
		return game;
	}
	public Gamer getGamer() {
		return gamer;
	}
	@Override
	public String toString() {
		return "GameGamer [id=" + id + ", is_winner=" + is_winner + ", game=" + game.getId() + ", gamer=" + gamer.getUsername() + "]";
	}
	
}