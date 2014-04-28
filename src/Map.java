import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class Map {
	ArrayList<Rectangle2D> walls = new ArrayList<Rectangle2D>();
	ArrayList<Rectangle2D> flagSpawns = new ArrayList<Rectangle2D>();
	ArrayList<Flag> flags = new ArrayList<Flag>();
	ArrayList<Rectangle2D> spawnZones = new ArrayList<Rectangle2D>();
	ArrayList<SpawnZone> spawn = new ArrayList<SpawnZone>();
	ArrayList<Rectangle2D> safeZones = new ArrayList<Rectangle2D>();
	
	public Map() {
		addWalls();
		
		addFlags();
		addSpawns();
		//addSafeZones();
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		drawGrid(g2d);
		//drawRects(g2d, spawnZones, Color.YELLOW);
		for (Flag f : flags) {
			f.paintComponent(g2d);
		}
		for (SpawnZone s : spawn) {
			s.paintComponent(g2d);
		}
		drawRects(g2d, walls, Color.GREEN);
		//drawRects(g2d, flagSpawns, Color.CYAN);
		
	}
	
	public void drawRects(Graphics2D g2d, ArrayList<Rectangle2D> rects, Color color) {
		for (Rectangle2D rect : rects) {
			g2d.setColor(color);
			g2d.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setColor(Color.BLACK);
			g2d.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
		}
	}
	
	public void drawGrid(Graphics2D g2d) {
		g2d.drawLine(500, 0, 500, 800);
	}
	
	public void addFlags() {
		mirrorRect(100, 400, 30, 30,flagSpawns);
		flags.add(new Flag(flagSpawns.get(0), 1));
		flags.add(new Flag(flagSpawns.get(1), 2));
	}
	
	public void addSpawns() {
		mirrorRect(430, 320, 70, 200, spawnZones);
		spawn.add(new SpawnZone(spawnZones.get(0), 1));
		spawn.add(new SpawnZone(spawnZones.get(1), 2));
		
	}
	
	public void addWalls() {
		walls.add(new Rectangle2D.Double(0, 100, 1000, 40)); //top bar
		walls.add(new Rectangle2D.Double(0, 720, 1000, 40)); //bottom bar
		mirrorRect(0, 100, 40, 700, walls);
		mirrorRect(40, 140, 150, 60, walls); //top corners
		mirrorRect(300, 200, 120, 15, walls); //top mid
		mirrorRect(490, 225, 10, 60, walls); // mid slit
		mirrorRect(480, 320, 20, 200, walls); // mid divider 
		mirrorRect(400, 600, 100, 10, walls); // lower mid line 
		mirrorRect(460, 680, 40, 40, walls);  //lower mid block
		mirrorRect(200, 675, 40, 45, walls); // outer lower block
		mirrorRect(300, 620, 40, 40, walls); // lower upper block
		mirrorRect(120, 540, 50, 50, walls); // lower outer big block
		mirrorRect(225, 515, 50, 8, walls); // lower L
		mirrorRect(225, 460, 8, 55, walls); // upper L
		mirrorRect(300, 380, 50, 10, walls); //
		mirrorRect(360, 500, 30, 30, walls); //
		mirrorRect(155, 290, 50, 50, walls); 
	}
	
	// creates 2 rectangles mirrored over the middle
	public void mirrorRect(int x, int y, int w, int h, ArrayList<Rectangle2D> list) {
		list.add(new Rectangle2D.Double(x,y,w,h));
		list.add(new Rectangle2D.Double(1000-w-x, y, w, h));
	}

	public ArrayList<Rectangle2D> getWalls() {
		return walls;
	}

	public void setWalls(ArrayList<Rectangle2D> walls) {
		this.walls = walls;
	}

	public ArrayList<Rectangle2D> getFlagSpawns() {
		return flagSpawns;
	}

	public void setFlagSpawns(ArrayList<Rectangle2D> flagSpawns) {
		this.flagSpawns = flagSpawns;
	}

	public ArrayList<Rectangle2D> getSpawnZones() {
		return spawnZones;
	}

	public void setSpawnZones(ArrayList<Rectangle2D> spawnZones) {
		this.spawnZones = spawnZones;
	}

	public ArrayList<Rectangle2D> getSafeZones() {
		return safeZones;
	}

	public void setSafeZones(ArrayList<Rectangle2D> safeZones) {
		this.safeZones = safeZones;
	}
	
}
