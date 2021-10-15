package clientebj;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class VentanaApuestas extends JFrame {
	
	private double currentBetValue;
	private Color frameColor, titleColor, titleBackgroundColor;
	private volatile boolean valueEstablished, closeConnection;
	private Listener listener;
	private ImageIcon image;
	private JFrame thisGUI;
	private JLabel title, betLabel, cashIcon;
	private JPanel buttonsPanel;
	private JTextField betValue;
	private JButton ready, exit;
	private GridBagConstraints constraints;
	
	public VentanaApuestas(double betValue) {
		//Storing some variables
		this.currentBetValue = betValue;
		
		//GUI Initialization
		initGUI();
		
		//config of GUI
		this.setTitle("BlackJack");
		this.setUndecorated(false);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setEnabled(true);
		this.getContentPane().setBackground(frameColor);
		this.requestFocus();		
		this.validate();
	}
	
	private void initGUI() {
		//make listeners and constraints
		listener = new Listener();
		constraints = new GridBagConstraints();
		
		//make other variables
		titleColor = new Color(255, 255, 255);
		titleBackgroundColor = new Color(0, 0, 0);
		frameColor = new Color(0, 0, 0);
		closeConnection = false;
		thisGUI = this;
		valueEstablished = false;
		
		//Making the GUI
		
		//setting up the GUI layout
		this.setLayout(new GridBagLayout());
		
		//title
		String titleText = "";
		int titleSize = 25;
		title = new Title(titleText, titleSize, titleBackgroundColor, titleColor, null, false);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.fill = GridBagConstraints.BOTH;
		this.add(title, constraints);
		
		//textField
		betLabel = new JLabel("valor de Apuesta: ");
		betLabel.setForeground(titleColor);
		betLabel.setOpaque(false);
		betValue = new JTextField(10);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		this.add(betLabel, constraints);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(betValue, constraints);
		int sizeX = 50;
		int sizeY = 50;
		image = new ImageIcon(this.getClass().getClassLoader().getResource("cash.png"));
		image = new ImageIcon(image.getImage().getScaledInstance(sizeX, sizeY, Image.SCALE_DEFAULT));
		cashIcon = new JLabel(image);
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.BOTH;
		this.add(cashIcon, constraints);
		
		//Buttons
		buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		ready = new JButton("Ingresar");
		ready.addActionListener(listener);
		constraints.gridx = 0;
		constraints.gridy = 0;
		buttonsPanel.add(ready, constraints);
		exit = new JButton("Salir");
		exit.addActionListener(listener);
		constraints.gridx = 1;
		constraints.gridy = 0;
		buttonsPanel.add(exit, constraints);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 3;
		this.add(buttonsPanel, constraints);
		
	}
	
	private void storeNewBetValue(double bet) {
		this.valueEstablished = true;
		this.currentBetValue = bet;
	}
	
	public boolean apuestaEstablecida() {
		return this.valueEstablished;
	}
	
	private boolean isNumber(String text) {
		if(text.length() == 0) {
			return false;
		}
		try{
			@SuppressWarnings("unused")
			double d = Double.parseDouble(text);
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public double getValorApuesta() {
		return this.currentBetValue;
	}
	
	public boolean seCerroConexion() {
		return this.closeConnection;
	}
	
	private class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == ready) {
				if(betValue.getText().length() == 0 || !isNumber(betValue.getText())) {
					String title = "Error!";
					String message = "Por favor ingresa un valor númerico!";
					JOptionPane.showMessageDialog(thisGUI, message, title, JOptionPane.ERROR_MESSAGE);
				}
				else {
					double value = Double.parseDouble(betValue.getText());
					storeNewBetValue(value);
				}
			}
			
			if(e.getSource() == exit) {
				String message = "Gracias por participar!!";
				String title = "Hasta pronto, " + message;
			    image = new ImageIcon(this.getClass().getClassLoader().getResource("ending.png"));
			    JOptionPane.showMessageDialog(thisGUI, null, title, JOptionPane.INFORMATION_MESSAGE, image);
			    closeConnection = true;
			}
			
		}
		
	}
	
}
