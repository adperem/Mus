package Red;

import Juego.Jugador;
import Juego.Mesa;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class AtenderPeticion extends Thread{

    private Socket socket;
    private LinkedList<Mesa> mesa;

    public AtenderPeticion(Socket socket, LinkedList<Mesa> mesa){
        this.socket = socket;
        this.mesa=mesa;
    }

    public void run(){
        try(ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream()) ){

            String peticion = in.readLine();
            if(peticion.startsWith("GET ")){
                //Intento de unirse a mesa
                double codMesa = Double.parseDouble(in.readLine());
                Jugador jugador = (Jugador) in.readObject();
                if (existMesa(codMesa,this.mesa)){ //Comprobamos q la mesa existe
                    for (Mesa mesa : this.mesa){
                        if(mesa.getCodMesa()==codMesa){ //intentamos unirnos
                            if(mesa.couldJoin(jugador)) {
                                mesa.joinTable(jugador);
                                out.writeObject(mesa);
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
        }
    }

    public static boolean existMesa(double codMesa,LinkedList<Mesa> mesas){
        for (Mesa mesa : mesas){
            if(mesa.getCodMesa()==codMesa) return true;
        }
        return false;
    }


}
