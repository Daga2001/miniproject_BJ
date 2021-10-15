package clientebj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import comunes.DatosBlackJack;

public class VentanaSalaJuego extends JInternalFrame {
	    
		private PanelJugador dealer, yo, jugador2, jugador3;
		private JTextArea areaMensajes;
		private JButton pedir, plantar;
		private JPanel mensajes, panelYo, panelBotones, yoFull, panelDealer, panelJugador2, panelJugador3;
		private VentanaApuestas ventanaApuestas;
		private ImageIcon image;
		
		private String yoId, jugador2Id, jugador3Id;
		private volatile boolean cerrarConexion, modificarApuesta, pantallaApuestasDesplegada, finDeRonda;
		private double apuestaYo, apuestaOtroJugador, apuestaOtroJugador2;
		private Escucha escucha;
		
		public VentanaSalaJuego(String yoId, String jugador2Id, String jugador3Id, double apuestaYo, double apuestaOtroJugador, double apuestaOtroJugador2) {
			this.yoId = yoId;
			this.jugador2Id = jugador2Id;
			this.jugador3Id = jugador3Id;
			this.apuestaYo = apuestaYo;
			this.apuestaOtroJugador = apuestaOtroJugador;
			this.apuestaOtroJugador2 = apuestaOtroJugador2;
			this.cerrarConexion = false;
			this.modificarApuesta = false;
			this.pantallaApuestasDesplegada = false;
			this.finDeRonda = false;
			//this.datosRecibidos=datosRecibidos;
						
			initGUI();
			
			//default window settings
			this.setTitle("Sala de juego BlackJack - Jugador: "+yoId);
			this.pack();
//			this.setLocation((ClienteBlackJack.WIDTH-this.getWidth())/2, 
//			         (ClienteBlackJack.HEIGHT-this.getHeight())/2);
			this.setLocation(((ClienteBlackJack.WIDTH/2)-(this.getWidth()/2))/2,((ClienteBlackJack.HEIGHT/2)-(this.getHeight()/2))/2);
			this.setResizable(false);
			this.show();
			this.setOpaque(false);
		}

		private void initGUI() {
			// TODO Auto-generated method stub
			//set up JFrame Container y Layout
	        
			//Create Listeners objects
			escucha = new Escucha();
			//Create Control objects
			
			PanelWithImage panel = new PanelWithImage();
			panel.setLayout(new BorderLayout());
			
			//Set up JComponents
			panelDealer = new JPanel();
			dealer = new PanelJugador("Dealer");
			panelDealer.setOpaque(false);
			panelDealer.add(dealer);
			panel.add(panelDealer,BorderLayout.NORTH);		
			
			panelJugador2 = new JPanel();
			jugador2= new PanelJugador(String.format("%s - apuesta: %s", jugador2Id, apuestaOtroJugador));	
			panelJugador2.setOpaque(false);
			panelJugador2.add(jugador2);
			panel.add(panelJugador2,BorderLayout.EAST);
			
			panelJugador3 = new JPanel();
			jugador3= new PanelJugador(String.format("%s - apuesta: %s", jugador3Id, apuestaOtroJugador2));	
			panelJugador3.setOpaque(false);
			panelJugador3.add(jugador3);
			panel.add(panelJugador3,BorderLayout.SOUTH);
			
			mensajes = new JPanel();
			mensajes.setBackground(new Color(255,255,255,60));
			areaMensajes = new JTextArea(8,18);
//			areaMensajes.setDisabledTextColor(Color.WHITE);
			JScrollPane scroll = new JScrollPane(areaMensajes);	
			Border blackline;
			blackline = BorderFactory.createLineBorder(Color.black);
			TitledBorder bordes;
			bordes = BorderFactory.createTitledBorder(blackline, "Area de Mensajes");
	        bordes.setTitleJustification(TitledBorder.CENTER);
//	        bordes.setTitleColor(Color.WHITE);
			scroll.setBorder(bordes);
			areaMensajes.setOpaque(false);
			areaMensajes.setBackground(new Color(0, 0, 0, 0));
			areaMensajes.setEditable(false);

			scroll.getViewport().setOpaque(false);
			scroll.setOpaque(false);
			mensajes.add(scroll);
			panel.add(mensajes,BorderLayout.CENTER);
			
			panelYo = new JPanel();
			panelYo.setLayout(new BorderLayout());
			yo = new PanelJugador(String.format("%s - apuesta: %s", yoId, apuestaYo));
			panelYo.setOpaque(false);
			panelYo.add(yo);
				
			pedir = new JButton("Carta");
			pedir.setEnabled(false);
			pedir.addActionListener(escucha);
			plantar = new JButton("Plantar");
			plantar.setEnabled(false);
			plantar.addActionListener(escucha);
			panelBotones = new JPanel();
			panelBotones.setOpaque(false);
			panelBotones.add(pedir);
			panelBotones.add(plantar);
			
			yoFull = new JPanel();
			yoFull.setPreferredSize(new Dimension(206,100));
			yoFull.setOpaque(false);
			yoFull.add(panelYo);
			yoFull.add(panelBotones);
			panel.add(yoFull,BorderLayout.WEST);	
			
			add(panel);
		}
		
