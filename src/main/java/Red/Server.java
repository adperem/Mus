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

        LinkedList<Mesa> partidasPublicas = new LinkedList<>();
        LinkedList<Mesa> partidasPrivadas = new LinkedList<>();


        try{
            ServerSocket ss = new ServerSocket(3333);
            ExecutorService pool = Executors.newCachedThreadPool();
            while (true){
                try {
                    Socket s = ss.accept();

                    pool.execute(new AtenderPeticion(s,partidasPublicas,partidasPrivadas));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
