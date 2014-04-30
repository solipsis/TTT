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
			  			System.out.println("Please wait for both players to connect");
			 			while (true) {
			 				try {

								addPlayer(new PlayerThread(listener.accept(), index));
								index ++;
								if (index == 2) {
									System.out.println("starting threads");
									for (PlayerThread player : players) {
										player.start();
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}	
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
		private int x;
		
		public PrintWriter getOut() {
			return out;
		}

		public PlayerThread(Socket socket, int index) {
			x = index;
			
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out.println(index);
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.socket = socket;
		}
		
		
		public Socket getSocket(){
			return socket;
		}
		
		public BufferedReader getIn(){
			return in;
		}
		
		class MessageBuilder extends Thread {
			@Override
			public void run(){
				
				try {
					
					System.out.println("both players have connected");
					if (x == 0) {
						//System.out.println("bluh");
						in = new BufferedReader(new InputStreamReader(players.get(0).socket.getInputStream()));
						out = new PrintWriter(players.get(1).socket.getOutputStream(), true);
					}
					else {
						//System.out.println("bluh bluh");
						in = new BufferedReader(new InputStreamReader(players.get(1).socket.getInputStream()));
						out = new PrintWriter(players.get(0).socket.getOutputStream(), true);
					}
					//out = new PrintWriter(socket.getOutputStream(), true);
					
					while(true){
						
						String input = in.readLine();
						//System.out.println("server got input: " + input);
						out.println(input);
				
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "A player has disconnected!", "D/C'd yo!", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		@Override
		public void run() {
			new MessageBuilder().start();
		}
	}
}
