package Juego;

import java.io.Serializable;

public class Carta implements Serializable {

    public enum Palo{
        OROS("Oros",0),COPAS("Copas",1),BASTOS("Bastos",2),ESPADAS("Espadas",3);
        private String palo;
        private int i;

        Palo(String palo, int i) {
            this.palo=palo;
            this.i=i;
        }

        public static Palo getPalo(int i) {
            switch (i){
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

        public boolean equals(Palo palo){
            return this.palo.equals(palo.palo);
        }
    }


    private int numero;
    private Palo palo;

    public Carta(int num,Palo palo){
        this.numero=num;
        this.palo=palo;
    }


    public int getNumero() {
        return numero;
    }

    public Palo getPalo() {
        return palo;
    }

    public void mostrarCarta(){
        System.out.println(this.numero+" de "+this.palo);
    }



    public boolean equals(Carta carta) {
        if (this.getPalo().equals(carta.getPalo()) && this.getNumero()==carta.getNumero()){
            return true;
        }
        return false;
    }
}
