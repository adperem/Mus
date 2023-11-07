import java.util.ArrayList;
import java.util.Random;

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
        Carta c = this.baraja.get(0);
        this.baraja.remove(0);
        return c;
    }

    public void barajear(){
        ArrayList<Carta> baraja= new ArrayList<Carta>();
        for (Carta c:this.baraja) {
            baraja.add(this.baraja.get())
        }


    }



}
