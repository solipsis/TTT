import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


/**
 * Bullet class that handles the drawing and movements of the bullets fired by the tanks
 * 
 */
public class Bullet {
	final int speed = 6;
	final int size = 20;
	private Direction direction;
	private Rectangle2D rect;
	int team;
	
	
	public Bullet( int team, Direction  direction, int x, int y) {
		this.direction = direction;
		rect = new Rectangle2D.Double(x, y, size, size);
		this.team = team;
	}
	
	/**
	 * Draws the bullet based on a Rectangle2D object
	 * 
	 * @param g2d
	 */
	public void paintComponent(Graphics2D g2d) {
		g2d.setColor(Color.ORANGE);
		g2d.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(Color.BLACK);
		g2d.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	}
	
	/**
	 * Changes the rectangle's x and y positions according to the bullet's speed and direction it is traveling
	 * 
	 */
	public void move() {
		double x = rect.getX();
		double y = rect.getY();
		double w = rect.getWidth();
		double h = rect.getHeight();
		
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

	public Rectangle2D getRect() {
		return rect;
	}

	public void setRect(Rectangle2D rect) {
		this.rect = rect;
	}
}
