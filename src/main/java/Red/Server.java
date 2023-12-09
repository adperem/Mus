package Red;

import Juego.Mesa;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Representa un lobby de Mus
 *
 * @author Adrián Pérez Moreno
 */

public class Server implements Serializable {
    /**
     * Lista de partidas publicas
     */
    static LinkedList<Mesa> partidasPublicas;
    /**
     * Lista de partidas privadas
     */
    static LinkedList<Mesa> partidasPrivadas;


    public static void main(String[] args) {

        partidasPublicas = new LinkedList<>();
        partidasPrivadas = new LinkedList<>();


        try {
            ServerSocket ss = new ServerSocket(3333);
            ExecutorService pool = Executors.newCachedThreadPool();
            while (true) {
                try {
                    Socket s = ss.accept();

                    pool.execute(new AtenderPeticion(s, partidasPublicas, partidasPrivadas));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
