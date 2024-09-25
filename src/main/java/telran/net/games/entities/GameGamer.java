package telran.net.games.entities;
import jakarta.persistence.*;
@Entity
@Table(name="game_gamer",uniqueConstraints =
{@UniqueConstraint(columnNames = {"game_id", "gamer_id"} )})
public class GameGamer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="is_winner", nullable = false)
	private boolean isWinner;
	@ManyToOne
	@JoinColumn(name = "game_id")
	private Game game;
	@ManyToOne
	@JoinColumn(name = "gamer_id")
	private Gamer gamer;
	public long getId() {
		return id;
	}
	public boolean isWinner() {
		return isWinner;
	}
	public Game getGame() {
		return game;
	}
	public Gamer getGamer() {
		return gamer;
	}
	public GameGamer() {
		
	}
	public GameGamer(boolean isWinner, Game game, Gamer gamer) {
		this.isWinner = isWinner;
		this.game = game;
		this.gamer = gamer;
	}
	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}	
}