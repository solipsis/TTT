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
	Map map;
	Socket socket;
	private int playerId;
	//BufferedReader in;
	//PrintWriter out;
	
	
	
	class MessageSender extends Thread {
		String message;
		public MessageSender(String s) {
			//System.out.println("created message sender");
			message = s;
		}
		@Override
		public void run(){
			//System.out.println("message sender started");
			try {
				//Socket socket = new Socket(InetAddress.getLocalHost()
				//		.getHostAddress(), PORT);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				
				out.println(message);
				//System.out.println("client sent message : " + message);	
					//if (input == null) {
					//	System.out.println("null");
					//	return;
					//}
				//}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "A player has disconnected!", "D/C'd yo!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class MessageReciever extends Thread {
		
		@Override
		public void run(){
			System.out.println("message Reciever started");
			try {
				//Socket socket = new Socket(InetAddress.getLocalHost()
				//		.getHostAddress(), PORT);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				playerId = Integer.parseInt(in.readLine());
				System.out.println("my id is " + playerId);
				
				while(true){
					String input = in.readLine();
					System.out.println("client got input");
					System.out.println(input);
					
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "A player has disconnected!", "D/C'd yo!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public PlayerClient() {
		try {
			socket = new Socket(InetAddress.getLocalHost().getHostAddress(), PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("creating message reciever");
		new MessageReciever().start();
		
		
		hm = new HashMap<>();
		toRemove = new ArrayList<Bullet>();
		enemyTanks = new ArrayList<Tank>();
		tanks = new ArrayList<Tank>();
		initializeTanks();

		addKeyListener(this);
		setFocusable(true);
		map = new Map();

		// ArrayList<Tank> temp = tanks;
		// tanks = enemyTanks;
		// enemyTanks = temp;

		setupHashMap();

		gameLoop();

	}

	public void initializeTanks() {
		tanks.add(new Tank("Q", 1, 435, 330));
		tanks.add(new Tank("W", 1, 435, 390));
		tanks.add(new Tank("E", 1, 435, 450));
		enemyTanks.add(new Tank("Q", 2, 800, 600));
		enemyTanks.add(new Tank("W", 2, 525, 390));
		enemyTanks.add(new Tank("E", 2, 525, 450));
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
				Rectangle2D oldRect = new Rectangle2D.Double(rect.getX(),
						rect.getY(), rect.getWidth(), rect.getHeight());
				selected.move();
				// if there is a collision than roll back the movement
				if (playerCollision()) {
					System.out.println("collision detected");
					selected.setRect(oldRect);
				}
				if (wallCollision()) {
					selected.setRect(oldRect);
				}
				flagCollision();
				//System.out.println("make ms");
				new MessageSender("move").start();
				//out.write("fat");
				//out.println("a");
				//out.println(selected.getId() + " " + selected.getX() + " " + selected.getY());
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
						map.flags.get(0).score();
						map.flags.get(1).score();
					}
				} else {
					f.pickedUp = true;
					selected.setHasFlag(true);
				}
				new MessageSender("flag").start();
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
		// System.out.println(e.getKeyCode());

		if (e.getKeyCode() == 81) { // Q
			handleSelection(0);
		}
		if (e.getKeyCode() == 87) { // W
			handleSelection(1);
		}
		if (e.getKeyCode() == 69) { // E
			handleSelection(2);
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

	private static final long serialVersionUID = 1L;
}
