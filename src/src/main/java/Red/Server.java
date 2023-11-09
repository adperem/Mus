package Red;

import Juego.Mesa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Serializable {


    public static void main(String[] args) {

        LinkedList<Mesa> partidas = new LinkedList<>();
        partidas.add(new Mesa(1));

        try(ServerSocket ss = new ServerSocket(3333)) {
            ExecutorService pool = Executors.newCachedThreadPool();
            while (true){
                try {
                    Socket s = ss.accept();
                    pool.execute(new AtenderPeticion(s,partidas));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
