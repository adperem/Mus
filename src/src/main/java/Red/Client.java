package Red;

import Juego.Carta;
import Juego.Jugador;


import java.io.*;
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

    private void jugar(){
        try(Scanner sc = new Scanner(System.in)){
            System.out.println("1 - Nuevo jugador.\n2 - Jugador existente.");
            switch (Integer.parseInt(sc.nextLine())){
                case 1: //Nuevo jugador
                    newPlayer(sc,this.jugador);
                    break;
                case 2: //Jugador existente

                    while(!Jugador.logIn(sc,this.jugador)){
                        System.out.println("Usuario o contraseña incorrecta");
                    }

            }

            //Usuario logeado

            System.out.println("Selecccione una opcion:");
            System.out.println("1 - Unirse a una mesa");
            System.out.println("2 - Crear una mesa");

            switch (Integer.parseInt(sc.nextLine())){
                case 1 :
                    System.out.println("Introduzca el codigo de la mesa");
                    joinGame(Double.parseDouble(sc.nextLine()),this.jugador);
                    break;
                case 2:
                    createGame();
                    break;
            }
        }
    }


    private static void newPlayer(Scanner sc,Jugador jugador){
        //creamos el nuevo jugador
        System.out.println("Introduce el nombre del jugador");
        String nombre = sc.nextLine();
        System.out.println("Introduce la cantidad a depositar");
        double cartera = Double.parseDouble(sc.nextLine());
        System.out.print("Pocesando compra");
        try {
            for (int i=0; i<3; i++){
                Thread.sleep( 1000);
                System.out.print(".");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Pago procesa con exito");
        Jugador j = new Jugador(sc.nextLine(),cartera);

        //añadimos el nuevo jugador a la base de datos

    }



    private static void joinGame(double codMesa,Jugador jugador)  {
        //Envia al servidor una peticion para entrar a la mesa con codMesa = codMesa
        try(Socket s = new Socket("localhost",3333)){

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
            if(accion.equals("CORRECT")){
                ArrayList<Carta> mano = new ArrayList<>(4);
                System.out.println("Te has unido a la mesa");
                // Nos hemos unido a la mesa
                accion = (String) in.readObject();
                if (accion.equals("START")){
                    // Empezamos a jugar
                    System.out.println("Empezamos juego");

                    for (int i=0; i<4;i++){
                        mano.add((Carta) in.readObject());
                    }

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




    private static void createGame(){
        //Envia al servidor la orden de crear un mesa nueva
    }


    public static void mostrarCartas(ArrayList<Carta> cartas){
        System.out.println("-------------------");
        System.out.println("Tus cartas");
        for(Carta carta: cartas){
            carta.mostrarCarta();
        }
        System.out.println("-------------------");

    }


}
