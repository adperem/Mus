package Juego;

import java.io.Serializable;
import java.util.ArrayList;

public class Mesa implements Serializable {

    private ArrayList<Jugador> jugadores = new ArrayList<>(4);
    private double codMesa;
    private Baraja baraja;
    private int numJugadorApuestaMasAlta;
    private int turno=0;
    private boolean mus=true;
    private ArrayList<String> ip;
    private int numRonda;
    private int apuestas[][]; //Si un jugador iguala, la apuesta pasa a ser negativa
    private int equipo[]; //guarda las puntuaciones
    private int jugadorCortar;


    public Mesa(double codMesa){
        //Creamos una mesa desde 0
        this.baraja = new Baraja();
        this.baraja.barajear();
        this.codMesa = codMesa;
        this.ip=new ArrayList<>(4);
        this.numJugadorApuestaMasAlta = -1;
        this.apuestas= new int[6][2];
        for (int i = 0; i < 6; i++) {
            this.apuestas[i][0]=i;
            this.apuestas[i][1]=-1;
        }
        this.apuestas[0][1]=-1;
        this.numRonda=2;//esto luego hay q cambiarlo
        this.equipo= new int[2];
        this.equipo[0]=0;this.equipo[1]=0;


    }

    public boolean couldJoin(Jugador jugador){
        if (this.jugadores.isEmpty()) return true;
        if(this.jugadores.size()==4) return false;
        for (int i=0;i<this.jugadores.size(); i++) {
            if(this.jugadores.get(i)!=null){
                if (this.jugadores.get(i).getName().equals(jugador.getName())) return false;
            }

        }
        return true;
    }


    public void joinTable(Jugador jugador){
        //PRE: jugador se debe poder unir a la mesa
        //POST: une el jugador a la mesa
        this.jugadores.add(jugador);
    }

    public void leftTable(Jugador jugador){
        //PRE: jugador debe estar en la mesa
        //POST: quita al jugador de la mesa
        for(int i=0;i<4;i++){
            if(this.jugadores.get(i).getName().equals(jugador.getName())) this.jugadores.remove(i);
        }
    }


    public double getCodMesa() {
        return codMesa;
    }

    public int getApuestaMasAlta() {
        return this.apuestas[numRonda][1];
    }
    public int getApuestaMasAlta(int ronda) {
        return this.apuestas[ronda][1];
    }

    public void setApuestaMasAlta(int apuestaMasAlta,int numJugador) {
        this.apuestas[numRonda][1]=apuestaMasAlta;
        this.numJugadorApuestaMasAlta =numJugador;
    }

    public int getNumPlayers(){
        return this.jugadores.size();
    }

    public void pasarTurno(){
        this.turno=(this.turno+1)%4;
    }

    public void cortar(){
        this.mus = false;
    }

    public int getTurno(){
        return this.turno;
    }

    public Carta sacarCarta(){
        return this.baraja.sacarCarta();
    }
    public void setTurno(int turno){this.turno=turno;}


    public boolean isMus() {
        return mus;
    }

    public void setMus(boolean mus) {
        this.mus = mus;
    }

    public void addIp(String ip){
        this.ip.add(ip);
    }

    public ArrayList<String> getIps(){
        return this.ip;
    }

    public boolean puedeApostar(int apuesta){
        return this.apuestas[numRonda][1]<apuesta;
    }

    public int getNumJugadorApuestaMasAlta() {
        return numJugadorApuestaMasAlta;
    }

    public void setNumJugadorApuestaMasAlta(int numJugadorApuestaMasAlta) {
        this.numJugadorApuestaMasAlta = numJugadorApuestaMasAlta;
    }

    public int getNumRonda() {
        return numRonda;
    }

    public void setNumRonda(int numRonda) {
        this.numRonda = numRonda;
    }

    public void addPuntos(int puntos,int numJugador){
        if(numJugador==1 || numJugador==3) this.equipo[0]=this.equipo[0]+puntos;
        else this.equipo[1]=this.equipo[1]+puntos;

    }

    public int getJugadorCortar(){
        return this.jugadorCortar;
    }
    public void setJugadorCortar(int numJugador){
        this.jugadorCortar=numJugador;
    }


}
