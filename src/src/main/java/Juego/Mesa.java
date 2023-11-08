package Juego;

import java.util.ArrayList;

public class Mesa {

    private ArrayList<Jugador> jugadores = new ArrayList<>(4);
    private double codMesa;
    private Baraja baraja;

    public Mesa(double codMesa){
        //Creamos una mesa desde 0
        this.baraja = new Baraja();
        this.codMesa = codMesa;

    }

    public boolean couldJoin(Jugador jugador){
        if (this.jugadores.isEmpty()) return true;
        if(this.jugadores.size()==4) return false;
        for (Jugador j:this.jugadores) {
            if (j.getName().equals(jugador.getName())) return false;
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
}
