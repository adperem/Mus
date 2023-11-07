package Juego;



import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    private Jugador jugador;
    public static void main(String[] args){
        Game game = new Game();
        game.jugar();

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
                    joinGame(Integer.parseInt(sc.nextLine()));
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



    private static void joinGame(int codMesa){
        //Envia al servidor una peticion para entrar a la mesa con codMesa = codMesa

    }

    private static void createGame(){
        //Envia al servidor la orden de crear un mesa nueva
    }







}
