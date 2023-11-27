package Red;

import Juego.Mesa;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class AtenderPeticion extends Thread{

    private Socket socket;
    //private HashMap<Mesa, ArrayList<String>> mesas;
    private int numJugador;
    private LinkedList<Mesa> mesas;

    public AtenderPeticion(Socket socket, LinkedList<Mesa> mesas){
        this.socket = socket;
        this.mesas = mesas;
    }

    public void run(){
        try(ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream())){

            if(mesas.size()==0){
                //Inicio del server sin mesas
                Mesa mesa = new Mesa(1);
                mesa.addIp(this.socket.getInetAddress().getHostAddress());
                mesas.add(mesa);
                
                ArrayList<String> players = new ArrayList<>(4);

                while(players.size()!=4){ //Esperamos a que el num de jugadores sea 4
                    Thread.sleep(1000);
                    players = mesa.getIps();
                    System.out.println("Jugadores unidos: "+players.size());
                }

                out.writeObject(1); // Enviamos el numJugador
                out.writeObject(mesa); // Enviamos la mesa
                System.out.println("Se ha unido un jugador");

            }else{

                int i=0;
                int numJugador;
                boolean aniadido = false;
                while(0< mesas.size() && !aniadido ){

                    if(mesas.get(i).getIps().size()<4){

                        numJugador = mesas.get(i).getIps().size();
                        mesas.get(i).addIp(this.socket.getInetAddress().getHostAddress());
                        aniadido=true;


                    }
                    i++;
                }
                i--;
                if (aniadido){
                    ArrayList<String> players = new ArrayList<>(4);
                    numJugador = mesas.get(i).getIps().size();
                    while(mesas.get(i).getIps().size()!=4){ //Esperamos a que el num de jugadores sea 4
                        Thread.sleep(1000);
                        players = mesas.get(i).getIps();
                        System.out.println("Jugadores unidos: "+players.size());
                    }

                    //out.writeObject(players.get(numJugador));// Enviamos la ip de sig jugador
                    out.writeObject(numJugador); // Enviamos el numJugador
                    out.writeObject(mesas.get(i)); // Enviamos la mesa
                    System.out.println("Se ha unido un jugador");
                }else {
                    Mesa mesa = new Mesa(mesas.size()+1);
                    mesa.addIp(this.socket.getInetAddress().getHostAddress());
                    mesas.add(mesa);

                    ArrayList<String> players = new ArrayList<>(4);
                    numJugador = mesas.get(i).getIps().size();
                    while(players.size()!=4){ //Esperamos a que el num de jugadores sea 4
                        Thread.sleep(1000);
                        players = mesa.getIps();
                        System.out.println("Jugadores unidos: "+players.size());
                    }

                    //out.writeObject(players.get(numJugador));// Enviamos la ip de sig jugador
                    out.writeObject(1); // Enviamos el numJugador
                    out.writeObject(mesa); // Enviamos la mesa
                    System.out.println("Se ha unido un jugador");
                }

            }

        }catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

















































    /*

    public void run(){
        try{
            //ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
            System.out.println("Entramos run");
            //BufferedReader bf = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            //String peticion = bf.readLine();
            ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
            String peticion = (String) in.readObject();
            System.out.println("Hemos leido la peticion: " +peticion);



            if(peticion.startsWith("GET ")){
                //Intento de unirse a mesa
                double codMesa = in.readDouble();
                System.out.println("hemos leido el codMesa: "+codMesa);
                Jugador jugador = (Jugador) in.readObject();
                if (existMesa(codMesa,this.mesa)){ //Comprobamos q la mesa existe
                    for (Mesa mesa : this.mesa){
                        if(mesa.getCodMesa()==codMesa){ //intentamos unirnos
                            if(mesa.couldJoin(jugador)) {
                                mesa.joinTable(jugador);
                                numJugador=mesa.getNumPlayers();
                                //System.out.println("Soy el jugador: "+numJugador);
                                out.writeObject("CORRECT");//Indicamos que el jugador se a unido correctamente
                                while(mesa.getNumPlayers()!=4){
                                    Thread.sleep( 1000);
                                    //System.out.println("jugadores en la mesa: "+mesa.getNumPlayers());
                                }
                                out.writeObject("START");
                                System.out.println("Empezamos la partida");

                                //Enviar cartas
                                //System.out.println("Turno antes de enviar cartas: "+mesa.getTurno()+" desde jugador "+numJugador);
                                esperarTurno(mesa);
                                for (int i=0;i<4; i++){
                                    //System.out.println("Carta repartida al jugador "+numJugador);
                                    esperarTurno(mesa);

                                    out.writeObject(mesa.sacarCarta());
                                    mesa.pasarTurno();
                                    //System.out.println("Turno depues de enviar la primera: "+mesa.getTurno());
                                }
                                //Mus
                                System.out.println("Turno:"+mesa.getTurno());
                                esperarTurno(mesa);
                                System.out.println("despues de la espera jugador "+numJugador);
                                String minetras =(String) in.readObject();
                                System.out.println("Sobre mus el jugador "+numJugador+" ha recibido"+minetras);
                                if (in.readBoolean()){
                                    System.out.println("se ha recibido mus del jugador "+numJugador);
                                    mesa.setMus(true);
                                    mesa.pasarTurno();
                                }else {
                                    mesa.setMus(false);
                                    mesa.setTurno(0);
                                }
                                esperarTurno(mesa);
                                if(mesa.isMus()){
                                    out.writeObject("Mus aprobado");
                                }
                                else out.writeObject("Mus denegado");




                            }
                        }
                    }
                }



            } else if (peticion.startsWith("POST ")) {
                //Peticion para crear mesa
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void startGame(){

    }

    private void esperarTurno(Mesa mesa){
        while(this.numJugador!= mesa.getTurno()+1){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }





    public static boolean existMesa(double codMesa,LinkedList<Mesa> mesas){
        for (Mesa mesa : mesas){
            if(mesa.getCodMesa()==codMesa) return true;
        }
        return false;
    }
*/

}
