package telran.net.games.proxy;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import telran.games.BullsCowsService;
import telran.games.dto.Move;
import telran.games.dto.MoveResult;
import telran.net.games.model.GameGamerDto;
import telran.net.games.model.MoveData;

public class BullsCowsProxy implements BullsCowsService {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public BullsCowsProxy(String host, int port) throws IOException {
        socket = new Socket(host, port); 
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }
    
    public String loginGamer(String username) {
        JSONObject request = new JSONObject();
        request.put("type", "loginGamer");
        request.put("username", username);
        sendRequest(request);
        
        try {
            String response = (String) input.readObject();
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getString("gamer");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void registerGamer(String username, String birthdate) {
        JSONObject request = new JSONObject();
        request.put("type", "registerGamer");
        request.put("username", username);
        request.put("birthdate", birthdate);
        sendRequest(request);
    }

    public List<String> startGame(long gameId) {
        JSONObject request = new JSONObject();
        request.put("type", "startGame");
        request.put("gameId", gameId);
        sendRequest(request);
        
        try {
            String response = (String) input.readObject(); 
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getJSONArray("gamers").toList().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public MoveData moveProcessing(String sequence, long gameId, String username) {
        JSONObject request = new JSONObject();
        request.put("type", "moveProcessing");
        request.put("sequence", sequence);
        request.put("gameId", gameId);
        request.put("username", username);
        return sendMoveRequest(request);
    }

    private void sendRequest(JSONObject request) {
        try {
            output.writeObject(request.toString());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MoveData sendMoveRequest(JSONObject request) {
        try {
            output.writeObject(request.toString());
            output.flush();
            String response = (String) input.readObject();
            return new MoveData(new JSONObject(response));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            BullsCowsProxy proxy = new BullsCowsProxy("localhost", 5000);
            proxy.registerGamer("JohnDoe", "1990-05-20"); 
            proxy.startGame(1L); 
            MoveData moveData = proxy.moveProcessing("1234", 1L, "JohnDoe"); 
            System.out.println(moveData); 
            proxy.close(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public long createNewGame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MoveResult> getResults(Move arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGameOver(long arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
