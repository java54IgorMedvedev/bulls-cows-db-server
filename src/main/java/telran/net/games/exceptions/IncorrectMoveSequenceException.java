package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class IncorrectMoveSequenceException extends IllegalArgumentException {
	public IncorrectMoveSequenceException(String sequence, int nDigits) {
		super(String.format("Wrong sequence - \"%s\", must be %d unrepeated digits"
				,sequence, nDigits ));
	}
}