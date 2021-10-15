package clientebj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import comunes.DatosBlackJack;

// TODO: Auto-generated Javadoc
/**
 * The Class ClienteBlackJack. 
 * 
 */
public class ClienteBlackJack extends JFrame implements Runnable{
	//Constantes de Interfaz Grafica
	public static final int WIDTH=670;
	public static final int HEIGHT=440;
	
	//Constantes de conexión con el Servidor BlackJack
	public static final int PUERTO=7377;
	public static final String IP="127.0.0.1";
	
	//variables de control del juego
	private String idYo, otroJugador, otroJugador2;
	private boolean turno;
	private int numeroJugadores, valorGanancias;
	private double apuestaYo, apuestaOtroJugador, apuestaOtroJugador2;
	private DatosBlackJack datosRecibidos;
	private double[] nuevasApuestas;
	private String[] idJugadores;
	
	//variables de control de hilos
	private Lock locker;
	private Condition threadsManager;
	
	//variables para manejar la conexión con el Servidor BlackJack
	private Socket conexion;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	//Componentes Graficos
	private JDesktopPane containerInternalFrames;
	private VentanaEntrada ventanaEntrada;
	private VentanaEspera ventanaEspera;
	private VentanaSalaJuego ventanaSalaJuego;
	
	/**
	 * Instantiates a new cliente black jack.
	 */
	public ClienteBlackJack() {
		initGUI();
		
		//default window settings
		this.setTitle("Juego BlackJack");
		this.setBackground(new Color(55,222,37));
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Inits the GUI.
	 */
	private void initGUI() {
		//set up JFrame Container y Layout
        
		//Create Listeners objects
		
		//Create Control objects
		turno=false;
		
		//Create threads-handler variables
		locker = new ReentrantLock();
		threadsManager = locker.newCondition();
		
		//Set up JComponents
	
		this.setBackground(SystemColor.activeCaption);
		containerInternalFrames = new JDesktopPane();
		containerInternalFrames.setOpaque(false);
		this.setContentPane(containerInternalFrames);
		adicionarInternalFrame(new VentanaEntrada(this));
	}
	
	public void adicionarInternalFrame(JInternalFrame nuevoInternalFrame) {
		add(nuevoInternalFrame);
	}
	
	public void iniciarHilo() {
		ExecutorService hiloCliente = Executors.newFixedThreadPool(1);
		hiloCliente.execute(this);
		//Thread hilo = new Thread(this);
		//hilo.start();
	}
	
	public void setIdYo(String id) {
		idYo=id;
	}
	
	public void setApuestaYo(String apuesta) {
		double number = Double.parseDouble(apuesta);
		apuestaYo = number;
	}
	
	private void mostrarMensajes(String mensaje) {
		System.out.println(mensaje);
	}
	
	public void enviarMensajeServidor(String mensaje) {
		try {
			out.writeObject(mensaje);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	private void ajustarDatosJugador() throws ClassNotFoundException, IOException {
		datosRecibidos = new DatosBlackJack();
		datosRecibidos = (DatosBlackJack) in.readObject();
		numeroJugadores = datosRecibidos.getIdJugadores().length;
		if(datosRecibidos.getIdJugadores()[0].equals(idYo)) {
			if (datosRecibidos.getIdJugadores()[1].equals(otroJugador)) {
				idYo = datosRecibidos.getIdJugadores()[0];
				apuestaYo = datosRecibidos.getValorApuestas()[0];
				otroJugador = datosRecibidos.getIdJugadores()[1];
				apuestaOtroJugador = datosRecibidos.getValorApuestas()[1];
				otroJugador2 = datosRecibidos.getIdJugadores()[2];
				apuestaOtroJugador2 = datosRecibidos.getValorApuestas()[2];
			}
			else {
				idYo = datosRecibidos.getIdJugadores()[0];
				apuestaYo = datosRecibidos.getValorApuestas()[0];
				otroJugador = datosRecibidos.getIdJugadores()[2];
				apuestaOtroJugador = datosRecibidos.getValorApuestas()[2];
				otroJugador2 = datosRecibidos.getIdJugadores()[1];
				apuestaOtroJugador2 = datosRecibidos.getValorApuestas()[1];
			}
			turno=true;
		}
		else if(datosRecibidos.getIdJugadores()[0].equals(otroJugador)) {
			if (datosRecibidos.getIdJugadores()[1].equals(idYo)) {
				idYo = datosRecibidos.getIdJugadores()[1];
				apuestaYo = datosRecibidos.getValorApuestas()[1];
				otroJugador = datosRecibidos.getIdJugadores()[0];
				apuestaOtroJugador = datosRecibidos.getValorApuestas()[0];
				otroJugador2 = datosRecibidos.getIdJugadores()[2];
				apuestaOtroJugador2 = datosRecibidos.getValorApuestas()[2];
			}
			else {
				idYo = datosRecibidos.getIdJugadores()[2];
				apuestaYo = datosRecibidos.getValorApuestas()[2];
				otroJugador = datosRecibidos.getIdJugadores()[0];
				apuestaOtroJugador = datosRecibidos.getValorApuestas()[0];
				otroJugador2 = datosRecibidos.getIdJugadores()[1];
				apuestaOtroJugador2 = datosRecibidos.getValorApuestas()[1];
			}
			turno=true;
		} else {
			if (datosRecibidos.getIdJugadores()[1].equals(idYo)) {
				idYo = datosRecibidos.getIdJugadores()[1];
				apuestaYo = datosRecibidos.getValorApuestas()[1];
				otroJugador = datosRecibidos.getIdJugadores()[2];
				apuestaOtroJugador = datosRecibidos.getValorApuestas()[2];
				otroJugador2 = datosRecibidos.getIdJugadores()[0];
				apuestaOtroJugador2 = datosRecibidos.getValorApuestas()[0];
			}
			else {
				idYo = datosRecibidos.getIdJugadores()[2];
				apuestaYo = datosRecibidos.getValorApuestas()[2];
				otroJugador = datosRecibidos.getIdJugadores()[1];
				apuestaOtroJugador = datosRecibidos.getValorApuestas()[1];
				otroJugador2 = datosRecibidos.getIdJugadores()[0];
				apuestaOtroJugador2 = datosRecibidos.getValorApuestas()[0];
			}
		}
	}
 
	public void reajustarDatosJugador(DatosBlackJack datosRecibidos) {
		try {
			ventanaSalaJuego.wait(3000);
			nuevasApuestas = new double[numeroJugadores];
			for(int i = 0; i < numeroJugadores; i++) {
				nuevasApuestas[i] = (double) in.readObject();
			}
			idJugadores = (String[]) in.readObject();
			datosRecibidos.setValorApuestas(nuevasApuestas);
			datosRecibidos.setIdJugadores(idJugadores);
			System.out.println(String.format("[client] newBet 1: %s, newBet 2: %s ", nuevasApuestas[0], nuevasApuestas[1]));
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(String.format("nuevasApuestas: %s, idJugadores: %s, datosRecibidos: %s", nuevasApuestas, datosRecibidos.getIdJugadores(), datosRecibidos));
		
		if(datosRecibidos.getIdJugadores() != null && datosRecibidos.getValorApuestas() != null) {			
			if(datosRecibidos.getIdJugadores()[0].equals(idYo)) {
				otroJugador = datosRecibidos.getIdJugadores()[1];
				System.out.println(String.format("first bet: %s, second bet: %s, numberOfBets: %s", datosRecibidos.getValorApuestas()[0], datosRecibidos.getValorApuestas()[1], datosRecibidos.getValorApuestas().length));
				apuestaOtroJugador = datosRecibidos.getValorApuestas()[1];
				ventanaSalaJuego.actualizarDatosOtrosJugadores(otroJugador, apuestaOtroJugador, otroJugador2, apuestaOtroJugador2);
				ventanaSalaJuego.setModificarApuesta(false);
			}
			else {
				otroJugador = datosRecibidos.getIdJugadores()[0];
				System.out.println(String.format("first bet: %s, second bet: %s, numberOfBets: %s", datosRecibidos.getValorApuestas()[0], datosRecibidos.getValorApuestas()[1], datosRecibidos.getValorApuestas().length));
				apuestaOtroJugador = datosRecibidos.getValorApuestas()[0];
				ventanaSalaJuego.actualizarDatosOtrosJugadores(otroJugador, apuestaOtroJugador, otroJugador2, apuestaOtroJugador2);
				ventanaSalaJuego.setModificarApuesta(false);
			}
		}
	}
	
	public void sleep(int miliseconds) {
		try {
			System.out.println(String.format("Jugador %s se durmió", idYo));
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int calcularGanancias() {
		locker.lock();
		try {
			valorGanancias = (int) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			locker.unlock();
		}
		return valorGanancias;
	}
	
	public void buscarServidor() {
		mostrarMensajes("Jugador buscando al servidor...");
		
		try {
			//buscar el servidor
			conexion = new Socket(IP,PUERTO);
			//obtener flujos E/S
			out = new ObjectOutputStream(conexion.getOutputStream());
			out.flush();
			in = new ObjectInputStream(conexion.getInputStream());
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mostrarMensajes("Jugador conectado al servidor");
		mostrarMensajes("Jugador estableció Flujos E/S");
		//mandar nombre jugador
		mostrarMensajes("Jugador envio nombre: "+idYo);
		mostrarMensajes("Jugador apostó: "+apuestaYo);
		enviarMensajeServidor(idYo);
		enviarMensajeServidor(Double.toString(apuestaYo));
		//procesar comunicación con el ServidorBlackJack
		iniciarHilo();	
	}
	
	@Override
	public void run() {
		//datosRecibidos = new DatosBlackJack();
		// TODO Auto-generated method stub
		//mostrar bienvenida al jugador	
		   
			try {
				ajustarDatosJugador();
//				datosRecibidos = new DatosBlackJack();
//				datosRecibidos = (DatosBlackJack) in.readObject();
//				if(datosRecibidos.getIdJugadores()[0].equals(idYo)) {
//					if (datosRecibidos.getIdJugadores()[1].equals(otroJugador)) {
//						idYo=datosRecibidos.getIdJugadores()[0];
//						otroJugador=datosRecibidos.getIdJugadores()[1];
//						otroJugador2=datosRecibidos.getIdJugadores()[2];
//					} else {
//						idYo=datosRecibidos.getIdJugadores()[0];
//						otroJugador = datosRecibidos.getIdJugadores()[2];
//						otroJugador2 = datosRecibidos.getIdJugadores()[1];
//					}
//					turno=true;
//				}
//				else if(datosRecibidos.getIdJugadores()[0].equals(otroJugador)) {
//					if (datosRecibidos.getIdJugadores()[1].equals(idYo)) {
//						idYo=datosRecibidos.getIdJugadores()[1];
//						otroJugador=datosRecibidos.getIdJugadores()[0];
//						otroJugador2=datosRecibidos.getIdJugadores()[2];
//					} else {
//						idYo=datosRecibidos.getIdJugadores()[2];
//						otroJugador=datosRecibidos.getIdJugadores()[0];
//						otroJugador2=datosRecibidos.getIdJugadores()[1];
//					}
//					turno=true;
//				}
//				else {
//					if (datosRecibidos.getIdJugadores()[1].equals(idYo)) {
//						idYo=datosRecibidos.getIdJugadores()[1];
//						otroJugador=datosRecibidos.getIdJugadores()[2];
//						otroJugador2=datosRecibidos.getIdJugadores()[0];
//					} else {
//						idYo = datosRecibidos.getIdJugadores()[2];
//						otroJugador = datosRecibidos.getIdJugadores()[1];
//						otroJugador2 = datosRecibidos.getIdJugadores()[0];
//					}
//				}
				this.habilitarSalaJuego(datosRecibidos);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//buscando nombre del OtroJugador
			
			//procesar turnos
			while(true) {
				try {
					
					if(ventanaSalaJuego != null) {
						ventanaSalaJuego.actualizarPanelesJugadores();
						fluidClient();
						if(ventanaSalaJuego.getModificarApuesta()) {
							ventanaSalaJuego.wait(5000);
						}
					}
										
					fluidClient();
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		
	}
	
	private void fluidClient() throws ClassNotFoundException, IOException {
		mostrarMensajes("Beginning!");
		datosRecibidos = new DatosBlackJack();
		datosRecibidos = (DatosBlackJack)in.readObject();
		mostrarMensajes("Cliente hilo run recibiendo mensaje servidor ");
		mostrarMensajes(datosRecibidos.getJugador()+" "+datosRecibidos.getJugadorEstado());
		
		ventanaSalaJuego.pintarTurno(datosRecibidos);
	}

	private void habilitarSalaJuego(DatosBlackJack datosRecibidos) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ventanaEspera = (VentanaEspera)containerInternalFrames.getComponent(0);
				ventanaEspera.cerrarSalaEspera();
				ventanaSalaJuego = new VentanaSalaJuego(idYo, otroJugador, otroJugador2, apuestaYo, apuestaOtroJugador, apuestaOtroJugador2);
				ventanaSalaJuego.pintarCartasInicio(datosRecibidos);
				adicionarInternalFrame(ventanaSalaJuego);
                if(turno) {
                	ventanaSalaJuego.activarBotones(turno);
                }
			}
			
		});
	}

	public void cerrarConexion() {
		// TODO Auto-generated method stub
		try {
			in.close();
			out.close();
			conexion.close();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
  
	public void setTurno(boolean turno) {
		this.turno=turno;
	}	
}
