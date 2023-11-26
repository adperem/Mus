package Red;

import Juego.Baraja;
import Juego.Carta;
import Juego.Jugador;
import Juego.Mesa;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Client {

    private Jugador jugador = new Jugador();

    public static void main(String[] args) {
        Client client = new Client();
        client.jugar();

    }

    private void jugar() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("1 - Nuevo jugador.\n2 - Jugador existente.");
            int seleccion = Integer.parseInt(sc.nextLine());
            while (seleccion != 1 && seleccion != 2) {
                System.out.println("1 - Nuevo jugador.\n2 - Jugador existente.");
                seleccion = Integer.parseInt(sc.nextLine());
            }

            switch (seleccion) {
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
            seleccion = Integer.parseInt(sc.nextLine());
            while (seleccion != 1 && seleccion != 2) {
                System.out.println("Selecccione una opcion:");
                System.out.println("1 - Unirse a una mesa");
                System.out.println("2 - Crear una mesa");
                seleccion = Integer.parseInt(sc.nextLine());
            }

            switch (seleccion) {
                case 1:
                    //System.out.println("Introduzca el codigo de la mesa");

                    //Solicitar mesa y siguiente jugador
                    Socket s = new Socket("localhost", 3333);

                    ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                    int numJugador = (int) in.readObject();
                    ArrayList<Carta> mano = new ArrayList<>(4);

                    if (numJugador == 1) {
                        //si es el primer jugador recibe la mesa del servidor
                        Mesa mesa = (Mesa) in.readObject();
                        String ipJ1 = mesa.getIps().get(0);
                        String ip = mesa.getIps().get(numJugador);
                        System.out.println("Te has unido a la mesa");
                        for (int i = 0; i < 4; i++) {
                            mano.add(mesa.sacarCarta());
                            enviarMesa(mesa, ip, numJugador);
                            mesa = recibirMesa(numJugador);
                        }

                        mostrarCartas(mano);
                        System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                        seleccion = Integer.parseInt(sc.nextLine());
                        while (seleccion != 1 && seleccion != 2) {
                            System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                            seleccion = Integer.parseInt(sc.nextLine());
                        }
                        switch (seleccion) {
                            case 1:
                                enviarMesa(mesa, ip, numJugador);
                                break;
                            case 2:
                                mesa.setMus(false);
                                mesa.setJugadorCortar(numJugador);
                                enviarMesa(mesa, ip, numJugador);
                                break;
                        }
                        mesa = recibirMesa(numJugador);
                        if (mesa.isMus()) {
                            System.out.println("Mus aceptado");
                            System.out.println("Indique separadas por comas las cartes que desea eliminar");
                            String eliminar = sc.nextLine();
                            String[] deleteCartas = eliminar.split(",");
                            ArrayList<Carta> delete = new ArrayList<>();
                            for (int i = 0; i < deleteCartas.length; i++)
                                delete.add(mano.get(Integer.parseInt(deleteCartas[i])));
                            for (int i = 0; i < deleteCartas.length; i++) {
                                mano.remove(delete.get(i));
                            }
                            enviarMesa(mesa, ip, numJugador);
                            mus(mano, ip, numJugador);
                        } else {
                            System.out.println("Se ha cortado");
                            mesa.setNumRonda(2);
                            mesa.setNumJugadorApuestaMasAlta(-1);
                            enviarMesa(mesa, ip, numJugador);
                        }

                        while (true) {
                            System.out.println("Pasamos a grandes");
                            grandes(mano, ip, numJugador);
                            System.out.println("Pasamos a chicas");
                            chicas(mano, ip, numJugador);
                            System.out.println("Pasamos a pares");
                            pares(mano, ip, numJugador);
                            System.out.println("Pasamos a juego");
                            mesa=juego(mano, ip, numJugador);
                            System.out.println("Asignamos puntos");
                            asignarPuntos(mano, ipJ1, numJugador,mesa);
                        }


                    } else {
                        //si es otro jugador recibe la mesa del jugador anterior
                        System.out.println("Te has unido a la mesa");
                        Mesa mesa = recibirMesa(numJugador);
                        String ipJ1 = mesa.getIps().get(0);
                        String ip;
                        if (numJugador == 4) {
                            ip = mesa.getIps().get(0);
                        } else {
                            ip = mesa.getIps().get(numJugador);
                        }

                        for (int i = 0; i < 4; i++) {

                            mano.add(mesa.sacarCarta());
                            enviarMesa(mesa, ip, numJugador);
                            mesa = recibirMesa(numJugador);
                        }
                        mostrarCartas(mano);
                        if (mesa.isMus()) {
                            System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                            seleccion = Integer.parseInt(sc.nextLine());
                            while (seleccion != 1 && seleccion != 2) {
                                System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                                seleccion = Integer.parseInt(sc.nextLine());
                            }
                            switch (seleccion) {
                                case 1:
                                    //enviarMesa(mesa, ip, numJugador);
                                    break;
                                case 2:
                                    mesa.setMus(false);
                                    mesa.setJugadorCortar(numJugador);
                                    //enviarMesa(mesa, ip, numJugador);
                                    break;
                            }
                        }
                        enviarMesa(mesa, ip, numJugador);

                        mesa = recibirMesa(numJugador);
                        if (mesa.isMus()) {
                            System.out.println("Mus aceptado");
                            System.out.println("Indique separadas por comas las cartes que desea eliminar");
                            String eliminar = sc.nextLine();
                            String[] deleteCartas = eliminar.split(",");
                            ArrayList<Carta> delete = new ArrayList<>();
                            for (int i = 0; i < deleteCartas.length; i++)
                                delete.add(mano.get(Integer.parseInt(deleteCartas[i])));
                            for (int i = 0; i < deleteCartas.length; i++) {
                                mano.remove(delete.get(i));
                            }
                            enviarMesa(mesa, ip, numJugador);
                            mus(mano, ip, numJugador);
                        } else {
                            System.out.println("Se ha cortado");
                            mesa.setNumRonda(2);
                            mesa.setNumJugadorApuestaMasAlta(-1);
                            enviarMesa(mesa, ip, numJugador);
                        }

                        while (true) {
                            System.out.println("Pasamos a grandes");
                            grandes(mano, ip, numJugador);
                            System.out.println("Pasamos a chicas");
                            chicas(mano, ip, numJugador);
                            System.out.println("Pasamos a pares");
                            pares(mano, ip, numJugador);
                            System.out.println("Pasamos a juego");
                            juego(mano,ip,numJugador);
                            System.out.println("Asignamos puntos");
                            asignarPuntos(mano, ipJ1, numJugador,null);
                        }


                    }


            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private static void newPlayer(Scanner sc, Jugador jugador) {
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


    private static void createGame() {
        //Envia al servidor la orden de crear un mesa nueva
    }


    public static void mostrarCartas(ArrayList<Carta> cartas) {
        System.out.println("-------------------");
        System.out.println("Tus cartas");
        int i = 0;
        for (Carta carta : cartas) {
            System.out.print(i + ") ");
            carta.mostrarCarta();
            i++;
        }
        System.out.println("-------------------");

    }


    public static Mesa recibirMesa(int numJugador) {

        try (ServerSocket ss = new ServerSocket(1234 + numJugador)) {
            int a = 1234 + numJugador;
            //System.out.println(numJugador+" recibe por "+a);
            Socket sRecibir = ss.accept();

            ObjectInputStream in = new ObjectInputStream(sRecibir.getInputStream());
            Mesa mesa = (Mesa) in.readObject();
            return mesa;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void enviarMesa(Mesa mesa, String ip, int numJugador) {

        if (numJugador == 4) numJugador = 0;
        int a = 1234 + numJugador + 1;
        //System.out.println(numJugador+" envia a "+a);


        try (Socket sEnviar = new Socket(ip, 1234 + numJugador + 1)) {


            ObjectOutputStream out = new ObjectOutputStream(sEnviar.getOutputStream());

            out.writeObject(mesa);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al enviar al puerto: " + a);
        }
    }

    public static void mus(ArrayList<Carta> mano, String ip, int numJugador) {


        Scanner sc = new Scanner(System.in);
        Mesa mesa;

        for (int i = 0; i < 4; i++) {
            mesa = recibirMesa(numJugador);
            if (mano.size() < 4) mano.add(mesa.sacarCarta());
            enviarMesa(mesa, ip, numJugador);
        }

        mesa = recibirMesa(numJugador);
        if (mesa.isMus()) {
            mostrarCartas(mano);

            System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
            int seleccion = Integer.parseInt(sc.nextLine());
            while (seleccion != 1 && seleccion != 2) {
                System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                seleccion = Integer.parseInt(sc.nextLine());
            }
            switch (seleccion) {
                case 1:
                    enviarMesa(mesa, ip, numJugador);
                    break;
                case 2:
                    mesa.setMus(false);
                    mesa.setJugadorCortar(numJugador);
                    enviarMesa(mesa, ip, numJugador);
                    break;
            }
        } else {
            enviarMesa(mesa, ip, numJugador);
        }
        mesa = recibirMesa(numJugador);
        if (mesa.isMus()) {
            System.out.println("Mus aceptado");
            System.out.println("Indique separadas por comas las cartes que desea eliminar");
            String eliminar = sc.nextLine();
            String[] deleteCartas = eliminar.split(",");
            ArrayList<Carta> delete = new ArrayList<>();
            for (int i = 0; i < deleteCartas.length; i++) delete.add(mano.get(Integer.parseInt(deleteCartas[i])));
            for (int i = 0; i < deleteCartas.length; i++) {
                mano.remove(delete.get(i));
            }
            enviarMesa(mesa, ip, numJugador);
            mus(mano, ip, numJugador);
        } else {
            System.out.println("Se ha cortado");
            mesa.setNumRonda(2);
            mesa.setNumJugadorApuestaMasAlta(-1);
            enviarMesa(mesa, ip, numJugador);
        }
    }


    public static void grandes(ArrayList<Carta> mano, String ip, int numJugador) {

        Mesa mesa = recibirMesa(numJugador);
        if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 2 && mesa.getPasadas() != 4) {
            if (mesa.getApuestaMasAlta() > 0)
                System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " ***");


            if (mesa.getJugadorCortar() == numJugador) {
                //Le toca al que ha cortado
                realizarApuesta(mesa, numJugador);
                mesa.setJugadorCortar(-1);
                enviarMesa(mesa, ip, numJugador);
                grandes(mano, ip, numJugador);
            } else {
                if (mesa.getJugadorCortar() == -1) {

                    if (numJugador == 1) {
                        //if (mesa.getApuestaMasAlta() == -1) {
                        //Primera ronda todavia no se ha podido apostar
                        //     mostrarCartas(mano);
                        //    realizarApuesta(mesa, numJugador);
                        // } else {
                        //alguien ha apostado
                        if (mesa.getNumJugadorApuestaMasAlta() != 3 && mesa.getApuestaMasAlta() >= 0) {
                            System.out.println("Grandes");
                            mostrarCartas(mano);
                            realizarApuesta(mesa, numJugador);
                        } else {
                            //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                        }
                        //}

                    } else if (numJugador == 2) {

                        if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                            System.out.println("Grandes");
                            mostrarCartas(mano);
                            realizarApuesta(mesa, numJugador);
                        } else {
                            //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                        }


                    } else if (numJugador == 3) {

                        if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                            System.out.println("Grandes");
                            mostrarCartas(mano);
                            realizarApuesta(mesa, numJugador);
                        } else {
                            //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                        }


                    } else if (numJugador == 4) {

                        if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                            System.out.println("Grandes");
                            mostrarCartas(mano);
                            realizarApuesta(mesa, numJugador);
                        } else {
                            //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                        }


                    }
                    enviarMesa(mesa, ip, numJugador);
                    grandes(mano, ip, numJugador);
                } else {
                    enviarMesa(mesa, ip, numJugador);
                    grandes(mano, ip, numJugador);
                }
            }


        } else {
            if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(2) > 0) {
                //nadie ha igualado
                mesa.addPuntos(1, numJugador);
            }

            mesa.setNumRonda(3);
            mesa.setNumJugadorApuestaMasAlta(-1);
            mesa.setPasadas(0);
            enviarMesa(mesa, ip, numJugador);

        }


    }

    public static void chicas(ArrayList<Carta> mano, String ip, int numJugador) {

        Mesa mesa = recibirMesa(numJugador);
        if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 3 && mesa.getPasadas() != 4) {
            if (mesa.getApuestaMasAlta() > 0)
                System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " ***");


            if (numJugador == 1) {
                if (mesa.getApuestaMasAlta() == -1) {
                    //Primera ronda todavia no se ha podido apostar
                    System.out.println("Chicas");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //alguien ha apostado
                    if (mesa.getNumJugadorApuestaMasAlta() != 3 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Chicas");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                    }
                }

            } else if (numJugador == 2) {

                if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Chicas");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            } else if (numJugador == 3) {

                if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Chicas");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            } else if (numJugador == 4) {

                if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Chicas");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            }
            enviarMesa(mesa, ip, numJugador);
            chicas(mano, ip, numJugador);
        } else {
            if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(3) >= 0) {
                //nadie ha igualado
                mesa.addPuntos(1, numJugador);
            }

            mesa.setNumRonda(4);
            mesa.setNumJugadorApuestaMasAlta(-1);
            mesa.setPasadas(0);
            enviarMesa(mesa, ip, numJugador);

        }
    }

    public static void pares(ArrayList<Carta> mano, String ip, int numJugador) {

        Mesa mesa = recibirMesa(numJugador);
        if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 4 && mesa.getPasadas() != 4) {
            if (mesa.getApuestaMasAlta() > 0)
                System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " ***");



            if (numJugador == 1 && hayPares(mano)) {
                if (mesa.getApuestaMasAlta() == -1) {
                    //Primera ronda todavia no se ha podido apostar
                    System.out.println("Pares");
                    mostrarCartas(mano);
                    mesa.setApuestaMasAlta(0, 1);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //alguien ha apostado
                    if (mesa.getNumJugadorApuestaMasAlta() != 3 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Pares");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                    }
                }

            } else if (numJugador == 2 && hayPares(mano)) {

                if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Pares");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            } else if (numJugador == 3 && hayPares(mano)) {

                if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Pares");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            } else if (numJugador == 4 && hayPares(mano)) {

                if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Pares");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            }
            if(!hayPares(mano))mesa.setPasadas(mesa.getPasadas() + 1);
            enviarMesa(mesa, ip, numJugador);
            pares(mano, ip, numJugador);
        } else {
            if (!hayPares(mano)) System.out.println("No tienes pares");
            if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(4) > 0) {
                //nadie ha igualado
                mesa.addPuntos(1, numJugador);
            }
            mesa.setNumRonda(5);
            mesa.setNumJugadorApuestaMasAlta(-1);
            mesa.setPasadas(0);
            enviarMesa(mesa, ip, numJugador);

        }
    }

    public static Mesa juego(ArrayList<Carta> mano, String ip, int numJugador) {

        Mesa mesa = recibirMesa(numJugador);
        if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 5 && hayJuego(mano) && mesa.getPasadas() != 4) {
            if (mesa.getApuestaMasAlta() > 0)
                System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " ***");


            if (numJugador == 1 && hayJuego(mano)) {
                if (mesa.getApuestaMasAlta() == -1) {
                    //Primera ronda todavia no se ha podido apostar
                    System.out.println("Juego");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //alguien ha apostado
                    if (mesa.getNumJugadorApuestaMasAlta() != 3 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Juego");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                    }
                }

            } else if (numJugador == 2 && hayJuego(mano)) {

                if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Juego");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            } else if (numJugador == 3 && hayJuego(mano)) {

                if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Juego");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            } else if (numJugador == 4 && hayJuego(mano)) {

                if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                    System.out.println("Juego");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            }
            if(!hayJuego(mano))mesa.setPasadas(mesa.getPasadas() + 1);
            enviarMesa(mesa, ip, numJugador);
            pares(mano, ip, numJugador);
        } else {
            if (!hayJuego(mano)) System.out.println("No tienes juego");
            if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(5) > 0) {
                //nadie ha igualado
                mesa.addPuntos(1, numJugador);
            }

            mesa.setNumRonda(6);
            mesa.setNumJugadorApuestaMasAlta(-1);
            mesa.setPasadas(0);
            if(numJugador!=1)enviarMesa(mesa, ip, numJugador);


        }
        if(numJugador==1) return mesa;
        System.out.println("salimos de juegoooooooooooooooooooooooooooooooooooooooo");
        return null;
    }

    public static void puntos(ArrayList<Carta> mano, String ip, int numJugador) {
        Mesa mesa = recibirMesa(numJugador);
        if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 5 && hayJuego(mano)) {
            if (mesa.getApuestaMasAlta() > 0)
                System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " ***");

            Scanner sc = new Scanner(System.in);
            if (numJugador == 1) {
                if (mesa.getApuestaMasAlta() == -1) {
                    //Primera ronda todavia no se ha podido apostar
                    System.out.println("Puntos");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //alguien ha apostado
                    if (mesa.getNumJugadorApuestaMasAlta() != 3 && mesa.getApuestaMasAlta() > 0) {
                        System.out.println("Puntos");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                    }
                }

            } else if (numJugador == 2) {

                if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() > 0) {
                    System.out.println("Puntos");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            } else if (numJugador == 3) {

                if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() > 0) {
                    System.out.println("Puntos");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            } else if (numJugador == 4) {

                if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() > 0) {
                    System.out.println("Puntos");
                    mostrarCartas(mano);
                    realizarApuesta(mesa, numJugador);
                } else {
                    //System.out.println("No puedes apostar, tu compañero ya ha apostado");
                }


            }
            enviarMesa(mesa, ip, numJugador);
            pares(mano, ip, numJugador);
        } else {
            if (!hayJuego(mano)) System.out.println("No tienes juego");
            if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getApuestaMasAlta(2) > 0) {
                //nadie ha igualado
                mesa.addPuntos(1, numJugador);
            }

            mesa.setNumRonda(6);
            mesa.setNumJugadorApuestaMasAlta(-1);
            enviarMesa(mesa, ip, numJugador);
            System.out.println("salimos");

        }
    }

    public static void asignarPuntos(ArrayList<Carta> mano, String ipJ1, int numJugador ,Mesa mesa) {

        //Si soy el primer jugador me encargo del reparto de puntos
        if (numJugador == 1) {

            //solicitamos la mano a todos los jugadores
            ArrayList<Carta> manoJ2 = solicitarMano(mesa.getIps().get(1),2);
            System.out.println("mano 1 recibida");
            ArrayList<Carta> manoJ3 = solicitarMano(mesa.getIps().get(2),3);
            System.out.println("mano 2 recibida");
            ArrayList<Carta> manoJ4 = solicitarMano(mesa.getIps().get(3),4);
            System.out.println("mano 3 recibida");
            System.out.println("He recivido las 4 manos");


            for (int i = 1; i < 6; i++) {
                if (mesa.getApuestaMasAlta(i) < 0) {
                    switch (i) {
                        case 2: //grandes
                            Carta cartaAltaE1 = Baraja.getCartaAlta(mano);
                            Carta cartaAltaE2 = Baraja.getCartaAlta(manoJ2);

                            if (cartaAltaE1.getNumero() < Baraja.getCartaAlta(manoJ3).getNumero()) {
                                cartaAltaE1 = Baraja.getCartaAlta(manoJ3);
                            }
                            if (cartaAltaE2.getNumero() < Baraja.getCartaAlta(manoJ4).getNumero()) {
                                cartaAltaE2 = Baraja.getCartaAlta(manoJ4);
                            }

                            if (cartaAltaE1.getNumero() > cartaAltaE2.getNumero()) {
                                mesa.addPuntos(mesa.getApuestaMasAlta(2) * -1, 1);
                            } else {
                                mesa.addPuntos(mesa.getApuestaMasAlta(2) * -1, 2);
                            }
                            break;
                        case 3: //Chicas
                            Carta cartaBajaE1 = Baraja.getCartaBaja(mano);
                            Carta cartaBajaE2 = Baraja.getCartaBaja(manoJ2);
                            if (cartaBajaE1.getNumero() < Baraja.getCartaBaja(manoJ3).getNumero()) {
                                cartaBajaE1 = Baraja.getCartaAlta(manoJ3);
                            }
                            if (cartaBajaE2.getNumero() < Baraja.getCartaBaja(manoJ4).getNumero()) {
                                cartaBajaE2 = Baraja.getCartaAlta(manoJ4);
                            }
                            if (cartaBajaE1.getNumero() < cartaBajaE2.getNumero()) {
                                mesa.addPuntos(mesa.getApuestaMasAlta(3) * -1, 1);
                            } else {
                                mesa.addPuntos(mesa.getApuestaMasAlta(3) * -1, 2);
                            }
                            break;
                        case 4: //Pares
                            int p1 = puntuajePares(mano);
                            int p2 = puntuajePares(manoJ2);
                            int p3 = puntuajePares(manoJ3);
                            int p4 = puntuajePares(manoJ4);
                            p1 = Math.max(p1, p3);
                            p2 = Math.max(p2, p4);
                            if (p1 > p2) mesa.addPuntos(mesa.getApuestaMasAlta(4), 1);
                            else mesa.addPuntos(mesa.getApuestaMasAlta(4), 2);
                            break;


                        case 5: //Juego
                            mesa.addPuntos(mesa.getApuestaMasAlta(5), ganadorJuego(mano, manoJ2, manoJ3, manoJ4));
                            break;


                    }
                }
            }
            mesa.showPuntuaciones();
            mesa.reiniciarPartida();
            enviarMesa(mesa, mesa.getIps().get(1), numJugador);
            if (mesa.finalizado()) System.out.println("FIN");
            System.exit(0);

        } else {
            //Si no soy el primer juegador envio mi mano
            try (ServerSocket ss = new ServerSocket(4321+numJugador)) {
                System.out.println("recibimos por "+numJugador);
                Socket s = ss.accept();
                System.out.println("conectado");
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                out.writeObject(mano);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mesa = recibirMesa(numJugador);
            mesa.showPuntuaciones();
            if(numJugador==4) enviarMesa(mesa, mesa.getIps().get(0), numJugador);
            else enviarMesa(mesa, mesa.getIps().get(numJugador), numJugador);
            if (mesa.finalizado()) System.out.println("FIN");
            System.exit(0);

        }


    }

    public static ArrayList<Carta> solicitarMano(String ip,int numJugador) {
        try (Socket s = new Socket(ip, 4321+numJugador)) {
            System.out.println("nos conectamos al puerto "+numJugador);
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ArrayList<Carta> mano = (ArrayList<Carta>) in.readObject();
            return mano;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void enviarMano(ArrayList<Carta> mano, String ip) {

        try (Socket sEnviar = new Socket(ip, 1235)) {

            DataInputStream in = new DataInputStream(sEnviar.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(sEnviar.getOutputStream());

            in.read();
            out.writeObject(mano);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void realizarApuesta(Mesa mesa, int numJugador) {
        Scanner sc = new Scanner(System.in);
        System.out.println("1 - Envidar\n2 - Pasar\n3 - Apostar\n4 - Igualar");
        switch (Integer.parseInt(sc.nextLine())) {
            case 1:
                System.out.println("Envidamos");
                if (!mesa.puedeApostar(2)) {
                    System.out.println("La cantidad apostada debe ser superior a " + mesa.getApuestaMasAlta());
                    realizarApuesta(mesa, numJugador);
                } else mesa.setApuestaMasAlta(2, numJugador); mesa.setPasadas(0);
                break;
            case 2:
                System.out.println("Pasamos");
                mesa.setPasadas(mesa.getPasadas() + 1);
                if(mesa.getApuestaMasAlta()<0)mesa.setApuestaMasAlta(0, mesa.getNumJugadorApuestaMasAlta());
                break;
            case 3:
                System.out.println("Introduce la cantidad: ");
                int cantidad = Integer.parseInt(sc.nextLine());
                while (!mesa.puedeApostar(cantidad)) {
                    System.out.println("La cantidad apostada debe ser superior a " + mesa.getApuestaMasAlta());
                    System.out.println("1 - Envidar\n2 - Pasar\n3 - Apostar");
                    cantidad = Integer.parseInt(sc.nextLine());
                }
                mesa.setApuestaMasAlta(cantidad, numJugador); mesa.setPasadas(0);
                System.out.println("Apostamos");
                break;
            case 4:
                System.out.println("Igualamos");
                //mesa.setNumJugadorApuestaMasAlta(mesa.getNumRonda() + 1);
                mesa.setApuestaMasAlta(-mesa.getApuestaMasAlta(), mesa.getNumJugadorApuestaMasAlta());
                mesa.setPasadas(0);
                break;
        }
    }

    private static boolean hayPares(ArrayList<Carta> mano) {
        for (Carta carta1 : mano) {
            for (Carta carta2 : mano) {
                if (!carta1.equals(carta2)) {
                    if (carta1.getNumero() == carta2.getNumero()) return true;
                }
            }
        }
        return false;
    }

    private static boolean hayJuego(ArrayList<Carta> mano) {
        int suma = 0;
        for (Carta carta : mano) {
            if (carta.getNumero() >= 10) suma = suma + 10;
            else suma = suma + carta.getNumero();
        }
        return suma >= 31;
    }

    private static int puntosJuego(ArrayList<Carta> mano) {
        int suma = 0;
        for (Carta carta : mano) {
            if (carta.getNumero() >= 10) suma = suma + 10;
            else suma = suma + carta.getNumero();
        }
        return suma;
    }

    public static int ganadorJuego(ArrayList<Carta> mano1, ArrayList<Carta> mano2, ArrayList<Carta> mano3, ArrayList<Carta> mano4) {


        Map<Integer, Integer> puntajes = new HashMap<>();
        puntajes.put(31, 0);
        puntajes.put(32, 1);
        puntajes.put(40, 2);
        puntajes.put(37, 3);
        puntajes.put(36, 4);
        puntajes.put(35, 5);
        puntajes.put(34, 6);
        puntajes.put(33, 7);

        int puntajeJugador1 = puntosJuego(mano1);
        int puntajeJugador2 = puntosJuego(mano2);
        int puntajeJugador3 = puntosJuego(mano3);
        int puntajeJugador4 = puntosJuego(mano4);

        ArrayList<Integer> puntajesOrdenados = new ArrayList<>(puntajes.values());
        Collections.sort(puntajesOrdenados, Collections.reverseOrder());

        for (int puntaje : puntajesOrdenados) {
            if (puntaje == puntajeJugador1) {
                return 1;
            } else if (puntaje == puntajeJugador2) {
                return 2;
            } else if (puntaje == puntajeJugador3) {
                return 3;
            } else if (puntaje == puntajeJugador4) {
                return 4;
            }
        }

        // Si ninguno de los jugadores tiene una puntuación en la lista, devuelve -1 o maneja la situación según sea necesario.
        return -1;
    }

    public static int puntuajePares(ArrayList<Carta> mano) {
        /*
         * Si tiene pares dev 1, trio dev 2, duplex dev 3
         */
        ArrayList<Carta> copia = new ArrayList<>(4);
        int puntuacion = 0;
        for (Carta carta : mano) {
            copia.add(carta);
        }


        for (int i = 0; i < 4; i++) {
            Carta c = copia.get(i);
            for (Carta carta : copia) {
                if (!c.equals(carta) && c.getNumero() == carta.getNumero() && puntuacion == 3) puntuacion = 4;
                else if (!c.equals(carta) && c.getNumero() == carta.getNumero() && puntuacion == 2) puntuacion = 3;
                else if (!c.equals(carta) && c.getNumero() == carta.getNumero() && puntuacion == 0) puntuacion = 2;
            }
        }
        return puntuacion;
    }


}


