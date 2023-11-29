package Red;

import Juego.Mesa;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class AtenderPeticion extends Thread {

    private Socket socket;
    private int numJugador;
    private LinkedList<Mesa> mesasPublicas;
    private LinkedList<Mesa> mesasPrivadas;

    public AtenderPeticion(Socket socket, LinkedList<Mesa> mesasPublicas, LinkedList<Mesa> mesasPrivadas) {
        this.socket = socket;
        this.mesasPublicas = mesasPublicas;
        this.mesasPrivadas = mesasPrivadas;
    }

    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            //recibo el codigo de la mesa. si no hay mesa con ese codigo la


            String accion = in.readLine();

            //Se quiere crear una nueva mesa
            if (accion.equals("NEW")) {
                //Creamos una mesa

                double codMesa = 0;
                for (Mesa mesa : mesasPrivadas) {
                    if (mesa.getCodMesa() == codMesa) codMesa++;
                }


                Mesa mesa = new Mesa(codMesa);
                mesa.addIp(this.socket.getInetAddress().getHostAddress());
                mesasPrivadas.add(mesa);

                while (mesa.getNumPlayers() != 4) { //Esperamos a que el num de jugadores sea 4
                    Thread.sleep(1000);
                    System.out.println("Jugadores unidos: " + mesa.getNumPlayers());
                }
                out.writeDouble(codMesa);
                out.writeInt(1); // Enviamos el numJugador
                out.writeObject(mesa);

            } else if (accion.equals("JOIN")) {
                //nos unimos a una mesa
                double codMesa = in.readDouble();
                Mesa mesa = null;
                while (mesa == null) {
                    for (Mesa mesa1 : mesasPrivadas) {
                        if (mesa1.getCodMesa() == codMesa) mesa = mesa1;
                    }
                }
                if (mesa != null && mesa.getNumPlayers() != 4) {
                    mesa.addIp(this.socket.getInetAddress().getHostAddress());
                    out.writeObject(mesa.getNumPlayers());
                    out.writeObject(mesa);
                } else out.writeObject(0);


            } else if (accion.equals("PLAY")) {

                if (mesasPublicas.size() == 0) {
                    //Inicio del server sin mesas
                    Mesa mesa = new Mesa(1);
                    mesa.addIp(this.socket.getInetAddress().getHostAddress());
                    mesasPublicas.add(mesa);


                    while (mesa.getNumPlayers() != 4) { //Esperamos a que el num de jugadores sea 4
                        Thread.sleep(1000);
                        System.out.println("Jugadores unidos: " + mesa.getNumPlayers());
                    }

                    out.writeObject(1); // Enviamos el numJugador
                    out.writeObject(mesa); // Enviamos la mesa
                    System.out.println("Se ha unido un jugador");

                } else {
                    int i = 0;
                    int numJugador;
                    boolean aniadido = false;
                    while (!aniadido) {

                        if (mesasPublicas.get(i).getNumPlayers() < 4) {

                            numJugador = mesasPublicas.get(i).getNumPlayers();
                            mesasPublicas.get(i).addIp(this.socket.getInetAddress().getHostAddress());
                            aniadido = true;
                        }
                        i++;
                    }
                    i--;
                    if (aniadido) {

                        Mesa mesa = mesasPublicas.get(i);
                        numJugador = mesa.getNumPlayers();

                        while (mesa.getNumPlayers() != 4) { //Esperamos a que el num de jugadores sea 4
                            Thread.sleep(1000);
                            System.out.println("Jugadores unidos: " + mesa.getNumPlayers());
                        }

                        out.writeObject(numJugador); // Enviamos el numJugador
                        out.writeObject(mesa); // Enviamos la mesa

                    } else {
                        Mesa mesa = new Mesa(1);
                        mesa.addIp(this.socket.getInetAddress().getHostAddress());
                        mesasPublicas.add(mesa);


                        while (mesa.getNumPlayers() != 4) { //Esperamos a que el num de jugadores sea 4
                            Thread.sleep(1000);
                            System.out.println("Jugadores unidos: " + mesa.getNumPlayers());
                        }

                        out.writeObject(1); // Enviamos el numJugador
                        out.writeObject(mesa); // Enviamos la mesa
                        System.out.println("Se ha unido un jugador");
                        ;
                    }
                }
            }


        } catch (IOException e) {
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
