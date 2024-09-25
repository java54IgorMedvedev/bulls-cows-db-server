package telran.net.games.entities;
import java.time.LocalDate;

import jakarta.persistence.*;
@Entity
@Table(name = "gamer")
public class Gamer {
	@Id
	private String username;
	private LocalDate birthdate;
	public Gamer(String username, LocalDate birthdate) {
		this.username = username;
		this.birthdate = birthdate;
	}
	public Gamer() {
	}
	public String getUsername() {
		return username;
	}
	public LocalDate getBirthdate() {
		return birthdate;
	}	
}