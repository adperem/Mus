package Red;

import Juego.Mesa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try(BufferedReader bf = new BufferedReader( new InputStreamReader(socket.getInputStream()))){
            String peticion = bf.readLine();
            if(peticion.startsWith("GET ")){
                //Intento de unirse a mesa
                double codMesa = Double.parseDouble(bf.readLine());
                String userName = bf.readLine();
                if (existMesa(codMesa,this.mesa)){ //Comprobamos q la mesa existe
                    for (Mesa mesa : this.mesa){
                        if(mesa.getCodMesa()==codMesa){ //intentamos unirnos
                            if(mesa.couldJoin(userName))
                        }
                    }
                }



            } else if (peticion.startsWith("POST ")) {
                //Peticion para crear mesa
            }
        } catch (IOException e) {
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
