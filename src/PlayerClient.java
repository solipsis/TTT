import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;




public class PlayerClient extends JComponent implements KeyListener {
	
	

	private ArrayList<Tank> tanks;
	private ArrayList<Tank> enemyTanks;
	private Tank selected;
	boolean shouldMove;
	boolean isRunning; // used for gameloop
	private Timer timer;
	Direction direction; // the direction to move in
	Direction previousDirection;
	HashMap<Integer, Direction> hm;
	
	
	public PlayerClient() {
		hm = new HashMap<>();
		enemyTanks = new ArrayList<Tank>();
		tanks = new ArrayList<Tank>();
		tanks.add(new Tank(50,50,'Q',1));
		tanks.add(new Tank(100,100,'W',1));
		tanks.add(new Tank(200,200,'E',1));
		selected = tanks.get(0);
		selected.setSelected(true);
		enemyTanks.add(new Tank(300,300,'Q', 2));
		addKeyListener(this);
		setFocusable(true);
		setupHashMap();
		//shouldMove = true;
		gameLoop();
		
	}
	
	public void setupHashMap() {
		hm.put(38, Direction.UP);
		hm.put(40, Direction.DOWN);
		hm.put(37, Direction.LEFT);
		hm.put(39, Direction.RIGHT);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		//Graphics2D g2d = (Graphics2D) g;
		for (Tank t : tanks) {
			t.paintComponent(g);
		}
		for (Tank t : enemyTanks) {
			t.paintComponent(g);
		}
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
			if (selected != null) {
				Rectangle2D rect = selected.getRect();
				Rectangle2D oldRect = new Rectangle2D.Double(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
				selected.move();
				// if there is a collision than roll back the movement
				if (playerCollision()) {
					System.out.println("collision detected");
					selected.setRect(oldRect);
				}
			}
			repaint();
		}
		for (Tank t : tanks) {
			t.gameTick();
		}
		for (Tank t : enemyTanks) {
			t.gameTick();
		}
		checkForHitByEnemy();
	}
	
	// checks to see if 2 tanks collide
	public boolean playerCollision() {
		for (Tank enemy : enemyTanks) {
			if (selected.getRect().intersects(enemy.getRect())){
				return true;
			}
		}
		return false;
	}
	
	
	public void checkForHitByEnemy() {
		for (Tank t : tanks) {
			Rectangle2D rect = t.getRect();
			for (Tank e : enemyTanks) {
				for (Bullet b : e.bullets) {
					if (rect.intersects(b.getRect())) {
						t.die();
					}
				}
			}
		}
	}
	
	public void updateRendering() {
		repaint();
	}
	
	
	
	
	

	//TODO: change so that key pressed simply sets a variable.
	//every time the game loop is called and that variable is true, keep moving.
	// key released sets that to false
	//repaint should be moved to game loop
	
	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println(e.getKeyCode());
		
		if (e.getKeyCode() == 81) { // Q
			selected.setSelected(false);
			if (selected == tanks.get(0)) {
				selected = null;
			}
			else {
				selected = tanks.get(0);
				selected.setSelected(true);
			}
		}
		
		if (e.getKeyCode() == 87) { // W
			selected.setSelected(false);
			if (selected == tanks.get(1)) {
				selected = null;
			}
			else {
				selected = tanks.get(1);
				selected.setSelected(true);
			}
		}
		if (e.getKeyCode() == 69) { // E
			selected.setSelected(false);
			if (selected == tanks.get(2)) {
				selected = null;
			}
			else {
				selected = tanks.get(2);
				selected.setSelected(true);
			}
		}
		if (selected != null) {
			if(e.getKeyCode() == 38) {
				selected.setDirection(Direction.UP);
				shouldMove = true;				
			}
			if(e.getKeyCode() == 39) {
				selected.setDirection(Direction.RIGHT);
				shouldMove = true;				
			}
			if(e.getKeyCode() == 40) {
				selected.setDirection(Direction.DOWN);
				shouldMove = true;				
			}
			if(e.getKeyCode() == 37) {
				selected.setDirection(Direction.LEFT);
				shouldMove = true;
			}
			previousDirection = selected.getDirection();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (hm.get(e.getKeyCode()) == previousDirection) { //allows for smooth directional changes
			shouldMove = false;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	private static final long serialVersionUID = 1L;
}
