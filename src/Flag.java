import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


public class Flag {
	Rectangle2D rect;
	int team;
	int spawnX;
	int spawnY;
	boolean pickedUp;
	
	public Flag(Rectangle2D rect, int team) {
		this.rect = rect;
		this.team = team;
		this.spawnX = (int)rect.getX();
		this.spawnY = (int)rect.getY();
	}
	
	public void paintComponent(Graphics g) {
		if (!pickedUp) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.CYAN);
			g2d.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setColor(Color.BLACK);
			g2d.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
		}
	}
	
	public void score() {
		pickedUp = false;
		rect = new Rectangle2D.Double((int)spawnX, (int)spawnY, rect.getWidth(), rect.getHeight());
	}
	
	public void drop() {
		pickedUp = false;
		rect = new Rectangle2D.Double((int)spawnX, (int)spawnY, rect.getWidth(), rect.getHeight());
	}
}
