package clientebj;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class VentanaEntrada extends JInternalFrame {
	
	private JLabel bienvenida, labelNombre, labelApuesta;
	private JPanel ingreso;
	private JTextField nombreJugador;
	private JTextField apuestaJugador;
	private JButton ingresar;
	private VentanaEspera ventanaEspera;
	private ClienteBlackJack cliente;
	
	private Escucha escucha;
	
	public VentanaEntrada(ClienteBlackJack cliente) {
		this.cliente=cliente;
		initInternalFrame();
		
		this.setTitle("Bienvenido a Black Jack");
		this.pack();
		this.setLocation((ClienteBlackJack.WIDTH-this.getWidth())/2, 
				         (ClienteBlackJack.HEIGHT-this.getHeight())/2);
		this.show();
	}

	private void initInternalFrame() {
		// TODO Auto-generated method stub
		escucha = new Escucha();
		this.getContentPane().setLayout(new BorderLayout());
		bienvenida = new JLabel("Registre su nombre para ingresar");
		add(bienvenida, BorderLayout.NORTH);

		ingreso = new JPanel(); 
		labelNombre = new JLabel("Nombre:"); 
		nombreJugador =	new JTextField(10); 
		labelApuesta = new JLabel("Apuesta:");
		apuestaJugador = new JTextField(10);
		ingresar = new JButton("Ingresar");
		ingresar.addActionListener(escucha);
		ingreso.add(labelNombre); ingreso.add(nombreJugador); 
		ingreso.add(labelApuesta); ingreso.add(apuestaJugador);
		ingreso.add(ingresar);
		add(ingreso,BorderLayout.CENTER);
	}
	
	private Container getContainerFrames() {
		return this.getParent();
	}
    
	private void cerrarVentanaEntrada() {
		this.dispose();
	}
	
	private boolean isNumber(String text) {
		if(text == null || text.length() == 0) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			double number = Double.parseDouble(text);
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}
	
	private class Escucha implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			//cargar Sala de Espera y cerrar Ventana Entrada
			if(nombreJugador.getText().length()==0) {
				JOptionPane.showMessageDialog(null, "Debes ingresar un nombre para identificarte!!");
			}
			if(!isNumber(apuestaJugador.getText()) || apuestaJugador.getText().length()==0){
				JOptionPane.showMessageDialog(null, "Debes ingresar un valor n�merico en la apuesta!!");
			}
			else {
				cliente.setIdYo(nombreJugador.getText());
				cliente.setApuestaYo(apuestaJugador.getText());
				ventanaEspera = new VentanaEspera(nombreJugador.getText());
				getContainerFrames().add(ventanaEspera);
				cliente.buscarServidor();
                cerrarVentanaEntrada();
			}	
		}
	}
	

}
