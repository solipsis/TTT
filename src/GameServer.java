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
	 	private int index;
	 
	 	ArrayList<PlayerThread> players;
	  	ServerSocket listener;
	  	
	 
	  	
	  	public GameServer() {
	 		players = new ArrayList<PlayerThread>();
	 		messages = new ArrayBlockingQueue<String>(MESSAGE_QUEUE_MAX);
	 		index = 0;
	  	}
	 
	 	public void listen() throws IOException {
	 		//System.out.println("starting listener");
	  		listener = new ServerSocket(PORT);
	  		// create a new thread to listen for new players
	  		new Thread(new Runnable() {
	  			@Override
	  			public void run() {
			  		try {
			  			//System.out.println("starting loop");
			 			while (true) {
			 				try {

								addPlayer(new PlayerThread(listener.accept()));
								index ++;
								if (index == 2) {
									for (PlayerThread player : players) {
										player.start();
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
			 				//System.out.println("looping...");
			  			}
			  		} finally {
			  			try {
							listener.close();
						} catch (IOException e) {
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

			//System.out.println("new player thread");
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(index);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.socket = socket;
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
					if (index == 1) {
						out = new PrintWriter(new PrintWriter(players.get(1).getSocket().getOutputStream()));
					}
					else {
						out = new PrintWriter(new PrintWriter(players.get(0).getSocket().getOutputStream()));
					}
					//out = new PrintWriter(socket.getOutputStream(), true);
					
					while(true){
						
						String input = in.readLine();
						//System.out.println("server got input: " + input);
						out.println(input);
				
						if (input == null) {
							System.out.println("null");
							return;
						}
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
