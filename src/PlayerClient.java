import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PlayerClient extends JComponent implements KeyListener {

	private static final int PORT = 5500;
	private ArrayList<Tank> tanks;
	private ArrayList<Tank> enemyTanks;
	private ArrayList<Bullet> toRemove;
	private Tank selected;
	boolean shouldMove;
	boolean isRunning; // used for gameloop
	private Timer timer;
	
	Direction direction; // the direction to move in
	Direction previousDirection;
	HashMap<Integer, Direction> hm;
	HashMap<String, Direction> dir;
	HashMap<Direction, String> dirString;
	HashMap<String, Direction> stringDir; //faster hash map for smaller packets
	Map map;
	HashMap<String, Integer> idVal;
	Socket socket;
	private int playerId;
	
	
	
	
	
	
	public void handleEnemySelection() {
		for (Tank tank : enemyTanks) {
			tank.setSelected(false);	
		}
		
	}
	
	public PlayerClient() {
		try {
			 //String ip = JOptionPane.showInputDialog( null, "Enter IP address for server:" );
			String ip = "192.168.0.7";
			socket = new Socket(ip, PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		new MessageReciever().start();
		
		stringDir = new HashMap<>();
		dirString = new HashMap<>();
		hm = new HashMap<>();
		idVal = new HashMap<>();
		dir = new HashMap<>();
		toRemove = new ArrayList<Bullet>();
		enemyTanks = new ArrayList<Tank>();
		tanks = new ArrayList<Tank>();
		addKeyListener(this);
		setFocusable(true);
		map = new Map();

		// ArrayList<Tank> temp = tanks;
		// tanks = enemyTanks;
		// enemyTanks = temp;

		setupHashMap();
		initializeTanks();
		gameLoop();

	}

	public void initializeTanks() {
		
		if (playerId == 1){
			tanks.add(new Tank("Q", 1, 435, 330));
			tanks.add(new Tank("W", 1, 435, 390));
			tanks.add(new Tank("E", 1, 435, 450));
			enemyTanks.add(new Tank("Q", 2, 525, 330));
			enemyTanks.add(new Tank("W", 2, 525, 390));
			enemyTanks.add(new Tank("E", 2, 525, 450));
		}else{
			tanks.add(new Tank("Q", 2, 525, 330));
			tanks.add(new Tank("W", 2, 525, 390));
			tanks.add(new Tank("E", 2, 525, 450));
			enemyTanks.add(new Tank("Q", 1, 435, 330));
			enemyTanks.add(new Tank("W", 1, 435, 390));
			enemyTanks.add(new Tank("E", 1, 435, 450));
		}
		for (Tank t : tanks) {
			t.setColor(Color.magenta);
		}
		for (Tank t : enemyTanks) {
			t.setColor(Color.red);
		}
		selected = tanks.get(0);
		selected.setSelected(true);
	}

	public void setupHashMap() {
		hm.put(38, Direction.UP);
		hm.put(40, Direction.DOWN);
		hm.put(37, Direction.LEFT);
		hm.put(39, Direction.RIGHT);
		idVal.put("Q", 0);
		idVal.put("W", 1);
		idVal.put("E", 2);
		dir.put("UP", Direction.UP);
		dir.put("DOWN", Direction.DOWN);
		dir.put("LEFT", Direction.LEFT);
		dir.put("RIGHT", Direction.RIGHT);
		dirString.put( Direction.UP, "U");
		dirString.put( Direction.DOWN, "D");
		dirString.put( Direction.LEFT, "L");
		dirString.put( Direction.RIGHT, "R");
		stringDir.put("U", Direction.UP);
		stringDir.put("D", Direction.DOWN);
		stringDir.put("L", Direction.LEFT);
		stringDir.put("R", Direction.RIGHT);
	}

	@Override
	public void paintComponent(Graphics g) {
		map.paintComponent(g);
		// Graphics2D g2d = (Graphics2D) g;
		for (Tank t : tanks) {
			t.paintComponent(g);
		}
		for (Tank t : enemyTanks) {
			t.paintComponent(g);
		}
	}

	public void gameLoop() {
		timer = new Timer();
		timer.schedule(new UpdateLoop(), 0, 1000 / 30);
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
				Rectangle2D oldRect = new Rectangle2D.Double(rect.getX(),
						rect.getY(), rect.getWidth(), rect.getHeight());
				selected.move();
				// if there is a collision than roll back the movement
				if (playerCollision()) {
					
					selected.setRect(oldRect);
					return;
				}
				if (wallCollision()) {
					selected.setRect(oldRect);
					return;
				}
				flagCollision();
				
				new MessageSender("M " + selected.getId() + " " + dirString.get(selected.getDirection())).start();
			}
			repaint();
		}
		for (Tank t : tanks) {
			t.gameTick();
			toRemove.clear();
			ArrayList<Bullet> toRemove = bulletCollision(t.bullets);
			t.bullets.removeAll(toRemove);
		}
		for (Tank t : enemyTanks) {
			t.gameTick();
			toRemove.clear();
			ArrayList<Bullet> toRemove = bulletCollision(t.bullets);
			t.bullets.removeAll(toRemove);
		}
		checkForHitByEnemy();
	}

	public void flagCollision() {
		for (Flag f : map.flags) {
			if (f.rect.intersects(selected.getRect())) {
				if (selected.team == f.team) {
					if (selected.isHasFlag()) {
						score();
						f.score();
						for (Flag t : map.flags){
							if (t.team != selected.team) {
								t.score();
							}
						}
						new MessageSender("SCORE ").start();
					}
				} else {
					f.pickedUp = true;
					selected.setHasFlag(true);
					new MessageSender("FLAG " + selected.getId()).start();
				}
				
			}
		}
	}

	public void score() {
		selected.setHasFlag(false);
		// add points;
	}

	public ArrayList<Bullet> bulletCollision(ArrayList<Bullet> bullets) {
		for (Bullet b : bullets) {
			for (Rectangle2D wall : map.getWalls()) {
				if (b.getRect().intersects(wall)) {
					toRemove.add(b);
				}
			}
			for (Rectangle2D spawn : map.getSpawnZones()) {
				if (b.getRect().intersects(spawn)) {
					toRemove.add(b);
				}
			}
		}
		return toRemove;
	}

	// checks to see if 2 enemy tanks collide
	public boolean playerCollision() {
		for (Tank enemy : enemyTanks) {
			if (selected.getRect().intersects(enemy.getRect())) {
				return true;
			}
		}
		return false;
	}

	// checks to see if tank collides with wall
	public boolean wallCollision() {
		for (Rectangle2D wall : map.getWalls()) {
			if (selected.getRect().intersects(wall)) {
				return true;
			}
		}
		for (SpawnZone wall : map.spawn) {
			if (selected.getRect().intersects(wall.rect)
					&& selected.team != wall.team) {
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
						if (t.isHasFlag()) {
							// take proper action if dead tank was carrying flag
							if (t.team == 1) {
								map.flags.get(1).drop();
							} else {
								map.flags.get(0).drop();
							}
						}
						
						new MessageSender("DEAD " + selected.getId()).start();
						t.die();
					}
				}
			}
		}
	}

	public void updateRendering() {
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {


		if (e.getKeyCode() == 81) { // Q
			handleSelection(0);
			new MessageSender("SELECT " + "Q").start();
		}
		if (e.getKeyCode() == 87) { // W
			handleSelection(1);
			new MessageSender("SELECT " + "W").start();
		}
		if (e.getKeyCode() == 69) { // E
			handleSelection(2);
			new MessageSender("SELECT " + "E").start();
		}

		if (selected != null) {
			if (e.getKeyCode() == 38) {
				selected.setDirection(Direction.UP);
				shouldMove = true;
			}
			if (e.getKeyCode() == 39) {
				selected.setDirection(Direction.RIGHT);
				shouldMove = true;
			}
			if (e.getKeyCode() == 40) {
				selected.setDirection(Direction.DOWN);
				shouldMove = true;
			}
			if (e.getKeyCode() == 37) {
				selected.setDirection(Direction.LEFT);
				shouldMove = true;
			}
			previousDirection = selected.getDirection();
		}
	}

	public void handleSelection(int x) {
		if (selected != null) {
			selected.setSelected(false);
		}
		if (selected == tanks.get(x)) {
			selected = null;
		} else {
			selected = tanks.get(x);
			selected.setSelected(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (hm.get(e.getKeyCode()) == previousDirection) { // allows for smooth
															// directional
															// changes
			shouldMove = false;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	class MessageSender extends Thread {
		String message;
		public MessageSender(String s) {
			message = s;
		}
		@Override
		public void run(){
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(message + " " + playerId);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "A player has disconnected!", "D/C'd yo!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class MessageReciever extends Thread {
		
		@Override
		public void run(){
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				playerId = Integer.parseInt(in.readLine());
				
				while(true){

					String s = in.readLine();
			
					String input[] = s.split(" ");
					
					//move message
					if (input[0].equals("M")) {
						enemyTanks.get(idVal.get(input[1])).setDirection(stringDir.get(input[2]));
						enemyTanks.get(idVal.get(input[1])).move();
						handleEnemySelection();
						//enemyTanks.get(idVal.get(input[1])).setDirection(dir.get(input[4]));
						enemyTanks.get(idVal.get(input[1])).setSelected(true);

					}
					
					if (input[0].equals("DEAD")) {

						Tank t = enemyTanks.get(idVal.get(input[1]));
						if (t.isHasFlag()) {
							for (Flag f : map.flags) {
								if (f.team != t.team) {
									f.drop();
							}
						}
						t.die();	
						}
	
					}
					if (input[0].equals("FLAG")) {
						for (Flag f : map.flags) {
							if (enemyTanks.get(0).team != f.team) {
								f.pickedUp = true;
								enemyTanks.get(idVal.get(input[1])).setHasFlag(true);;
							}
						}
					}
					if (input[0].equals("SCORE")) {
						for (Flag f : map.flags) {
							if (enemyTanks.get(0).team != f.team) {
								f.score();
								for (Tank t : enemyTanks) {
									t.setHasFlag(false);
								}
							}
						}
					}
					if (input[0].equals("SELECT")) {
						boolean inverse = !enemyTanks.get(idVal.get(input[1])).isSelected();
						handleEnemySelection();
						enemyTanks.get(idVal.get(input[1])).setSelected(inverse);
					}

				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "A player has disconnected!", "D/C'd yo!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private static final long serialVersionUID = 1L;
}
