package servidorbj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import comunes.Baraja;
import comunes.Carta;
import comunes.DatosBlackJack;

/* Clase encargada de realizar la gesti?n del juego, esto es, el manejo de turnos y estado del juego.
 * Tambi?n gestiona al jugador Dealer. 
 * El Dealer tiene una regla de funcionamiento definida:
 * Pide carta con 16 o menos y Planta con 17 o mas.
 */
public class ServidorBJ implements Runnable{
	//constantes para manejo de la conexion.
	public static final int PUERTO=7377;
	public static final String IP="127.0.0.1";
	public static final int LONGITUD_COLA=3;

	// variables para funcionar como servidor
	private ServerSocket server;
	private Socket conexionJugador;
	
	//variables para manejo de hilos
	private ExecutorService manejadorHilos;
	private Lock bloqueoJuego;
	private Condition esperarInicio, esperarTurno, finalizar;
	private Jugador[] jugadores;
	
	//variables de control del juego
	private String[] idJugadores;
	private volatile boolean finDeRonda, dealerTermina;
	private int jugadorEnTurno, jugadoresQueReinician, jugadoresQueTerminan;
	//private boolean iniciarJuego;
	private Baraja mazo;
	private ArrayList<ArrayList<Carta>> manosJugadores;
	private ArrayList<Carta> manoJugador1;
	private ArrayList<Carta> manoJugador2;
	private ArrayList<Carta> manoJugador3;
	private ArrayList<Carta> manoDealer;
	private int[] valorManos;
	private double[] valorApuestas;
	private DatosBlackJack datosEnviar;
	
