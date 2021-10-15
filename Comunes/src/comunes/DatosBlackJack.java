package comunes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class DatosBlackJack implements Serializable{
	private String[] idJugadores;
	private ArrayList<Carta> manoJugador1, manoJugador2, manoJugador3, manoDealer;
	private int[] valorManos;
	private double[] valorApuestas;
	private Carta carta;
	private String mensaje;
	private String jugador,jugadorEstado;
	private volatile boolean reiniciar;
	
	public String getJugador() {
		return jugador;
	}
	public void setJugador(String jugador) {
		this.jugador = jugador;
	}
	
	public String getJugadorEstado() {
		return jugadorEstado;
	}
	public void setJugadorEstado(String jugadorEstado) {
		this.jugadorEstado = jugadorEstado;
	}
		
	public String[] getIdJugadores() {
		return idJugadores;
	}
	public void setIdJugadores(String[] idJugadores) {
		this.idJugadores = idJugadores;
	}
	
	public double[] getValorApuestas() {
		return valorApuestas;
	}
	
	public void setValorApuestas(double[] valorApuestas) {
		this.valorApuestas = valorApuestas;
	}
	
	public void setValorApuesta(int index, double newValue) {
		this.valorApuestas[index] = newValue;
	}
	
	public ArrayList<Carta> getManoJugador1() {
		return manoJugador1;
	}
	public void setManoJugador1(ArrayList<Carta> manoJugador1) {
		this.manoJugador1 = manoJugador1;
	}
	
	public ArrayList<Carta> getManoJugador2() {
		return manoJugador2;
	}
	
	public void setManoJugador2(ArrayList<Carta> manoJugador2) {
		this.manoJugador2 = manoJugador2;
	}
	
	public ArrayList<Carta> getManoJugador3() {
		return manoJugador3;
	}
	
	public void setManoJugador3(ArrayList<Carta> manoJugador3) {
		this.manoJugador3 = manoJugador3;
	}
	
	public ArrayList<Carta> getManoDealer() {
		return manoDealer;
	}
	public void setManoDealer(ArrayList<Carta> manoDealer) {
		this.manoDealer = manoDealer;
	}
	
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public void setValorManos(int[] valorManos) {
		this.valorManos=valorManos;
	}
	public int[] getValorManos() {
		return valorManos;	
	}
	public void setCarta(Carta carta) {
		this.carta=carta;
	}
	public Carta getCarta() {
		return carta;
	}
	
	public boolean isNumber(String text) {
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
	
	public void setReiniciar(boolean reiniciar) {
		this.reiniciar=reiniciar;
	}
	
	public boolean getReiniciar() {
		return reiniciar;
	}
	
}
