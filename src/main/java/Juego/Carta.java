package Juego;

import java.io.Serializable;

/**
 * Representa una carta de la barja española
 *
 * @author Adrián Pérez Moreno
 */
public class Carta implements Serializable {

    public enum Palo {

        OROS("Oros", 0), COPAS("Copas", 1), BASTOS("Bastos", 2), ESPADAS("Espadas", 3);
        private String palo;
        private int i;

        Palo(String palo, int i) {
            this.palo = palo;
            this.i = i;
        }

        public static Palo getPalo(int i) {
            switch (i) {
                case 0:
                    return OROS;
                case 1:
                    return COPAS;
                case 2:
                    return BASTOS;
                case 3:
                    return ESPADAS;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }
        }

        public boolean equals(Palo palo) {
            return this.palo.equals(palo.palo);
        }
    }

    /**
     * Número de la carta
     */
    private int numero;
    /**
     * Palo de la carta
     */
    private Palo palo;

    /**
     * Inicializa un carta con un número y un palo
     *
     * @param num  Número de la carta
     * @param palo Palo de la carta
     */
    public Carta(int num, Palo palo) {
        this.numero = num;
        this.palo = palo;
    }

    /**
     * Devuelve el número de la Carta
     *
     * @return El número de la Carta
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Devuelve el palo de la Carta
     *
     * @return El palo de la Carta
     */
    public Palo getPalo() {
        return palo;
    }

    /**
     * Muestra por la salida estandar el número y el palo de la carta
     */
    public void mostrarCarta() {
        System.out.println(this.numero + " de " + this.palo);
    }

    /**
     * Compara si dos cartas son iguales
     *
     * @param carta Carta con la cual se desea comparar
     * @return true si son iguales, false en caso contrario
     */
    public boolean equals(Carta carta) {
        if (this.getPalo().equals(carta.getPalo()) && this.getNumero() == carta.getNumero()) {
            return true;
        }
        return false;
    }
}
