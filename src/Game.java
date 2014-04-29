import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Game {

	
	public static void main(String[] args) {
		
		final JFrame selectFrame = new JFrame("TTT");
		selectFrame.setSize(300,200);
		selectFrame.setVisible(true);
		selectFrame.setLayout(null);
		selectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel welcome = new JLabel("Welcome to Tubular Tank Tactician!");
		welcome.setBounds(40,0,500,100);
		selectFrame.add(welcome);
		JButton launchServer = new JButton("Run Server");
		launchServer.setBounds(30, 110, 100, 30);
		selectFrame.add(launchServer);
		JButton launchClient = new JButton("Run Client");
		launchClient.setBounds(150, 110, 100, 30);
		selectFrame.add(launchClient);
		
		final JFrame gameFrame = new JFrame("TTT");
		gameFrame.setSize(1020,840);
		gameFrame.setVisible(false);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JFrame serverFrame = new JFrame("TTT");
		serverFrame.setSize(300,200);
		serverFrame.setVisible(false);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		launchServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameServer server = new GameServer();
				selectFrame.setVisible(false);
				serverFrame.setVisible(true);
				try {
					server.listen();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		launchClient.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				PlayerClient client = new PlayerClient();
				gameFrame.add(client);
				gameFrame.setVisible(true);
				selectFrame.setVisible(false);
			}
		});	
		
	}
}
