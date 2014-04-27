import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


public class Tank {
	
	private boolean isSelected;
	private boolean hasFlag;
	final int speed = 5;
	final int size = 20;
	private Direction direction;
	private Rectangle2D rect;
	char id = ' ';
	int team;
	int shootTimer;
	Color color;
	
	public Tank(int x, int y, char id, int team ) {
		direction = Direction.UP;
		rect = new Rectangle2D.Double(x, y, size, size);
		isSelected = false;
		hasFlag = false;
		shootTimer = 50;
		this.id = id;
		this.team = team;
	}
	
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.RED);
		// Rectangle2D doesnt have an int version. why...
		g2d.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	}
	
	public void move() {
		
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
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
		
	
	
	
	
}
