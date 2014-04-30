import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class GameServer extends JComponent{

	private static final int PORT = 5500;
	 	
	 	private static final int MAX_PLAYERS = 2;
		private static final int MESSAGE_QUEUE_MAX = 500; //as a tentative start
	 	private BlockingQueue<String> messages;
	 
	 	ArrayList<PlayerThread> players;
	  	ServerSocket listener;
	  	
	 
	  	
	  	public GameServer() {
	 		players = new ArrayList<PlayerThread>();
	 		messages = new ArrayBlockingQueue<String>(MESSAGE_QUEUE_MAX);
	  	}
	 
	 	public void listen() throws IOException {
	 		System.out.println("starting listener");
	  		listener = new ServerSocket(PORT);
	  		new Thread(new Runnable() {
	  			@Override
	  			public void run() {
			  		try {
			  			System.out.println("starting loop");
			 			while (true) {
			 				try {
								new PlayerThread(listener.accept()).start();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			 				System.out.println("looping...");
			  			}
			  		} finally {
			  			try {
							listener.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			  		}
	  			}
	  		}).start();
	  	}
	 
	 	public void addPlayer(PlayerThread player) {
	 		synchronized(player){
	 			players.add(player);
	 		}
	 
	 	}

	 	@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawString("Server", 50, 50);
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
					JOptionPane.showMessageDialog(null, "A player has disconnected!", "D/C'd yo!", JOptionPane.ERROR_MESSAGE);
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
