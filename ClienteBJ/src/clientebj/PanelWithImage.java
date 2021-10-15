package clientebj;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class PanelWithImage extends JPanel {
	private ImageIcon image;
	
	public PanelWithImage() {
		image = new ImageIcon(this.getClass().getClassLoader().getResource("Mesa.jpg"));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		
		g.drawImage(image.getImage(), 0, 0, 640, 360, image.getImageObserver());
    }
}
