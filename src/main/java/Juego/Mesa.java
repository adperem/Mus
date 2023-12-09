package Juego;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representa una mesa donde juega una partida de mus
 *
 * @author Adrián Pérez Moreno
 */
public class Mesa implements Serializable {

    /**
     * Codigo indentificador de la mesa
     */
    private int codMesa;
    /**
     * Baraja de la mesa
     */
    private Baraja baraja;
    /**
     * Número del jugadro que ha realizado la apuesta más alta en la ronda actual
     */
    private int numJugadorApuestaMasAlta;
    /**
     * Booleano que indica si se ha cortado
     */
    private boolean mus = true;
    /**
     * {@code ArrayList} que guarda las ips de los jugadores de la partida
     */
    private ArrayList<String> ip;
    /**
     * Numero de la ronda actual
     */
    private int numRonda;
    /**
     * Matriz que representa por cada rondo su apuesta más alta
     */
    private int[][] apuestas; //Si un jugador iguala, la apuesta pasa a ser negativa
    /**
     * Guarda las puntuaciones de cada equipo
     */
    private final int[] equipo;
    /**
     * Número del jugador que ha cortado
     */
    private int jugadorCortar;
    /**
     * Cantidad de veces que se ha pasado en la ronda actual
     */
    private int pasadas;
    /**
     * {@code ArrayList} que guarda las manos de los jugadores
     */
    private ArrayList<ArrayList<Carta>> manos;
    /**
     * Buleano que representa si alguien tiene juego
     */
    private boolean juego;

    /**
     * Inicializa una Mesa
     *
     * @param codMesa Identificador de la nueva mesa
     */
    public Mesa(int codMesa) {
        this.baraja = new Baraja();
        this.baraja.barajar();
        this.codMesa = codMesa;
        this.ip = new ArrayList<>(4);
        this.numJugadorApuestaMasAlta = -1;
        this.apuestas = new int[6][2];
        for (int i = 0; i < 6; i++) {
            this.apuestas[i][0] = i;
            this.apuestas[i][1] = -1;
        }
        this.apuestas[2][1] = 0;
        this.apuestas[4][1] = 0;
        this.apuestas[5][1] = -1;
        this.apuestas[3][1] = -1;
        this.apuestas[0][1] = -1;
        this.numRonda = 0;
        this.equipo = new int[2];
        this.equipo[0] = 0;
        this.equipo[1] = 0;
        this.pasadas = 0;
        this.manos = new ArrayList<>(3);
    }

    /**
     * Devuelve el codigo de la mesa
     *
     * @return El codigo de la mesa
     */
    public double getCodMesa() {
        return codMesa;
    }

    /**
     * Devuelve la apuesta más alta de la ronda actual
     *
     * @return Apuesta más alta
     */
    public int getApuestaMasAlta() {
        return this.apuestas[this.numRonda][1];
    }

    /**
     * Devuelve la apuesta más alta la ronda indicada
     * @param ronda ronda indicada
     * @return Apuesta más alta
     */
    public int getApuestaMasAlta(int ronda) {
        return this.apuestas[ronda][1];
    }

    /**
     * Acutualiza el valor de la apuesta más alta
     * @param apuestaMasAlta Valor de la apuesta
     * @param numJugador Jugador que ha realizado la apuesta
     */
    public void setApuestaMasAlta(int apuestaMasAlta, int numJugador) {
        this.apuestas[numRonda][1] = apuestaMasAlta;
        this.numJugadorApuestaMasAlta = numJugador;
    }

    /**
     * Devuelve el número de jugadores de la mesa
     * @return Numero de jugadores
     */
    public int getNumPlayers() {
        return this.ip.size();
    }

    /**
     * Corta el mus
     */
    public void cortar() {
        this.mus = false;
    }

    /**
     * Saca una carta de la baraja
     * @return una carta
     */
    public Carta sacarCarta() {
        Carta carta = this.baraja.sacarCarta();
        if (carta == null) {
            this.baraja = new Baraja();
            return this.baraja.sacarCarta();
        }
        return carta;
    }

    /**
     * Comprueba si se ha cortado
     * @return true si no se ha cortado, false en caso contrario
     */
    public boolean isMus() {
        return mus;
    }


    /**
     * Añade una ip a la mesa
     * @param ip que se desea añadir
     */
    public void addIp(String ip) {
        this.ip.add(ip);
    }

    /**
     * Devuelve las ips de los jugadores
     * @return {@code ArrayList} con las ips
     */
    public ArrayList<String> getIps() {
        return this.ip;
    }

