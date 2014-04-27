import javax.swing.JFrame;


public class Game {

	
	public static void main(String[] args) {
		JFrame frame = new JFrame("TTT");
		frame.setSize(800,800);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		PlayerClient client = new PlayerClient();
		frame.add(client);
		
	}

}
