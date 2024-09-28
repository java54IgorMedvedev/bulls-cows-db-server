package telran.net.games.model;

import org.json.JSONObject;

public record MoveData(String sequence, Integer bulls, Integer cows) {
   private static final String SEQUENCE_FIELD = "sequence";
private static final String BULLS_FILED = "bulls";
private static final String COWS_FIELD = "cows";

public MoveData(JSONObject jsonObject) {
	   this(jsonObject.getString(SEQUENCE_FIELD),
			   jsonObject.getInt(BULLS_FILED), jsonObject.getInt(COWS_FIELD));
   }
public String toString() {
	//TODO - creating JSON string from object
	return null;
	
}
}