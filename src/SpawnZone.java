import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


/**
 * SpawnZone class that handles the drawing of the spawn zones where the tanks start
 *
 */
public class SpawnZone {
	Rectangle2D rect;
	int team;
	
	public SpawnZone(Rectangle2D rect, int team) {
		this.rect = rect;
		this.team = team;
	}
	
	/**
	 * Draws the spawn zones based on Rectangle2D objects
	 * 
	 * @param g
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.YELLOW);
		g2d.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(Color.BLACK);
		g2d.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	}
}
