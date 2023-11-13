package Juego;

import java.io.Serializable;
import java.util.ArrayList;

public class Mesa implements Serializable {

    private ArrayList<Jugador> jugadores = new ArrayList<>(4);
    private double codMesa;
    private Baraja baraja;
    private float apuestaMasAlta;
    private int turno=0;
    private boolean mus=true;
    private ArrayList<String> ip;


    public Mesa(double codMesa){
        //Creamos una mesa desde 0
        this.baraja = new Baraja();
        this.codMesa = codMesa;
        this.ip=new ArrayList<>(4);

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

    public double getApuestaMasAlta() {
        return apuestaMasAlta;
    }

    public void setApuestaMasAlta(float apuestaMasAlta) {
        this.apuestaMasAlta = apuestaMasAlta;
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
}
