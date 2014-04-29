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
	 		players = new ArrayList<PlayerThread>();
	 		messages = new ArrayBlockingQueue<String>(MESSAGE_QUEUE_MAX);
	  	}
	 
	 	public void listen() throws IOException {
	  		listener = new ServerSocket(PORT);
	  		try {
	 			while (true) {
	 				new PlayerThread(listener.accept()).start();
	  			}
	  		} finally {
	  			listener.close();
	  		}
	  	}
	 
	 	public void addPlayer(PlayerThread player) {
	 		synchronized(player){
	 			players.add(player);
	 		}
	 
	 	}

	class PlayerThread extends Thread {

		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;

		public PlayerThread(Socket socket) {
			this.socket = socket;
		}
		
		public void sendPlayerId(){
			//send the player's id based on the threads position in the players arraylist
		}
		
		public void sendToAll(String message) throws IOException{
			for (PlayerThread player : players){
				if (player.getSocket() != socket){
					out = new PrintWriter(player.getSocket().getOutputStream());
					out.write(message);
					out.println();
				}
			}
		}
		
		public Socket getSocket(){
			return socket;
		}
		
		class MessageBuilder extends Thread {
			@Override
			public void run(){
				try {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while(true){
						messages.add(in.readLine());
						System.out.println(in.readLine());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void run() {
			new MessageBuilder().start();

//			try {
//				
//				while (true){ //need an exit condition
//					sendToAll(messages.take());
//				}
//				
//				
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
}