		public void activarBotones(boolean turno) {
			pedir.setEnabled(turno);
			plantar.setEnabled(turno);
		}
		
		public boolean getModificarApuesta() {
			return this.modificarApuesta;
		}
		
		public void setModificarApuesta(boolean bool) {
			this.modificarApuesta = bool;
		}
		
		public boolean getFinDeRonda() {
			return this.finDeRonda;
		}
		
		public void setFinDeRonda(boolean bool) {
			this.finDeRonda = bool;
		}
		
		public void actualizarDatosOtrosJugadores(String jugador2Id, double apuestaOtroJugador, String jugador3Id, double apuestaOtroJugador2) {
			this.jugador2Id = jugador2Id;
			this.apuestaOtroJugador = apuestaOtroJugador;
			this.jugador3Id = jugador3Id;
			this.apuestaOtroJugador2 = apuestaOtroJugador2;
		}
		
		public void wait(int miliseconds) {
			try {
				System.out.println(String.format("jugador %s se ha dormido", yoId));
				Thread.sleep(miliseconds);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void pintarCartasInicio(DatosBlackJack datosRecibidos) {
			if(datosRecibidos.getIdJugadores()[0].equals(yoId)) {
				if (datosRecibidos.getIdJugadores()[1].equals(jugador2Id)) {
					yo.pintarCartasInicio(datosRecibidos.getManoJugador1());
					jugador2.pintarCartasInicio(datosRecibidos.getManoJugador2());
					jugador3.pintarCartasInicio(datosRecibidos.getManoJugador3());
				}
				else {
					yo.pintarCartasInicio(datosRecibidos.getManoJugador1());
					jugador2.pintarCartasInicio(datosRecibidos.getManoJugador3());
					jugador3.pintarCartasInicio(datosRecibidos.getManoJugador2());
				}
			}
			else if(datosRecibidos.getIdJugadores()[0].equals(jugador2Id)) {
				if (datosRecibidos.getIdJugadores()[1].equals(yoId)) {
					yo.pintarCartasInicio(datosRecibidos.getManoJugador2());
					jugador2.pintarCartasInicio(datosRecibidos.getManoJugador1());
					jugador3.pintarCartasInicio(datosRecibidos.getManoJugador3());
				}
				else {
					yo.pintarCartasInicio(datosRecibidos.getManoJugador3());
					jugador2.pintarCartasInicio(datosRecibidos.getManoJugador1());
					jugador3.pintarCartasInicio(datosRecibidos.getManoJugador2());
				}
			}
			else {
				if(datosRecibidos.getIdJugadores()[0].equals(jugador3Id)) {
					if (datosRecibidos.getIdJugadores()[1].equals(yoId)) {
						yo.pintarCartasInicio(datosRecibidos.getManoJugador2());
						jugador2.pintarCartasInicio(datosRecibidos.getManoJugador3());
						jugador3.pintarCartasInicio(datosRecibidos.getManoJugador1());
					}
					else {
						yo.pintarCartasInicio(datosRecibidos.getManoJugador3());
						jugador2.pintarCartasInicio(datosRecibidos.getManoJugador2());
						jugador3.pintarCartasInicio(datosRecibidos.getManoJugador1());
					}
				}
			}
			dealer.pintarCartasInicio(datosRecibidos.getManoDealer());
			
			areaMensajes.append(datosRecibidos.getMensaje()+"\n");
		}
		
		public void pintarCartasReinicio(DatosBlackJack datosRecibidos) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(datosRecibidos.getIdJugadores()[0].equals(yoId)) {
						yo.pintarCartasReinicio(datosRecibidos.getManoJugador1());
						jugador2.pintarCartasReinicio(datosRecibidos.getManoJugador2());
					}else {
						yo.pintarCartasReinicio(datosRecibidos.getManoJugador2());
						jugador2.pintarCartasReinicio(datosRecibidos.getManoJugador1());
					}
					dealer.pintarCartasReinicio(datosRecibidos.getManoDealer());
					
				}
			});
			
		}
		
		public void limpiar() {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					yo.limpiar();
					jugador2.limpiar();
					dealer.limpiar();
				}
				
			});
		}
		
		public void pintarTurno(DatosBlackJack datosRecibidos) {
			areaMensajes.append(datosRecibidos.getMensaje()+"\n");	
			ClienteBlackJack cliente = (ClienteBlackJack)this.getTopLevelAncestor();
			actualizarPanelesJugadores();
			
			if(datosRecibidos.getReiniciar() == true) {
				restart();
				activarBotones(false);
				areaMensajes.setText(datosRecibidos.getMensaje()+"\n");	
				limpiar();
				pintarCartasReinicio(datosRecibidos);	
			}
			
			if(datosRecibidos.getJugador().contentEquals(yoId)){
				if(datosRecibidos.getJugadorEstado().equals("iniciar")) {
					activarBotones(true);
				}else {
					if(datosRecibidos.getJugadorEstado().equals("plantó") ){
						cliente.setTurno(false);
					}else {
						yo.pintarLaCarta(datosRecibidos.getCarta());
						if(datosRecibidos.getJugadorEstado().equals("voló")) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									activarBotones(false);
									cliente.setTurno(false);
								}});			
						    }
						}
					} 
			 }
			else if (datosRecibidos.getJugador().contentEquals(jugador2Id)){
				//mensaje para PanelJuego jugador2
				if(datosRecibidos.getJugadorEstado().equals("sigue")||
				   datosRecibidos.getJugadorEstado().equals("voló")) {
					jugador2.pintarLaCarta(datosRecibidos.getCarta());
				}
			}
			else {//movidas de los otros jugadores
					if(datosRecibidos.getJugador().equals(jugador3Id)) {
						//mensaje para PanelJuego jugador2
						if(datosRecibidos.getJugadorEstado().equals("sigue")||
						   datosRecibidos.getJugadorEstado().equals("voló")) {
							jugador3.pintarLaCarta(datosRecibidos.getCarta());
						}
					}else {
						//mensaje para PanelJuego dealer
						if(datosRecibidos.getJugadorEstado().equals("sigue") ||
						   datosRecibidos.getJugadorEstado().equals("voló")	||
						   datosRecibidos.getJugadorEstado().equals("plantó")) {
							dealer.pintarLaCarta(datosRecibidos.getCarta());
							checkIfRoundIsOver(datosRecibidos.getJugadorEstado(), datosRecibidos, cliente);
						}
					}
				}			 	
		}
		
		public void restart() {
			this.modificarApuesta = false;
			this.pantallaApuestasDesplegada = false;
			this.finDeRonda = false;
		}
	   
	   private void enviarDatos(String mensaje) {
			// TODO Auto-generated method stub
		  ClienteBlackJack cliente = (ClienteBlackJack)this.getTopLevelAncestor();
		  cliente.enviarMensajeServidor(mensaje);
		}
		   
	   public void actualizarPanelesJugadores() {
  	    	//panel jugador 1
		   	TitledBorder bordes;
		   	bordes = BorderFactory.createTitledBorder(String.format("%s - apuesta: %s", yoId, apuestaYo));
		   	bordes.setTitleColor(Color.WHITE);
		   	yo.setBorder(bordes);
		   	//panel jugador 2
		   	bordes = BorderFactory.createTitledBorder(String.format("%s - apuesta: %s", jugador2Id, apuestaOtroJugador));
		   	bordes.setTitleColor(Color.WHITE);
		   	jugador2.setBorder(bordes);
		   	//panel jugador 3
		   	bordes = BorderFactory.createTitledBorder(String.format("%s - apuesta: %s", jugador3Id, apuestaOtroJugador2));
		   	bordes.setTitleColor(Color.WHITE);
		   	jugador3.setBorder(bordes);
		   	this.repaint();
		   	this.validate();
	   }
	   
	   private void asignarNuevaApuesta(double number) {
		   this.apuestaYo = number;
	   }
	   
	   private double calcularValorApuesta(double number, DatosBlackJack datosRecibidos, ClienteBlackJack cliente) {
		   	System.out.println("Hi poter!");
		   	asignarNuevaApuesta(number);
		   	System.out.println(String.format("jugador %s solicita reiniciar la ronda", yoId));
		   	cliente.enviarMensajeServidor("reiniciar ronda");
		   	System.out.println(String.format("valor nueva apuesta: %s", apuestaYo));
		   	cliente.enviarMensajeServidor(String.valueOf(apuestaYo));
		   	cliente.enviarMensajeServidor(yoId);
		   	modificarApuesta = true;
		   	cliente.reajustarDatosJugador(datosRecibidos);
		   	actualizarPanelesJugadores();
//		   	cliente.enviarMensajeServidor("cambios ajustados");
		   	System.out.println("los cambios del jugador: "+yoId+" se han realizado con éxito");
		   	return this.apuestaYo;
	   }
	   
	   private int determinarGanancias(ClienteBlackJack cliente) {
		   cliente.enviarMensajeServidor("calcular apuesta");
		   cliente.enviarMensajeServidor(yoId);
		   return cliente.calcularGanancias();
	   }
	   
	   private void showFinalBet(ClienteBlackJack cliente) {
		   int valor = determinarGanancias(cliente);
		   String title = "Valor final!";
		   String message = "";
		   if(valor > 0) {
			   message = String.format("El dealer te pagará %s USD!", valor);
		   }
		   else {
			   message = String.format("Has perdido la apuesta, ahora tienes %s USD!", valor);
		   }
		   JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	   }
	   
	   public void showRequestMessage(DatosBlackJack datosRecibidos, ClienteBlackJack cliente) {
		   String title = "Nueva ronda!";
		   String message = "Quieres Iniciar Una nueva ronda?";
		   int sizeX = 50, sizeY = 50;
		   image = new ImageIcon(this.getClass().getClassLoader().getResource("requestNewRound.png"));
		   image = new ImageIcon(image.getImage().getScaledInstance(sizeX, sizeY, Image.SCALE_DEFAULT));
		   int answer = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, image);
		   switch(answer) {
	   			case JOptionPane.YES_OPTION:
//									   ventanaApuestas = new VentanaApuestas(apuestaYo);
//									   while(!ventanaApuestas.apuestaEstablecida()) {
////										   System.out.println("I'm the infinity itself >:)");
//										   cerrarConexion = ventanaApuestas.seCerroConexion();
//										   if(this.cerrarConexion) {
//											   cliente.cerrarConexion();
//										   }
//									   }
//									   System.out.println("I'm not so inevitable");
//									   calcularValorApuesta(ventanaApuestas.getValorApuesta(), datosRecibidos, cliente);
//									   ventanaApuestas.dispose();
	   								   cliente.enviarMensajeServidor("reiniciar ronda");
									   break;
	   			case JOptionPane.NO_OPTION:
									   message = "Gracias por participar!!";
									   title = "Hasta pronto, " + message;
									   image = new ImageIcon(this.getClass().getClassLoader().getResource("ending.png"));
									   JOptionPane.showMessageDialog(this, null, title, JOptionPane.INFORMATION_MESSAGE, image);
									   cliente.cerrarConexion();
									   break;
	   		}
	   }
	   
	   private void checkIfRoundIsOver(String dealerStatus, DatosBlackJack datosRecibidos, ClienteBlackJack cliente) {
		   if((dealerStatus.equals("voló") || dealerStatus.equals("plantó")) && !pantallaApuestasDesplegada) {
			   this.finDeRonda = true;
			   pantallaApuestasDesplegada = true;
			   //Ventana de aviso del valor final de la apuesta
			   showFinalBet(cliente);
			   //Ventana de pregunta
			   showRequestMessage(datosRecibidos, cliente);
		   }
	   }
	   
	   public boolean seCerroConexion() {
		   return this.cerrarConexion;
	   }
	   
	   private class Escucha implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			// TODO Auto-generated method stub
			if(actionEvent.getSource()==pedir) {
				System.out.println("Aaaaaa!");
				//enviar pedir carta al servidor
				enviarDatos("pedir");				
			}else {
				System.out.println("SIUUUU!");
				//enviar plantar al servidor
				enviarDatos("plantar");
				activarBotones(false);
			}
		}
	   }
}
