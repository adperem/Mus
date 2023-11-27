package Red;

import Juego.Mesa;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Serializable {


    public static void main(String[] args) {
        //ArrayList<String> players = new ArrayList<>(4);
        //HashMap<Mesa, ArrayList<String>> partidas = new HashMap<>();
        LinkedList<Mesa> partidas = new LinkedList<>();
        //LinkedList<Mesa,String> partidas = new LinkedList<>();


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