    /**
     * Comprueba si la apuesta es valida
     * @param apuesta cantidad a comprobar
     * @return true si se puede apostar, false en caso contrario
     */
    public boolean puedeApostar(int apuesta) {
        return this.apuestas[numRonda][1] < apuesta;
    }

    /**
     * Devuelve el jugador con la apuesta más alta en la ronda actual
     * @return el número de jugador
     */
    public int getNumJugadorApuestaMasAlta() {
        return numJugadorApuestaMasAlta;
    }

    /**
     * Actualiza el jugador con la apuesta más alta en la ronda actual
     * @param numJugadorApuestaMasAlta número del jugador
     */
    public void setNumJugadorApuestaMasAlta(int numJugadorApuestaMasAlta) {
        this.numJugadorApuestaMasAlta = numJugadorApuestaMasAlta;
    }

    /**
     * Devuelve el número de la ronda actual
     * @return ronda actual
     */
    public int getNumRonda() {
        return numRonda;
    }

    /**
     * Acutaliza el valor de la ronda actual
     * @param numRonda ronda nueva
     */
    public void setNumRonda(int numRonda) {
        this.numRonda = numRonda;
    }

    /**
     * Añade puntos a un equipo
     * @param puntos Puntos a añadir
     * @param numJugador Jugador que recibe los puntos
     */
    public void addPuntos(int puntos, int numJugador) {
        if (numJugador == 1 || numJugador == 3) this.equipo[0] = this.equipo[0] + puntos;
        else this.equipo[1] = this.equipo[1] + puntos;

    }

    /**
     * Devuelve el jugador que ha cortado
     * @return el número del jugador
     */
    public int getJugadorCortar() {
        return this.jugadorCortar;
    }

    /**
     * Actualiza el valor del jugador que ha cortado
     * @param numJugador número del jugador que ha cortado
     */
    public void setJugadorCortar(int numJugador) {
        this.jugadorCortar = numJugador;
    }

    /**
     * Comprueba si la partida ha finalizado
     * @return true si ha finalizado, false en caso contrario
     */
    public boolean finalizado() {
        return this.equipo[0] >= 25 || this.equipo[1] >= 25;
    }

    /**
     * Devuelve el equipo ganodor
     * @return el número del equipo que ha ganado
     */
    public int ganador() {
        if (this.equipo[0] >= 25) return 0;
        return 1;
    }

    /**
     * Reinicia los valores de la mesa
     */
    public void reiniciarPartida() {

        this.baraja = new Baraja();
        this.baraja.barajar();
        this.numJugadorApuestaMasAlta = -1;
        this.apuestas = new int[6][2];
        this.mus = true;
        for (int i = 0; i < 6; i++) {
            this.apuestas[i][0] = i;
            this.apuestas[i][1] = -1;
        }
        this.apuestas[2][1] = 0;
        this.apuestas[4][1] = 0;
        this.apuestas[5][1] = -1;
        this.apuestas[3][1] = -1;
        this.apuestas[0][1] = -1;

        this.numRonda = 0;

        this.pasadas = 0;
        this.manos = new ArrayList<>(3);
        this.jugadorCortar = -1;
        this.juego = false;


    }

    /**
     * Devuelve el número de pasadas
     * @return número de pasdas
     */
    public int getPasadas() {
        return pasadas;
    }

    /**
     * Actualiza el valor de las pasadas
     * @param pasadas Nuevo valor
     */
    public void setPasadas(int pasadas) {
        this.pasadas = pasadas;
    }

    /**
     * Muestra las puntuaciones de los equipos por la salida estandar
     */
    public void showPuntuaciones() {
        System.out.println("Equipo 1: " + this.equipo[0]);
        System.out.println("Equipo 2: " + this.equipo[1]);
    }

    /**
     * Añade una maso a la mesa
     * @param mano mano a añadir
     */
    public void addMano(ArrayList<Carta> mano) {
        this.manos.add(mano);
    }

    /**
     * Devuelve las manos
     * @return {@code ArrayList} con las manos de los jugadores
     */
    public ArrayList<ArrayList<Carta>> getManos() {
        return this.manos;
    }

    /**
     * Comprueba si algun jugador tiene juego
     * @return true en caso afirmativo, false en caso contrario
     */
    public boolean hayJuego() {
        return this.juego;
    }

    /**
     * Actualiza el valor de juego
     * @param juego Nuevo valor de juego
     */
    public void setJuego(boolean juego) {
        this.juego = juego;
    }

    /**
     * Actualiza el valor de mus
     * @param mus Nuevo valor de mus
     */
    public void setMus(boolean mus) {
        this.mus = mus;
    }
}
