package clientebj;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import comunes.Carta;

public class PanelJugador extends JPanel {
	//constantes de clase
	private static final int ANCHO = 206;
	private static final int ALTO = 89;
	
	//variables para control del graficado
	private ArrayList<Recuerdo> dibujoRecordar;
	private int x;
	private ImageIcon image;
	    
	public PanelJugador(String datosJugador) {
		//this.setBackground(Color.GREEN);
		dibujoRecordar = new ArrayList<Recuerdo>();
		this.setPreferredSize(new Dimension(ANCHO,ALTO));
		TitledBorder bordes;
		bordes = BorderFactory.createTitledBorder(datosJugador);
		bordes.setTitleColor(Color.WHITE);
		this.setBorder(bordes);
		this.setBackground(new Color(0,0,0,64));
//		this.setOpaque(false);
	}
	
	public void pintarCartasInicio(ArrayList<Carta> manoJugador) {
		x=5;
	    for(int i=0;i<manoJugador.size();i++) {
	    	Recuerdo recuerdo = new Recuerdo(manoJugador.get(i),x);
	    	dibujoRecordar.add(recuerdo);
	    	x+=15;
	    }			
	    repaint();
	}
	
	public void pintarLaCarta (Carta carta) {
		dibujoRecordar.add(new Recuerdo(carta,x));
		x+=27;
		repaint();
	}
	
	public void pintarCartasReinicio(ArrayList<Carta> manoJugador) {
		x=5;
		for(int i=0;i<manoJugador.size();i++) {
	    	dibujoRecordar.add(new Recuerdo(manoJugador.get(i),x));
	    	x+=27;
	    	//add(manoJugador.get(i));
	    }
		System.out.println(manoJugador.get(0).getValor());
		//System.out.println(manoJugador.get(1).getValor());
	    repaint();	  
	}
	
	public void limpiar() {
		dibujoRecordar.clear();
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Font font =new Font(Font.DIALOG,Font.BOLD,12);
		g.setFont(font);
		
		//Color de fondo
//		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				
		//pinta la mano inicial
		for(int i=0;i<dibujoRecordar.size();i++) {
			String card = dibujoRecordar.get(i).getCartaRecordar();
			int x = dibujoRecordar.get(i).getxRecordar();
			int y = 17;
			int sizeX = 55;
			int sizeY = 65;
			image = new ImageIcon(this.getClass().getClassLoader().getResource(String.format("%s.png", card)));
			image = new ImageIcon(image.getImage().getScaledInstance(sizeX, sizeY, Image.SCALE_DEFAULT));
			g.drawImage(image.getImage(), x , y, image.getImageObserver());
		}	
	}
	
	private class Recuerdo{
		private Carta cartaRecordar;
		private int xRecordar;

		public Recuerdo(Carta cartaRecordar, int xRecordar) {
			this.cartaRecordar = cartaRecordar;
			this.xRecordar = xRecordar;
		}

		public String getCartaRecordar() {
			return cartaRecordar.toString();
		}

		public int getxRecordar() {
			return xRecordar;
		}
	}

}