	public ServidorBJ() {
	    //inicializar variables de control del juego
		inicializarVariablesControlRonda();
	    //inicializar las variables de manejo de hilos
		inicializareVariablesManejoHilos();
		//crear el servidor
    	try {
    		mostrarMensaje("Iniciando el servidor...");
			server = new ServerSocket(PUERTO,LONGITUD_COLA);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private void inicializareVariablesManejoHilos() {
		// TODO Auto-generated method stub
    	manejadorHilos = Executors.newFixedThreadPool(LONGITUD_COLA);
		bloqueoJuego = new ReentrantLock();
		esperarInicio = bloqueoJuego.newCondition();
		esperarTurno = bloqueoJuego.newCondition();
		finalizar = bloqueoJuego.newCondition();
		jugadores = new Jugador[LONGITUD_COLA];	
	}

	private void inicializarVariablesControlRonda() {
		// TODO Auto-generated method stub
    	 //Variables de control del juego.
		
		idJugadores = new String[LONGITUD_COLA];
		valorApuestas = new double[LONGITUD_COLA];
		valorManos = new int[LONGITUD_COLA+1];
		finDeRonda = false;
		dealerTermina = false;
		jugadoresQueReinician = 0;
		jugadoresQueTerminan = 0;
		
		mazo = new Baraja();
		Carta carta;
		
		manoJugador1 = new ArrayList<Carta>();
		manoJugador2 = new ArrayList<Carta>();
		manoJugador3 = new ArrayList<Carta>();
		manoDealer = new ArrayList<Carta>();
		
		//reparto inicial jugadores 1, 2 y 3
		for(int i=1;i<=2;i++) {
		  carta = mazo.getCarta();
		  manoJugador1.add(carta);
		  calcularValorMano(carta,0);
		  carta = mazo.getCarta();
		  manoJugador2.add(carta);
		  calcularValorMano(carta,1);
		  carta = mazo.getCarta();
		  manoJugador3.add(carta);
		  calcularValorMano(carta,2);
		}
		//Carta inicial Dealer
		carta = mazo.getCarta();
		manoDealer.add(carta);
		calcularValorMano(carta,3);
		
		//gestiona las tres manos en un solo objeto para facilitar el manejo del hilo
		manosJugadores = new ArrayList<ArrayList<Carta>>(LONGITUD_COLA+1);
		manosJugadores.add(manoJugador1);
		manosJugadores.add(manoJugador2);
		manosJugadores.add(manoJugador3);
		manosJugadores.add(manoDealer);
	}

	private void calcularValorMano(Carta carta, int i) {
		// TODO Auto-generated method stub
    	
			if(carta.getValor().equals("As")) {
				valorManos[i]+=11;
			}else {
				if(carta.getValor().equals("J") || carta.getValor().equals("Q")
						   || carta.getValor().equals("K")) {
					valorManos[i]+=10;
				}else {
					valorManos[i]+=Integer.parseInt(carta.getValor()); 
				}
		}
	}
	
	public void iniciar() {
       	//esperar a los clientes
    	mostrarMensaje("Esperando a los jugadores...");
    	
    	for(int i=0; i<LONGITUD_COLA;i++) {
    		try {
				conexionJugador = server.accept();
				jugadores[i] = new Jugador(conexionJugador,i);
	    		manejadorHilos.execute(jugadores[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
    	} 	
    }
    
	private void mostrarMensaje(String mensaje) {
		System.out.println(mensaje);
	}
	
	private void iniciarRondaJuego() {
		
		this.mostrarMensaje("bloqueando al servidor para despertar al jugador 1");
    	bloqueoJuego.lock();
    	
    	//despertar al jugador 1 porque es su turno
    	try {
    		this.mostrarMensaje("Despertando al jugador 1 para que inicie el juego");
        	jugadores[0].setSuspendido(false);
        	jugadores[1].setSuspendido(false);
//        	esperarInicio.signal();
        	esperarInicio.signalAll();
    	}catch(Exception e) {
    		
    	}finally {
    		this.mostrarMensaje("Desbloqueando al servidor luego de despertar al jugador 1 para que inicie el juego");
    		bloqueoJuego.unlock();
    	}			
	}
	
    private boolean seTerminoRonda() {
       return this.finDeRonda;	
    }
    
    private void setTerminoRonda(boolean answer) {
    	this.finDeRonda = answer;
    }
    
    private void analizarMensaje(String entrada, int indexJugador) {
		// TODO Auto-generated method stub
        //garantizar que solo se analice la petici?n del jugador en turno.
    	while(indexJugador!=jugadorEnTurno) {
    		bloqueoJuego.lock();
    		try {
				esperarTurno.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		bloqueoJuego.unlock();
    	}
    	
    	//valida turnos para jugador 0 o 1
        	
    	if(entrada.equals("pedir")) {
    		//dar carta 
    		mostrarMensaje("Se envi? carta al jugador "+idJugadores[indexJugador]);
    		Carta carta = mazo.getCarta();
    		//adicionar la carta a la mano del jugador en turno
    		manosJugadores.get(indexJugador).add(carta);
    		calcularValorMano(carta, indexJugador);
    		
    		datosEnviar = new DatosBlackJack();
    		datosEnviar.setIdJugadores(idJugadores);
    		datosEnviar.setValorApuestas(valorApuestas);
			datosEnviar.setValorManos(valorManos);
			datosEnviar.setCarta(carta);
			datosEnviar.setJugador(idJugadores[indexJugador]);
    		//determinar qu? sucede con la carta dada en la mano del jugador y 
			//mandar mensaje a todos los jugadores
    		if(valorManos[indexJugador]>21) {
    			//jugador Vol?
	    		datosEnviar.setMensaje(idJugadores[indexJugador]+" tienes "+valorManos[indexJugador]+" volaste :(");	
	    		datosEnviar.setJugadorEstado("vol?");
	    		
	    		jugadores[0].enviarMensajeCliente(datosEnviar);
	    		jugadores[1].enviarMensajeCliente(datosEnviar);
	    		jugadores[2].enviarMensajeCliente(datosEnviar);
	    		
	    		jugadoresQueTerminan++;
	    		
	    		//notificar a todos que jugador sigue
	    		if(jugadorEnTurno==0) {
	        		
	        		datosEnviar = new DatosBlackJack();
		    		datosEnviar.setIdJugadores(idJugadores);
		    		datosEnviar.setValorApuestas(valorApuestas);
					datosEnviar.setValorManos(valorManos);
					datosEnviar.setJugador(idJugadores[1]);
					datosEnviar.setJugadorEstado("iniciar");
					datosEnviar.setMensaje(idJugadores[1]+" te toca jugar y tienes "+valorManos[1]);
					
					jugadores[0].enviarMensajeCliente(datosEnviar);
					jugadores[1].enviarMensajeCliente(datosEnviar);
					jugadores[2].enviarMensajeCliente(datosEnviar);
					
					//levantar al jugador en espera de turno
					
					bloqueoJuego.lock();
		    		try {
						//esperarInicio.await();
						jugadores[0].setSuspendido(true);
						esperarTurno.signalAll();
						jugadorEnTurno++;
					}finally {
						bloqueoJuego.unlock();
					}
	        	} 
	    		else if(jugadorEnTurno==1) {
	        		
	        		datosEnviar = new DatosBlackJack();
		    		datosEnviar.setIdJugadores(idJugadores);
		    		datosEnviar.setValorApuestas(valorApuestas);
					datosEnviar.setValorManos(valorManos);
					datosEnviar.setJugador(idJugadores[2]);
					datosEnviar.setJugadorEstado("iniciar");
					datosEnviar.setMensaje(idJugadores[2]+" te toca jugar y tienes "+valorManos[2]);
					
					jugadores[0].enviarMensajeCliente(datosEnviar);
					jugadores[1].enviarMensajeCliente(datosEnviar);
					jugadores[2].enviarMensajeCliente(datosEnviar);
					
					//levantar al jugador en espera de turno
					
					bloqueoJuego.lock();
		    		try {
						//esperarInicio.await();
						jugadores[1].setSuspendido(true);
						esperarTurno.signalAll();
						jugadorEnTurno++;
					}finally {
						bloqueoJuego.unlock();
					}
	        	} else {//era el jugador 3 entonces se debe iniciar el dealer
	        		//notificar a todos que le toca jugar al dealer
	        		datosEnviar = new DatosBlackJack();
		    		datosEnviar.setIdJugadores(idJugadores);
		    		datosEnviar.setValorApuestas(valorApuestas);
					datosEnviar.setValorManos(valorManos);
					datosEnviar.setJugador("dealer");
					datosEnviar.setJugadorEstado("iniciar");
					datosEnviar.setMensaje("Dealer se repartir? carta");
					
					jugadores[0].enviarMensajeCliente(datosEnviar);
					jugadores[1].enviarMensajeCliente(datosEnviar);
					jugadores[2].enviarMensajeCliente(datosEnviar);
					
					iniciarDealer();
	        	}		
    		}else {//jugador no se pasa de 21 puede seguir jugando
    			datosEnviar.setCarta(carta);
    			datosEnviar.setJugador(idJugadores[indexJugador]);
    			datosEnviar.setMensaje(idJugadores[indexJugador]+" ahora tienes "+valorManos[indexJugador]);
	    		datosEnviar.setJugadorEstado("sigue");
	    		
	    		jugadores[0].enviarMensajeCliente(datosEnviar);
	    		jugadores[1].enviarMensajeCliente(datosEnviar);
	    		jugadores[2].enviarMensajeCliente(datosEnviar);
	    		
    		}
    	}else {
    		//jugador en turno plant?
    		datosEnviar = new DatosBlackJack();
    		datosEnviar.setIdJugadores(idJugadores);
    		datosEnviar.setValorApuestas(valorApuestas);
			datosEnviar.setValorManos(valorManos);
			datosEnviar.setJugador(idJugadores[indexJugador]);
    		datosEnviar.setMensaje(idJugadores[indexJugador]+" se plant?");
    		datosEnviar.setJugadorEstado("plant?");
    		
    		jugadores[0].enviarMensajeCliente(datosEnviar);		    		
    		jugadores[1].enviarMensajeCliente(datosEnviar);
    		jugadores[2].enviarMensajeCliente(datosEnviar);
    		
    		jugadoresQueTerminan++;
    		
    		//notificar a todos el jugador que sigue en turno
    		if(jugadorEnTurno==0) {
        		
        		datosEnviar = new DatosBlackJack();
	    		datosEnviar.setIdJugadores(idJugadores);
	    		datosEnviar.setValorApuestas(valorApuestas);
				datosEnviar.setValorManos(valorManos);
				datosEnviar.setJugador(idJugadores[1]);
				datosEnviar.setJugadorEstado("iniciar");
				datosEnviar.setMensaje(idJugadores[1]+" te toca jugar y tienes "+valorManos[1]);
				
				jugadores[0].enviarMensajeCliente(datosEnviar);
				jugadores[1].enviarMensajeCliente(datosEnviar);
				jugadores[2].enviarMensajeCliente(datosEnviar);
				
				//levantar al jugador en espera de turno
				
				bloqueoJuego.lock();
	    		try {
					//esperarInicio.await();
					jugadores[0].setSuspendido(true);
					esperarTurno.signalAll();
					jugadorEnTurno++;
				}finally {
					bloqueoJuego.unlock();
				}
        	} 
    		else if(jugadorEnTurno==1) {
        		
        		datosEnviar = new DatosBlackJack();
	    		datosEnviar.setIdJugadores(idJugadores);
	    		datosEnviar.setValorApuestas(valorApuestas);
				datosEnviar.setValorManos(valorManos);
				datosEnviar.setJugador(idJugadores[2]);
				datosEnviar.setJugadorEstado("iniciar");
				datosEnviar.setMensaje(idJugadores[2]+" te toca jugar y tienes "+valorManos[2]);
				
				jugadores[0].enviarMensajeCliente(datosEnviar);
				jugadores[1].enviarMensajeCliente(datosEnviar);
				jugadores[2].enviarMensajeCliente(datosEnviar);
				
				//levantar al jugador en espera de turno
				
				bloqueoJuego.lock();
	    		try {
					//esperarInicio.await();
					jugadores[1].setSuspendido(true);
					esperarTurno.signalAll();
					jugadorEnTurno++;
				}finally {
					bloqueoJuego.unlock();
				}
        	} else {//era el jugador 3 entonces se debe iniciar el dealer
        		//notificar a todos que le toca jugar al dealer
        		datosEnviar = new DatosBlackJack();
	    		datosEnviar.setIdJugadores(idJugadores);
	    		datosEnviar.setValorApuestas(valorApuestas);
				datosEnviar.setValorManos(valorManos);
				datosEnviar.setJugador("dealer");
				datosEnviar.setJugadorEstado("iniciar");
				datosEnviar.setMensaje("Dealer se repartir? carta");
				
				jugadores[0].enviarMensajeCliente(datosEnviar);
				jugadores[1].enviarMensajeCliente(datosEnviar);
				jugadores[2].enviarMensajeCliente(datosEnviar);
				
				iniciarDealer();
        	}	
    	}
   } 
    
    public void iniciarDealer() {
       //le toca turno al dealer.
    	Thread dealer = new Thread(this);
    	dealer.start();
    }
    
    /*The Class Jugador. Clase interna que maneja el servidor para gestionar la comunicaci?n
     * con cada cliente Jugador que se conecte
     */
    private class Jugador implements Runnable{
       
    	//varibles para gestionar la comunicaci?n con el cliente (Jugador) conectado
        private Socket conexionCliente;
    	private ObjectOutputStream out;
    	private ObjectInputStream in;
    	private String entrada;
    	
    	//variables de control
    	private int indexJugador;
    	private boolean suspendido;
  
		public Jugador(Socket conexionCliente, int indexJugador) {
			this.conexionCliente = conexionCliente;
			this.indexJugador = indexJugador;
			suspendido = true;
			//crear los flujos de E/S
			try {
				out = new ObjectOutputStream(conexionCliente.getOutputStream());
				out.flush();
				in = new ObjectInputStream(conexionCliente.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}	
				
		private void setSuspendido(boolean suspendido) {
			this.suspendido = suspendido;
		}
		
		private void asignarValorApuesta(double valorNuevo, String idJugador) {
			mostrarMensaje(String.format("nuevaApuesta: %s, idJugador: %s", valorNuevo, idJugador));
			for(int i = 0; i < valorApuestas.length; i++) {
				if(idJugadores[i].equals(idJugador) ) {
					mostrarMensaje(String.format("valorApuestas[%s] antes: %s", i, valorApuestas[i]));
					valorApuestas[i] = valorNuevo;
					mostrarMensaje(String.format("valorApuestas[%s] ahora: %s", i, valorApuestas[i]));
				}
			}
		}
		
		private void wait(int miliseconds) {
			try {
				Thread.sleep(miliseconds);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void leerSolicitud() {
			try {
				double valorNuevo = Double.parseDouble((String) in.readObject());
				String idJugador = (String) in.readObject();
				asignarValorApuesta(valorNuevo, idJugador);
				
				datosEnviar.setIdJugadores(idJugadores);
	    		datosEnviar.setValorApuestas(valorApuestas);
				datosEnviar.setValorManos(valorManos);
				mostrarMensaje(String.format("nuevasApuestas: %s, idJugadores: %s, datosEnviar: %s", datosEnviar.getValorApuestas(), datosEnviar.getIdJugadores(), datosEnviar));
				mostrarMensaje(String.format("apuesta1: %s, apuesta2: %s", valorApuestas[0], valorApuestas[1]));
				mostrarMensaje(String.format("[datos enviados] apuesta1: %s, apuesta2: %s", datosEnviar.getValorApuestas()[0], datosEnviar.getValorApuestas()[1]));
				for(int i = 0; i < datosEnviar.getValorApuestas().length; i++) {
					enviarMensajeCliente(datosEnviar.getValorApuestas()[i]);
				}
				enviarMensajeCliente(datosEnviar.getIdJugadores());
				
				String clientAnswer = "";
				
//				Thread.sleep(10000);
//				while(true) {
////					mostrarMensaje("you've killed your computer!");
//					if(in.available() != 0) {
//						clientAnswer = (String) in.readObject();
//					}
////					System.out.println(String.format("clientAnswer: %s", clientAnswer));
//					if(clientAnswer.equals("cambios ajustados")) {
//						break;
//					}
//				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private int calcularGanancias(String idPlayer) {
			int valorFinal = 0;
			int index = 0;
			for(int i = 0; i < idJugadores.length; i++) {
				if(idJugadores[i].equals(idPlayer)) {
					valorFinal = (int) valorApuestas[i];
					index = i;
					break;
				}
			}
			if(valorManos[LONGITUD_COLA] <= 21) {
				if(valorManos[LONGITUD_COLA] < valorManos[index]) {
					if(valorManos[index] == 21) {
						return (valorFinal*1/1);
					}
					else {
						return (valorFinal*3/2);
					}
				}
				if(valorManos[LONGITUD_COLA] == valorManos[index]) {
					return valorFinal;
				}
				else {
					return 0;
				}
			}
			else {
				if(valorManos[index] == 21) {
					return (valorFinal*1/1);
				}
				else {
					return (valorFinal*3/2);
				}
			}
		}
		
		//Dar nuevos valores a los mazos.------------------------------------------------------------
		public void reiniciar() {
			mostrarMensaje("Se reinici? el juego");
			finDeRonda = false;
			dealerTermina = false;
			jugadoresQueReinician = 0;
			jugadoresQueTerminan = 0;
			
			Carta carta;
			manoJugador1 = new ArrayList<Carta>();
			manoJugador2 = new ArrayList<Carta>();
			manoJugador3 = new ArrayList<Carta>();
			manoDealer = new ArrayList<Carta>();
			manosJugadores = new ArrayList<ArrayList<Carta>>();
			mazo = new Baraja();
			
			for(int i = 0; i <= LONGITUD_COLA; i++) {
				valorManos[i] = 0;
			}
			
			//reparto inicial jugadores 1, 2 y 3
			for(int i=1;i<=2;i++) {
			  carta = mazo.getCarta();
			  manoJugador1.add(carta);
			  calcularValorMano(carta,0);
			  carta = mazo.getCarta();
			  manoJugador2.add(carta);
			  calcularValorMano(carta,1);
			  carta = mazo.getCarta();
			  manoJugador3.add(carta);
			  calcularValorMano(carta,2);
			}
			
			//Carta inicial Dealer
			carta = mazo.getCarta();
			manoDealer.add(carta);
			calcularValorMano(carta, LONGITUD_COLA);
			
			//gestiona las tres manos en un solo objeto para facilitar el manejo del hilo
			manosJugadores = new ArrayList<ArrayList<Carta>>(LONGITUD_COLA+1);
			manosJugadores.add(manoJugador1);
			manosJugadores.add(manoJugador2);
			manosJugadores.add(manoJugador3);
			manosJugadores.add(manoDealer);
			
		}
		
		private void reiniciarRonda() {
			mostrarMensaje("Server restarted the game!");
			//garantizar que solo se analice la petici?n del jugador en turno.
    		//---------------------------------------------------------------------------------
    		//Utiliza reiniciar y le envia al cliente (0) los nuevos valores, este mensaje lo lee al dar reiniciar en la vista
    		//Actualizar barajas jugadores.
    		reiniciar();
    		datosEnviar = new DatosBlackJack();
    		datosEnviar.setJugador(idJugadores[0]);
			datosEnviar.setManoDealer(manoDealer);
			datosEnviar.setManoJugador1(manoJugador1);
			datosEnviar.setManoJugador2(manoJugador2);
			datosEnviar.setManoJugador3(manoJugador3);
			datosEnviar.setIdJugadores(idJugadores);
			datosEnviar.setValorApuestas(valorApuestas);
			datosEnviar.setValorManos(valorManos);
			datosEnviar.setReiniciar(true);
			datosEnviar.setJugadorEstado("iniciar");
			datosEnviar.setMensaje("Inicias "+idJugadores[0]+" tienes "+valorManos[0]);
			
			for(int i = 0; i < LONGITUD_COLA; i++) {
				jugadores[i].enviarMensajeCliente(datosEnviar);
			}
			
			bloqueoJuego.lock();
			
    		try {
				//esperarInicio.await();
				jugadores[indexJugador].setSuspendido(true);
				esperarTurno.signalAll();
				jugadorEnTurno = 0;
			}finally {
				bloqueoJuego.unlock();
			}
    		
    		for(int i=0; i<LONGITUD_COLA;i++) {
    			manejadorHilos.execute(jugadores[i]);
    		}
    		
		}
	   
		@Override
		public void run() {
			// TODO Auto-generated method stub	
			//procesar los mensajes eviados por el cliente
			
			//ver cual jugador es
			if(indexJugador==0) {
				//es jugador 1, debe ponerse en espera a la llegada del otro jugador
				
				try {
					//guarda el nombre del primer jugador
					idJugadores[0] = (String)in.readObject();
					valorApuestas[0] = Double.parseDouble((String) in.readObject());
					mostrarMensaje("Hilo establecido con jugador (1) "+idJugadores[0]);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				mostrarMensaje("bloquea servidor para poner en espera de inicio al jugador 1");
				bloqueoJuego.lock(); //bloquea el servidor
				
				while(suspendido) {
					mostrarMensaje("Parando al Jugador 1 en espera del otro jugador...");
					try {
						esperarInicio.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						mostrarMensaje("Desbloquea Servidor luego de bloquear al jugador 1");
						bloqueoJuego.unlock();
					}
				}
				
				//ya se conect? el otro jugador, 
				//le manda al jugador 1 todos los datos para montar la sala de Juego
				//le toca el turno a jugador 1
				
				mostrarMensaje("manda al jugador 1 todos los datos para montar SalaJuego");
				datosEnviar = new DatosBlackJack();
				datosEnviar.setManoDealer(manosJugadores.get(3));
				datosEnviar.setManoJugador1(manosJugadores.get(0));
				datosEnviar.setManoJugador2(manosJugadores.get(1));
				datosEnviar.setManoJugador3(manosJugadores.get(2));
				datosEnviar.setIdJugadores(idJugadores);
				datosEnviar.setValorApuestas(valorApuestas);
				datosEnviar.setValorManos(valorManos);
				datosEnviar.setMensaje("Inicias "+idJugadores[0]+" tienes "+valorManos[0]);
				enviarMensajeCliente(datosEnviar);
				jugadorEnTurno=0;
			}
			else if(indexJugador==1) {
				//es jugador 1, debe ponerse en espera a la llegada del otro jugador
				
				try {
					//guarda el nombre del primer jugador
					idJugadores[1] = (String)in.readObject();
					valorApuestas[1] = Double.parseDouble((String) in.readObject());
					mostrarMensaje("Hilo establecido con jugador (2) "+idJugadores[1]);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				mostrarMensaje("bloquea servidor para poner en espera de inicio al jugador 2");
				bloqueoJuego.lock(); //bloquea el servidor
				
				while(suspendido) {
					mostrarMensaje("Parando al Jugador 2 en espera del otro jugador...");
					try {
						esperarInicio.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						mostrarMensaje("Desbloquea Servidor luego de bloquear al jugador 2");
						bloqueoJuego.unlock();
					}
				}
				
				//ya se conect? el otro jugador, 
				//le manda al jugador 1 todos los datos para montar la sala de Juego
				//le toca el turno a jugador 1
				
				mostrarMensaje("manda al jugador 1 todos los datos para montar SalaJuego");
				datosEnviar = new DatosBlackJack();
				datosEnviar.setManoDealer(manosJugadores.get(3));
				datosEnviar.setManoJugador1(manosJugadores.get(0));
				datosEnviar.setManoJugador2(manosJugadores.get(1));			
				datosEnviar.setManoJugador3(manosJugadores.get(2));			
				datosEnviar.setIdJugadores(idJugadores);
				datosEnviar.setValorApuestas(valorApuestas);
				datosEnviar.setValorManos(valorManos);
				datosEnviar.setMensaje("Inicias "+idJugadores[0]+" tienes "+valorManos[0]);
				enviarMensajeCliente(datosEnviar);
//				jugadorEnTurno=0;
				suspendido = true;
				mostrarMensaje("Bloquea al servidor para poner en espera de turno al jugador 2");
				bloqueoJuego.lock();
				try {
					mostrarMensaje("Pone en espera de turno al jugador 2");
					esperarTurno.await();
                    //
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					bloqueoJuego.unlock();
				}
			}
			else {
				   //Es jugador 3
				   //le manda al jugador 3 todos los datos para montar la sala de Juego
				   //jugador 3 debe esperar su turno
				try {
					idJugadores[2]=(String)in.readObject();
					valorApuestas[2] = Double.parseDouble((String) in.readObject());
					mostrarMensaje("Hilo jugador (3) "+idJugadores[2]);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				mostrarMensaje("manda al jugador 3 el nombre del jugador 2");
				
				datosEnviar = new DatosBlackJack();
				datosEnviar.setManoDealer(manosJugadores.get(3));
				datosEnviar.setManoJugador1(manosJugadores.get(0));
				datosEnviar.setManoJugador2(manosJugadores.get(1));			
				datosEnviar.setManoJugador3(manosJugadores.get(2));			
				datosEnviar.setIdJugadores(idJugadores);
				datosEnviar.setValorApuestas(valorApuestas);
				datosEnviar.setValorManos(valorManos);
				datosEnviar.setMensaje("Inicias "+idJugadores[0]+" tienes "+valorManos[0]);
				enviarMensajeCliente(datosEnviar);
				
				iniciarRondaJuego(); //despertar al jugador 1 para iniciar el juego
				mostrarMensaje("Bloquea al servidor para poner en espera de turno al jugador 2");
				bloqueoJuego.lock();
				try {
					mostrarMensaje("Pone en espera de turno al jugador 3");
					esperarTurno.await();
					mostrarMensaje("Despierta de la espera de inicio del juego al jugador 1");
                    //
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					bloqueoJuego.unlock();
				}	
			}
			
			while(!seTerminoRonda()) {
				try {
					entrada = (String) in.readObject();
					if(entrada.equals("reiniciar ronda")) {
						System.out.println("un jugador quiere reiniciar la ronda!");
						jugadoresQueReinician++;
//						leerSolicitud();
						if(jugadoresQueReinician == LONGITUD_COLA) {
							reiniciarRonda();
						}
					}
					if(entrada.equals("calcular apuesta")) {
						while(!dealerTermina || jugadoresQueTerminan < LONGITUD_COLA) {
							//do nothing
						}
						mostrarMensaje("Calculando Apuesta!");
						String idPlayer = (String) in.readObject();
						int finalValue = calcularGanancias(idPlayer);
						enviarMensajeCliente(finalValue);
					}
					if(!dealerTermina) {
						analizarMensaje(entrada,indexJugador);
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//controlar cuando se cierra un cliente
				}
			}
			//cerrar conexi?n
		}
		
		public void enviarMensajeCliente(Object mensaje) {
			try {  
				mostrarMensaje("Se mand? mensaje al cliente");
				out.writeObject(mensaje);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
    }//fin inner class Jugador      

    //Jugador dealer emulado por el servidor
	@Override
	public void run() {
		// TODO Auto-generated method stub
		mostrarMensaje("Incia el dealer ...");
        boolean pedir = true;
        
        while(!dealerTermina) {
		  	Carta carta = mazo.getCarta();
			//adicionar la carta a la mano del dealer
			manosJugadores.get(LONGITUD_COLA).add(carta);
			calcularValorMano(carta, 3);
			
			mostrarMensaje("El dealer recibe "+carta.toString()+" suma "+ valorManos[LONGITUD_COLA]);
			

    		datosEnviar = new DatosBlackJack();
			datosEnviar.setCarta(carta);
			datosEnviar.setJugador("dealer");
				
			if(valorManos[LONGITUD_COLA]<=16) {
				datosEnviar.setJugadorEstado("sigue");
				datosEnviar.setMensaje("Dealer ahora tiene "+valorManos[LONGITUD_COLA]);
				mostrarMensaje("El dealer sigue jugando");
			}else {
				if(valorManos[LONGITUD_COLA]>21) {
					datosEnviar.setJugadorEstado("vol?");
					datosEnviar.setMensaje("Dealer ahora tiene "+valorManos[LONGITUD_COLA]+" vol? :(");
					pedir=false;
					mostrarMensaje("El dealer vol?");
					dealerTermina = true;
				}else {
					datosEnviar.setJugadorEstado("plant?");
					datosEnviar.setMensaje("Dealer ahora tiene "+valorManos[LONGITUD_COLA]+" plant?");
					pedir=false;
					mostrarMensaje("El dealer plant?");
					dealerTermina = true;
				}
			}
			//envia la jugada a los otros jugadores
			datosEnviar.setCarta(carta);
			jugadores[0].enviarMensajeCliente(datosEnviar);
			jugadores[1].enviarMensajeCliente(datosEnviar);
			jugadores[2].enviarMensajeCliente(datosEnviar);
				
        }//fin while
        
	}
    
}//Fin class ServidorBJ
