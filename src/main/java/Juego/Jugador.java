package Juego;

import java.io.Serializable;

/**
 * Representa a un jugador en el mundo real
 *
 * @author Adrián Pérez Moreno
 */


public class Jugador implements Serializable {
    /**
     * Nombre del jugador
     */
    private String name;
    /**
     * Creditos del jugador
     */
    private double cartera;
    /**
     * Contraseña del jugador
     */
    private String passwd;

    /**
     * Inicializa un Jugador
     *
     * @param name    Nombre del jugador
     * @param cartera Creditos del jugador
     * @param passwd  Contraseña del jugador
     */
    public Jugador(String name, double cartera, String passwd) {
        this.name = name;
        this.cartera = cartera;
        this.passwd = passwd;
    }

    /**
     * Devuelve el nombre del jugadro
     *
     * @return El nombre de jugadro
     */
    public String getName() {
        return this.name;
    }

    /**
     * Acutualiza los creditos del jugador
     *
     * @param cartera Nueva cantidad de creditos
     */
    public void setCartera(double cartera) {
        this.cartera = cartera;
    }

    /**
     * Devuelve el numero de creditos del jugador
     *
     * @return Creditos disponibles
     */
    public double getCartera() {
        return cartera;
    }

    /**
     * Devuelve la contraseña del jugador
     *
     * @return Contraseña del jugadro
     */
    public String getPasswd() {
        return passwd;
    }
}
