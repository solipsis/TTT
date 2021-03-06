import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class Tank {
	
	private boolean isSelected;
	private boolean dead;
	private boolean hasFlag;
	final int speed = 6;
	final static int size = 40;
	private Direction direction;
	private Rectangle2D rect;
	String id = "";
	int team;
	int shootTimer;
	int deathTimer;
	int spawnX;
	int spawnY;
	Map gameMap;
	Color color;
	ArrayList<Bullet> bullets;
	
	public Tank(String id, int team, int spawnX, int spawnY, Map gameMap ) {
		bullets = new ArrayList<Bullet>();
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		dead = false;
		direction = Direction.UP;
		rect = new Rectangle2D.Double(spawnX, spawnY, size, size);
		isSelected = false;
		hasFlag = false;
		shootTimer = 15;
		this.id = id;
		this.team = team;
		this.gameMap = gameMap;
	}
	
	public Tank() {
		// TODO Auto-generated constructor stub
	}

	public boolean wallCollision() {
		for (Rectangle2D wall : gameMap.getWalls()) {
			if (rect.intersects(wall)) {
				return true;
			}
		}
		for (SpawnZone wall : gameMap.spawn) {
			if (rect.intersects(wall.rect)
					&& team != wall.team) {
				return true;
			}
		}
		return false;
	}
	
	public boolean playerCollision(ArrayList<Tank> tanks) {
		for (Tank enemy : tanks) {
			if (rect.intersects(enemy.getRect())) {
				return true;
			}
		}
		return false;
	}


	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(color);
		
		
		for(Bullet b : bullets) {
			b.paintComponent(g2d);
		}
		if (!dead) {
			g2d.setColor(color);
			// Rectangle2D doesnt have an int version. why...
			g2d.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
			g2d.setColor(Color.BLACK);
			Font textFont = new Font("Arial", Font.BOLD, 20);  
			g.setFont(textFont); 
			g2d.drawString(id, (int)rect.getX() + 10, (int)rect.getY() + 30);
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			
		
			//highlight the selected tank
			if (isSelected) {
				g2d.setColor(Color.BLUE);
				g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			}
			g2d.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());	
			drawCannon(g2d);
		}
	}
	
	public void gameTick() {
		if (!isSelected) {
			shootTimer--;
			if (shootTimer == 0) {
				shootBullet();
				shootTimer = 15;
			}
		}
		else {
			shootTimer = 15;
		}
		
		if (dead) {
			deathTimer--;
			if (deathTimer <= 0) {
				dead = false;
			}
		}
		
		for (Bullet b : bullets) {
			b.move();
		}
		
	}
	
	public void drawCannon(Graphics2D g2d) {
		g2d.setColor(Color.GREEN);
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		int x = (int)rect.getX();
		int y = (int)rect.getY();
		
		switch (direction) {
		case UP:
			g2d.drawLine(x + 10, y, x + 30, y);
			break;
		case DOWN:
			g2d.drawLine(x + 10, y+size, x + 30, y+size);
			break;
		case LEFT:
			g2d.drawLine(x, y + 10, x, y + 30);
			break;
		case RIGHT:
			g2d.drawLine(x + size, y + 10, x + size, y + 30);
			break;
		default:
			break;
	}
		//g2d.drawLine(rect.getX() + 10, rect, x2, y2);
		
	}
	
	public void shootBullet() {
		bullets.add(new Bullet(team, direction, (int)rect.getX() + 10, (int)rect.getY() + 10));
	}
	
	
	public void move() {
		if (!dead) {
			double x = rect.getX();
			double y = rect.getY();
			double w = rect.getWidth();
			double h = rect.getHeight();
			
			// a tank moves slower if it holds the flag
			int speed = this.speed;
			if (hasFlag) {
				speed *= .5;
			}
			
			switch (direction) {
				case UP:
					rect.setFrame(x, y-speed, w, h);
					break;
				case DOWN:
					rect.setRect(x, y+speed, w, h);
					break;
				case LEFT:
					rect.setRect(x-speed, y, w, h);
					break;
				case RIGHT:
					rect.setRect(x+speed, y, w, h);
					break;
				default:
					break;
			}
		}
	}
	
	public void die() {
		dead = true;
		deathTimer = 500;
		hasFlag = false;
		rect = new Rectangle2D.Double(spawnX, spawnY, size, size);
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}


	public Rectangle2D getRect() {
		return rect;
	}


	public void setRect(Rectangle2D rect) {
		this.rect = rect;
	}


	public boolean isSelected() {
		return isSelected;
	}


	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}


	public boolean isHasFlag() {
		return hasFlag;
	}


	public void setHasFlag(boolean hasFlag) {
		this.hasFlag = hasFlag;
	}


	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		this.color = color;
	}

	public double getX(){
		return rect.getX();
	}
	public double getY(){
		return rect.getY();
	}
	
	public String getId(){
		return id;
	}


	public boolean isDead() {
		return dead;
	}


	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	
	
	
	
}
