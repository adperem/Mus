package Red;

import Juego.Jugador;
import Juego.Mesa;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class AtenderPeticion extends Thread implements Serializable{

    private Socket socket;
    private LinkedList<Mesa> mesa;

    public AtenderPeticion(Socket socket, LinkedList<Mesa> mesa){
        this.socket = socket;
        this.mesa=mesa;
    }

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
                                out.writeObject("CORRECT"); //Indicamos que el jugador se a unido correctamente
                                while(mesa.getNumPlayers()!=4){
                                    Thread.sleep( 1000);
                                    System.out.println("jugadores en la mesa: "+mesa.getNumPlayers());
                                }
                                out.writeObject("START");
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





    public static boolean existMesa(double codMesa,LinkedList<Mesa> mesas){
        for (Mesa mesa : mesas){
            if(mesa.getCodMesa()==codMesa) return true;
        }
        return false;
    }


}
