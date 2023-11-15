package Red;

import Juego.Carta;
import Juego.Jugador;
import Juego.Mesa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private Jugador jugador=new Jugador();
    public static void main(String[] args){
        Client client = new Client();
        client.jugar();

    }

    private void jugar() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("1 - Nuevo jugador.\n2 - Jugador existente.");
            switch (Integer.parseInt(sc.nextLine())) {
                case 1: //Nuevo jugador
                    newPlayer(sc, this.jugador);
                    break;
                case 2: //Jugador existente

                    while (!Jugador.logIn(sc, this.jugador)) {
                        System.out.println("Usuario o contraseña incorrecta");
                    }

            }

            //Usuario logeado

            System.out.println("Selecccione una opcion:");
            System.out.println("1 - Unirse a una mesa");
            System.out.println("2 - Crear una mesa");

            switch (Integer.parseInt(sc.nextLine())) {
                case 1:
                    System.out.println("Introduzca el codigo de la mesa");

                    //Solicitar mesa y siguiente jugador
                    Socket s = new Socket("localhost", 3333);

                    ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                    int numJugador = (int) in.readObject();
                    ArrayList<Carta> mano = new ArrayList<>(4);
                    if (numJugador == 1) {
                        //si es el primer jugador recibe la mesa del servidor
                        Mesa mesa = (Mesa) in.readObject();
                        String ip = mesa.getIps().get(numJugador);
                        System.out.println("Te has unido a la mesa");
                        for (int i = 0; i < 4; i++) {
                            mano.add(mesa.sacarCarta());
                            enviarMesa(mesa, ip, numJugador);
                            mesa = recibirMesa(numJugador);
                        }

                        mostrarCartas(mano);
                        System.out.println();
                        System.out.println("Seleccione");
                        System.out.println("1 - Mus");
                        System.out.println("2 - Cortar ");
                        switch (Integer.parseInt(sc.nextLine())){
                            case 1:
                                enviarMesa(mesa,ip,numJugador);
                                break;
                            case 2:
                                mesa.setMus(false);
                                enviarMesa(mesa,ip,numJugador);
                                break;
                        }
                        mesa = recibirMesa(numJugador);
                        if(mesa.isMus()){
                            System.out.println("Mus aceptado");
                            System.out.println("Indique separadas por comas las cartes que desea eliminar");
                            String eliminar = sc.nextLine();
                            String[] deleteCartas=eliminar.split(",");
                            for (int j = 3; j >-1 ; j--) {
                                mano.remove(j);
                            }
                            enviarMesa(mesa,ip,numJugador);
                            mus(mano, ip, numJugador);
                        }
                        //grandes(mano,ip,numJugador);
                        System.out.println("se ha cortado");



                    } else {
                        //si es otro jugador recibe la mesa del jugador anterior
                        System.out.println("Te has unido a la mesa");
                        Mesa mesa = recibirMesa(numJugador);
                        String ip;
                        if (numJugador==4){
                            ip = mesa.getIps().get(0);
                        }else {
                            ip = mesa.getIps().get(numJugador);
                        }

                        for (int i = 0; i < 4; i++) {

                            mano.add(mesa.sacarCarta());
                            if (numJugador==4){
                                enviarMesa(mesa,ip,0);
                            }else{
                                enviarMesa(mesa,ip,numJugador);
                            }
                            mesa=recibirMesa(numJugador);
                        }
                        mostrarCartas(mano);
                        System.out.println();
                        System.out.println("Seleccione");
                        System.out.println("1 - Mus");
                        System.out.println("2 - Cortar ");
                        if(sc.nextLine().equals("2")){
                            mesa.setMus(false);
                        }
                        enviarMesa(mesa,ip,numJugador);
                        mesa = recibirMesa(numJugador);
                        if(mesa.isMus()){
                            System.out.println("Mus aceptado");
                            System.out.println("Indique separadas por comas las cartes que desea eliminar");
                            String eliminar = sc.nextLine();
                            String[] deleteCartas=eliminar.split(",");
                            for (int j = 3; j >-1 ; j--) {
                                mano.remove(j);
                            }
                            enviarMesa(mesa,ip,numJugador);
                            mus(mano, ip, numJugador);
                        }
                        System.out.println("se ha cortado");


                        //mus(mano,ip,numJugador);







                    }
                    mostrarCartas(mano);
                    break;

            }
            } catch(IOException | ClassNotFoundException e){
                throw new RuntimeException(e);
            }
    }



        private static void newPlayer (Scanner sc, Jugador jugador){
            //creamos el nuevo jugador
            System.out.println("Introduce el nombre del jugador");
            String nombre = sc.nextLine();
            System.out.println("Introduce la cantidad a depositar");
            double cartera = Double.parseDouble(sc.nextLine());
            System.out.print("Pocesando compra");
            try {
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(1000);
                    System.out.print(".");
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Pago procesa con exito");
            Jugador j = new Jugador(sc.nextLine(), cartera);

            //añadimos el nuevo jugador a la base de datos

        }


        private static void joinGame ( double codMesa, Jugador jugador){
            //Envia al servidor una peticion para entrar a la mesa con codMesa = codMesa
            try (Socket s = new Socket("localhost", 3333)) {

                //PrintStream p = new PrintStream(s.getOutputStream());
                //p.println("mesaje desde cliente");


                //p.println("GET "); //Indicamos que queremos unirnos a una mesa
                //p.println(codMesa); //Enviamos el codigo de mesa
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                out.writeObject("GET ");
                out.writeDouble(codMesa);
                out.writeObject(jugador); //Enviamos el jugador

                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                String accion = (String) in.readObject();
                if (accion.equals("CORRECT")) {
                    ArrayList<Carta> mano = new ArrayList<>(4);
                    System.out.println("Te has unido a la mesa");
                    // Nos hemos unido a la mesa
                    accion = (String) in.readObject();
                    if (accion.equals("START")) {
                        // Empezamos a jugar
                        System.out.println("Empezamos juego");

                        for (int i = 0; i < 4; i++) {
                            mano.add((Carta) in.readObject());
                        }
                    /*
                    mostrarCartas(mano);
                    System.out.println("Seleccione:\n1 - Mus\n2 -Cortar");
                    switch (Integer.parseInt(sc.nextLine())){
                        case 1:
                            out.writeObject("true");
                            break;
                        case 2:
                            out.writeObject("false");
                            break;
                    }
                    System.out.println((String) in.readObject());
                    */


                    }


                }


            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }


        private static void createGame () {
            //Envia al servidor la orden de crear un mesa nueva
        }


        public static void mostrarCartas (ArrayList < Carta > cartas) {
            System.out.println("-------------------");
            System.out.println("Tus cartas");
            int i=0;
            for (Carta carta : cartas) {
                System.out.print(i+") ");
                carta.mostrarCarta();
            }
            System.out.println("-------------------");

        }


        private static void conectToNextPlayer (Mesa mesa, String ip,int numJugador){
            try (Socket s = new Socket("localhost", 3333);
                 ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {

                mesa = (Mesa) in.readObject();
                ip = (String) in.readObject();

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }


        private static void play (Mesa mesa, String ip,int numJugador){
            try (ServerSocket ss = new ServerSocket(1234 + numJugador)) {
                ArrayList<Carta> mano = new ArrayList<>(4);

                Socket sRecibir = ss.accept();
                Socket sEnviar = new Socket(ip, 1234);

                ObjectInputStream in = new ObjectInputStream(sRecibir.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(sEnviar.getOutputStream());

                // *Primera carta* //
                if (numJugador != 1) {
                    mesa = (Mesa) in.readObject();
                    mano.add(mesa.sacarCarta());
                } else {
                    mano.add(mesa.sacarCarta());
                }
                out.writeObject(mesa);

                // *Segunda carta* //
                mesa = (Mesa) in.readObject();
                mano.add(mesa.sacarCarta());
                out.writeObject(mesa);

                // *Tercera carta* //
                mesa = (Mesa) in.readObject();
                mano.add(mesa.sacarCarta());
                out.writeObject(mesa);

                // *Cuarta carta* //
                mesa = (Mesa) in.readObject();
                mano.add(mesa.sacarCarta());
                out.writeObject(mesa);

                mostrarCartas(mano);


            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }


        public static Mesa recibirMesa ( int numJugador){

            try (ServerSocket ss = new ServerSocket(1234 + numJugador)) {
                int a=1234 + numJugador;
                //System.out.println(numJugador+" recibe por "+a);
                Socket sRecibir = ss.accept();

                ObjectInputStream in = new ObjectInputStream(sRecibir.getInputStream());
                Mesa mesa =(Mesa) in.readObject();
                return mesa;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static void enviarMesa (Mesa mesa, String ip,int numJugador){
        int a =1234+numJugador+1;
            //System.out.println(numJugador+" envia a "+a);
            if(numJugador==4){
                numJugador=0;
            }

            try (Socket sEnviar = new Socket(ip, 1234 + numJugador + 1)) {


                ObjectOutputStream out = new ObjectOutputStream(sEnviar.getOutputStream());

                out.writeObject(mesa);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void mus(ArrayList<Carta> mano,String ip,int numJugador){

            int numJugadorEnviar=numJugador;
            if(numJugador==4){
                numJugadorEnviar=0;
            }
            Scanner sc = new Scanner(System.in);
            Mesa mesa;
            int numCartas= mano.size();
            for (int i = 0; i < 4- numCartas; i++) {
                mesa = recibirMesa(numJugador);
                mano.add(mesa.sacarCarta());
                enviarMesa(mesa,ip,numJugadorEnviar);
            }

            mesa = recibirMesa(numJugador);
            if(mesa.isMus()){
                mostrarCartas(mano);

                System.out.println();
                System.out.println("Seleccione un opcion");
                System.out.println("1 - Mus");
                System.out.println("2 - Cortar");
                switch (Integer.parseInt(sc.nextLine())){
                    case 1:
                        enviarMesa(mesa,ip,numJugador);
                        break;
                    case 2:
                        mesa.setMus(false);
                        enviarMesa(mesa,ip,numJugador);
                        break;
                }
            }else {
                enviarMesa(mesa,ip,numJugadorEnviar);
            }
            mesa = recibirMesa(numJugador);
            if (mesa.isMus()){
                System.out.println("Mus aceptado");
                System.out.println("Indique separadas por comas las cartes que desea eliminar");
                String eliminar = sc.nextLine();
                String[] deleteCartas=eliminar.split(",");
                for (int j = 3; j >-1 ; j--) {
                    mano.remove(j);
                }
                enviarMesa(mesa,ip,numJugadorEnviar);
                mus(mano, ip, numJugador);
            } else {
                System.out.println("mus denegado");
                enviarMesa(mesa,ip,numJugadorEnviar);
            }
        }

        public static void  grandes(ArrayList<Carta> mano,String ip,int numJugador){
            Mesa mesa = recibirMesa(numJugador);
            if(numJugador==1 && mesa.getApuestaMasAlta()==0.0){

            }
            if(numJugador==4){
                if(mesa.getApuestaMasAlta()==0.0){
                    //enviar al jugador 1
                }else if (Double.toString(mesa.getApuestaMasAlta()).substring(Double.toString(mesa.getApuestaMasAlta()).indexOf(".")).equals("2")){
                    enviarMesa(mesa,ip,numJugador); //Enviar al jugador 1
                }else {
                    System.out.println("1 - Envidar\n2 - Pasar\n3 - Apostar");
                    Scanner sc = new Scanner(System.in);
                    switch (Integer.parseInt(sc.nextLine())){
                        case 1:
                            System.out.println("Apostamos 2");
                            break;
                        case 2:
                            System.out.println("Pasamos");
                            break;
                        case 3:
                            System.out.println("Apostamos");
                            break;
                    }
                }
            } else if (numJugador == 2) {

                if(Double.toString(mesa.getApuestaMasAlta()).substring(Double.toString(mesa.getApuestaMasAlta()).indexOf(".")).equals("1")){

                }


                System.out.println("1 - Envidar\n2 - Pasar\n3 - Apostar");
                Scanner sc = new Scanner(System.in);
                switch (Integer.parseInt(sc.nextLine())){
                    case 1:
                        System.out.println("Apostamos 2");
                        break;
                    case 2:
                        System.out.println("Pasamos");
                        break;
                    case 3:
                        System.out.println("Apostamos");
                        break;
                }
            }
        }










}
