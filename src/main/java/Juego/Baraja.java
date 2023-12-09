package Juego;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represente una baraja española de 40 cartas.
 *
 * @author Adrián Pérez Moreno
 */
public class Baraja implements Serializable {

    /**
     * {@code ArrayList} que representa la baraja
     */
    private ArrayList<Carta> baraja = new ArrayList<>();

    /**
     * Inicializa una baraja
     */
    public Baraja() {
        for (int i = 0; i < 4; i++) {
            for (int ii = 1; ii < 8; ii++) {
                this.baraja.add(new Carta(ii, Carta.Palo.getPalo(i)));
            }
            this.baraja.add(new Carta(10, Carta.Palo.getPalo(i)));
            this.baraja.add(new Carta(11, Carta.Palo.getPalo(i)));
            this.baraja.add(new Carta(12, Carta.Palo.getPalo(i)));
        }
    }

    /**
     * Saca un carta de la baraja
     *
     * @return La primera carta de la baraja
     */
    public Carta sacarCarta() {
        if (this.baraja.size() > 0) {
            Carta c = this.baraja.get(0);
            this.baraja.remove(0);
            return c;
        }
        return null;
    }

    /**
     * Baraja las cartas
     */
    public void barajar() {
        Collections.shuffle(this.baraja);
    }

    /**
     * Devulve la carta más alta de la mano.
     *
     * @param mano {@code ArrayList} que representa la mano de un Jugador
     * @return La Carta más alta de la mano
     */
    public static Carta getCartaAlta(ArrayList<Carta> mano) {
        Carta mayor = mano.get(0);
        for (Carta c : mano) {
            if (c.getNumero() > mayor.getNumero()) mayor = c;
        }
        return mayor;
    }

    /**
     * Devulve la carta más baja de la mano.
     *
     * @param mano {@code ArrayList} que representa la mano de un Jugador
     * @return La Carta más baja de la mano
     */
    public static Carta getCartaBaja(ArrayList<Carta> mano) {
        Carta menor = mano.get(0);
        for (Carta c : mano) {
            if (c.getNumero() > menor.getNumero()) menor = c;
        }
        return menor;
    }

}
