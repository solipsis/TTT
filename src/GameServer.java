import java.net.ServerSocket;
import java.util.ArrayList;


public class GameServer {
	
	private static final int PORT = 5500;
	
	ArrayList<Player> players;
	ServerSocket listener;
	
	public GameServer() {
		players = new ArrayList<Player>();
	}
	
	public void addPlayer(Player player) {
		listener = new ServerSocket(PORT);
		try {
			while(true) {
				new Handler(listener.accept())start();
			}
		} finally {
			listener.close();
		}
	}
}
