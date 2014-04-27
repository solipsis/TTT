import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GameServer {

	private static final int PORT = 5500;

	private static final int MESSAGE_QUEUE_MAX = 500; //as a tentative start
	private BlockingQueue<String> messages;

	ArrayList<PlayerThread> players;

	ServerSocket listener;
	

	public GameServer() {


	public void addPlayer(PlayerThread player) {
		synchronized(player){
			players.add(player);
		}

	}

	class PlayerThread extends Thread {

		private Socket socket;
		private PrintWriter out;

		public PlayerThread(Socket socket) {
			this.socket = socket;
		}
		
		public void sendToAll(String message){
			for (PlayerThread player : players){
				out.write(message);
			}
		}

		@Override
		public void run() {

			try {
				BufferedReader in;
				out = new PrintWriter(socket.getOutputStream());
				while (true){ //need an exit condition
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					sendToAll(in.readLine());
				}
				
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
