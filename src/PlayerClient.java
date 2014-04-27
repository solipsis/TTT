import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;




public class PlayerClient extends JComponent implements KeyListener {
	
	

	private Tank tank;
	boolean shouldMove;
	boolean isRunning; // used for gameloop
	private Timer timer;
	
	public PlayerClient() {
		tank = new Tank(50,50,'1',1);
		addKeyListener(this);
		setFocusable(true);
		//shouldMove = true;
		gameLoop();
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		//Graphics2D g2d = (Graphics2D) g;
		tank.paintComponent(g);
	}
	
	
	public void gameLoop() {
		timer = new Timer();
		timer.schedule(new UpdateLoop(), 0, 1000 / 60);
	}
	
	private class UpdateLoop extends TimerTask {
		@Override
		public void run() {
			updateLogic();
			updateRendering();	
		}
		
	}
	
	public void updateLogic() {
		if (shouldMove) {
			tank.move();
			repaint();
		}
	}
	
	public void updateRendering() {
		
	}
	
	
	
	
	

	//TODO: change so that key pressed simply sets a variable.
	//every time the game loop is called and that variable is true, keep moving.
	// key released sets that to false
	//repaint should be moved to game loop
	
	@Override
	public void keyPressed(KeyEvent e) {
		//if (shouldMove) {
			if(e.getKeyCode() == 38) {
				tank.setDirection(Direction.UP);
				shouldMove = true;
				//repaint();
			}
			if(e.getKeyCode() == 39) {
				tank.setDirection(Direction.RIGHT);
				shouldMove = true;
				//tank.move();
			//	repaint();
			}
			if(e.getKeyCode() == 40) {
				tank.setDirection(Direction.DOWN);
				shouldMove = true;
				//tank.move();
			//	repaint();
			}
			if(e.getKeyCode() == 37) {
				tank.setDirection(Direction.LEFT);
				shouldMove = true;
				//tank.move();
				//repaint();
			}
		//}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		shouldMove = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private static final long serialVersionUID = 1L;
}
