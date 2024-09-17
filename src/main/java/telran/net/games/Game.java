package telran.net.games;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "game")

public class Game {
	@Id
	private long id;
	private LocalDateTime date;
	private boolean is_finished;
	private String sequence;
	public Game(long id, LocalDateTime date, boolean is_finished, String sequence) {
		this.id = id;
		this.date = date;
		this.is_finished = is_finished;
		this.sequence = sequence;
	}
	public Game() {
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public boolean isIs_finished() {
		return is_finished;
	}
	public void setIs_finished(boolean is_finished) {
		this.is_finished = is_finished;
	}
	public long getId() {
		return id;
	}
	public String getSequence() {
		return sequence;
	}
	@Override
	public String toString() {
		return "Game [id=" + id + ", date=" + date + ", is_finished=" + is_finished + ", sequence=" + sequence + "]";
	}
	
	
	
}