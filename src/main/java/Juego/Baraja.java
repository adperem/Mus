package Juego;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Baraja implements Serializable {

    private ArrayList<Carta> baraja= new ArrayList<>();

    public Baraja(){
        for (int i=0; i<4; i++){
            for (int ii=1; ii<9; ii++){
                this.baraja.add(new Carta(ii, Carta.Palo.getPalo(i)));
            }
            this.baraja.add(new Carta(10, Carta.Palo.getPalo(i)));
            this.baraja.add(new Carta(11, Carta.Palo.getPalo(i)));
            this.baraja.add(new Carta(12, Carta.Palo.getPalo(i)));
        }
    }

    public Carta sacarCarta(){
        if(this.baraja.size()>0){
            Carta c = this.baraja.get(0);
            this.baraja.remove(0);
            return c;
        }
        return null;
    }

    public void barajear(){
        Collections.shuffle(this.baraja);
    }

    public static Carta getCartaAlta(ArrayList<Carta> mano){
        Carta mayor=mano.get(0);
        for (Carta c : mano){
            if (c.getNumero()>mayor.getNumero()) mayor=c;
        }
        return mayor;
    }

    public static Carta getCartaBaja(ArrayList<Carta> mano){
        Carta menor=mano.get(0);
        for (Carta c : mano){
            if (c.getNumero()>menor.getNumero()) menor=c;
        }
        return menor;
    }

}
