import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


public class Bullet {
	final int speed = 5;
	final int size = 20;
	private Direction direction;
	private Rectangle2D rect;
	int team;
	
	
	public Bullet( int team, Direction  direction, int x, int y) {
		this.direction = direction;
		rect = new Rectangle2D.Double(x, y, size, size);
		this.team = team;
	}
	
	public void paintComponent(Graphics2D g2d) {
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
		switch (direction) {
			case UP:
				rect.setRect(x, y-speed, w, h);
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
