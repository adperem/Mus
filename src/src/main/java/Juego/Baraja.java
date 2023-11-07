package Juego;

import java.util.ArrayList;
import java.util.Collections;

public class Baraja {

    private ArrayList<Carta> baraja= new ArrayList<Carta>();

    public Baraja(){
        for (int i=0; i<4; i++){
            for (int ii=0; ii<8; ii++){
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



}
