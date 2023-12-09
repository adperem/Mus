package Red;

import Juego.Baraja;
import Juego.Carta;
import Juego.Jugador;
import Juego.Mesa;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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

                    Jugador j;
                    while ((this.jugador = logIn(sc)) == null) {
                        System.out.println("Usuario o contraseña incorrecta");
                    }

                    break;

            }

            //Usuario logeado

            System.out.println("Selecccione una opcion:");
            System.out.println("1 - Empezar a jugar");
            System.out.println("2 - Crear una mesa");
            System.out.println("3 - Unirse a una mesa");
            seleccion = Integer.parseInt(sc.nextLine());
            while (seleccion != 1 && seleccion != 2 && seleccion != 3) {
                System.out.println("Selecccione una opcion:");
                System.out.println("1 - Empezar a jugar");
                System.out.println("2 - Crear una mesa");
                System.out.println("3 - Unirse a una mesa");
                seleccion = Integer.parseInt(sc.nextLine());
            }

            switch (seleccion) {
                case 1:

                    //Solicitar mesa y siguiente jugador
                    Socket socket = new Socket("localhost", 3333);


                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("PLAY");

                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    int numJugador = (int) in.readObject();

                    play(in, numJugador);
                    break;


                case 2:
                    //crear una mesa
                    try {
                        socket = new Socket("localhost", 3333);

                        out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject("NEW");

                        in = new ObjectInputStream(socket.getInputStream());
                        int codMesa = (int) in.readObject();
                        System.out.println("Codigo de la mesa " + codMesa);
                        numJugador = in.readInt();
                        System.out.println("Has creado la mesa " + codMesa);
                        play(in, numJugador);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    break;

                case 3:
                    // Unirse a una mesa
                    try {
                        socket = new Socket("localhost", 3333);


                        out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject("JOIN");

                        in = new ObjectInputStream(socket.getInputStream());

                        System.out.println("Inserte el codigo de la mesa");

                        out.writeObject(Integer.parseInt(sc.nextLine()));

                        numJugador = (int) in.readObject();
                        if (numJugador == 0) {
                            System.out.println("Error al unirse a la mesa");
                        } else {
                            play(in, numJugador);
                        }


                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private void play(ObjectInputStream in, int numJugador) {

        try {
            System.out.println("Esperando a que empiece la partida");
            Scanner sc = new Scanner(System.in);
            int seleccion;
            ArrayList<Carta> mano = new ArrayList<>(4);
            if (numJugador == 1) {
                //si es el primer jugador recibe la mesa del servidor
                Mesa mesa = (Mesa) in.readObject();
                in.close();

                String ip = mesa.getIps().get(numJugador);
                System.out.println("Te has unido a la mesa");

                mano.add(mesa.sacarCarta());
                Thread.sleep(1000);
                Socket sEnviar = new Socket(ip, 1234 + numJugador + 1);

                ObjectOutputStream out = new ObjectOutputStream(sEnviar.getOutputStream());
                out.writeObject(mesa);
                ServerSocket ss = new ServerSocket(1234 + numJugador);
                Socket sRecibir = ss.accept();
                in = new ObjectInputStream(sRecibir.getInputStream());
                for (int i = 0; i < 3; i++) {
                    mesa = (Mesa) in.readObject();
                    mano.add(mesa.sacarCarta());
                    out.writeObject(mesa);
                }

                mostrarCartas(mano);
                mesa = (Mesa) in.readObject();

                System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                seleccion = Integer.parseInt(sc.nextLine());
                while (seleccion != 1 && seleccion != 2) {
                    System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                    seleccion = Integer.parseInt(sc.nextLine());
                }
                switch (seleccion) {
                    case 1:
                        out.writeObject(mesa);
                        break;
                    case 2:
                        mesa.cortar();
                        mesa.setJugadorCortar(numJugador);
                        out.writeObject(mesa);
                        break;
                }
                mesa = (Mesa) in.readObject();

                if (mesa.getJugadorCortar() != 0 && mesa.getJugadorCortar() != numJugador) {
                    out.writeObject(mesa);
                    mesa = (Mesa) in.readObject();
                }

                if (mesa.isMus()) {
                    System.out.println("Mus aceptado");
                    System.out.println("Indique separadas por comas las cartes que desea eliminar");
                    String eliminar = sc.nextLine();
                    String[] deleteCartas = eliminar.split(",");
                    ArrayList<Carta> delete = new ArrayList<>();
                    for (int i = 0; i < deleteCartas.length; i++)
                        delete.add(mano.get(Integer.parseInt(deleteCartas[i]) - 1));
                    for (int i = 0; i < deleteCartas.length; i++) {
                        mano.remove(delete.get(i));
                    }
                    out.writeObject(mesa);
                    mus(mano, in, out, numJugador);
                } else {
                    System.out.println("Se ha cortado");
                    mesa.setNumRonda(1);
                    mesa.setNumJugadorApuestaMasAlta(-1);
                    out.writeObject(mesa);
                }

                while (true) {
                    grandes(mano, in, out, numJugador);
                    chicas(mano, in, out, numJugador);
                    pares(mano, in, out, numJugador);
                    juego(mano, in, out, numJugador);
                    puntos(mano, in, out, numJugador);
                    asignarPuntos(mano, in, out, numJugador, this.jugador);
                    mano = new ArrayList<>(4);
                    mus(mano, in, out, numJugador);
                }


            } else {
                //si es otro jugador recibe la mesa del jugador anterior
                System.out.println("Te has unido a la mesa");
                in.close();
                ServerSocket ss = new ServerSocket(1234 + numJugador);
                Socket sRecibir = ss.accept();
                in = new ObjectInputStream(sRecibir.getInputStream());
                Mesa mesa = (Mesa) in.readObject();

                String ip;
                Socket sEnviar;
                if (numJugador == 4) {
                    ip = mesa.getIps().get(0);
                    sEnviar = new Socket(ip, 1235);
                } else {
                    ip = mesa.getIps().get(numJugador);
                    sEnviar = new Socket(ip, 1234 + numJugador + 1);
                }


                ObjectOutputStream out = new ObjectOutputStream(sEnviar.getOutputStream());

                for (int i = 0; i < 4; i++) {

                    mano.add(mesa.sacarCarta());
                    out.writeObject(mesa);
                    mesa = (Mesa) in.readObject();
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
                            out.writeObject(mesa);
                            break;
                        case 2:
                            mesa.cortar();
                            mesa.setJugadorCortar(numJugador);

                            out.writeObject(mesa);
                            break;
                    }
                    mesa = (Mesa) in.readObject();
                }

                if (mesa.getJugadorCortar() != 0 && mesa.getJugadorCortar() != numJugador) {
                    out.writeObject(mesa);
                    mesa = (Mesa) in.readObject();
                }


                if (mesa.isMus()) {
                    System.out.println("Mus aceptado");
                    System.out.println("Indique separadas por comas las cartes que desea eliminar");
                    String eliminar = sc.nextLine();
                    String[] deleteCartas = eliminar.split(",");
                    ArrayList<Carta> delete = new ArrayList<>();
                    for (int i = 0; i < deleteCartas.length; i++)
                        delete.add(mano.get(Integer.parseInt(deleteCartas[i]) - 1));
                    for (int i = 0; i < deleteCartas.length; i++) {
                        mano.remove(delete.get(i));
                    }

                    out.writeObject(mesa);
                    mus(mano, in, out, numJugador);
                } else {
                    System.out.println("Se ha cortado");
                    mesa.setNumRonda(1);
                    mesa.setNumJugadorApuestaMasAlta(-1);
                    out.writeObject(mesa);
                }

                while (true) {
                    System.out.println("vamos a entrar a grandes");
                    grandes(mano, in, out, numJugador);
                    chicas(mano, in, out, numJugador);
                    pares(mano, in, out, numJugador);
                    juego(mano, in, out, numJugador);
                    puntos(mano, in, out, numJugador);
                    asignarPuntos(mano, in, out, numJugador, this.jugador);

                    //mesa = (Mesa) in.readObject();
                    //out.writeObject(mesa);


                    mano = new ArrayList<>(4);
                    mus(mano, in, out, numJugador);
                }


            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.err.println("Ha ocurrido un error");
        }


    }


    private static void newPlayer(Scanner sc, Jugador jugador) {
        //creamos el nuevo jugador
        System.out.println("Introduce el nombre del jugador");
        String nombre = sc.nextLine();


        System.out.println("Introduce una contraseña");
        String passwd = sc.nextLine();


        System.out.println("Introduce la cantidad a depositar");
        double cartera = Double.parseDouble(sc.nextLine());
        System.out.print("Pocesando compra");
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(100);
                System.out.print(".");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\nPago procesa con exito");
        Jugador j = new Jugador(nombre, cartera, passwd);
        jugador = j;
        try {
            Socket s = new Socket("localhost", 3333);

            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject("SINGUP");
            out.writeObject(j);
            DataInputStream in = new DataInputStream(s.getInputStream());
            if (in.readBoolean()) System.out.println("Jugador creado con exito");
            else System.out.println("Ha ocurrido un problema, vuelve a intentarlo mas tarde");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Ha ocurrido un error");
        }

    }

    private static Jugador logIn(Scanner sc) {
        //creamos el nuevo jugador
        System.out.println("Introduce el nombre del jugador");
        String nombre = sc.nextLine();


        System.out.println("Introduce una contraseña");
        String passwd = sc.nextLine();


        Jugador jugador = new Jugador(nombre, 0, passwd);


        try (Socket s = new Socket("localhost", 3333)) {


            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject("LOGIN");
            out.writeObject(jugador);
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());


            if (in.readBoolean()) {
                System.out.println("Sesion iniciada correctamente");
                jugador = (Jugador) in.readObject();
            } else {
                System.out.println("Ha ocurrido un problema, vuelve a intentarlo mas tarde");
                return null;
            }

            System.out.println("Creditos disponibles: " + jugador.getCartera());
            return jugador;
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private static void update(Jugador jugador) {

        try {
            Socket s = new Socket("localhost", 3333);


            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject("UPDATE");
            out.writeObject(jugador);
            //ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            //if(in.readBoolean()) System.out.println("conectado");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void mostrarCartas(ArrayList<Carta> cartas) {
        System.out.println("-------------------");
        System.out.println("Tus cartas");
        int i = 1;
        for (Carta carta : cartas) {
            System.out.print(i + ") ");
            carta.mostrarCarta();
            i++;
        }
        System.out.println("-------------------");

    }


    private static void mus(ArrayList<Carta> mano, ObjectInputStream in, ObjectOutputStream out, int numJugador) {

        try {
            Scanner sc = new Scanner(System.in);
            Mesa mesa;


            for (int i = 0; i < 4; i++) {
                mesa = (Mesa) in.readObject();
                if (mano.size() < 4) mano.add(mesa.sacarCarta());
                out.writeObject(mesa);
            }

            mesa = (Mesa) in.readObject();
            mesa.showPuntuaciones();

            if (mesa.isMus()) {
                System.out.println("Ronda de Mus");
                mostrarCartas(mano);

                System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                int seleccion = Integer.parseInt(sc.nextLine());
                while (seleccion != 1 && seleccion != 2) {
                    System.out.println("\nSeleccione\n1 - Mus\n2 - Cortar");
                    seleccion = Integer.parseInt(sc.nextLine());
                }
                switch (seleccion) {
                    case 1:
                        out.writeObject(mesa);
                        break;
                    case 2:
                        mesa.cortar();
                        mesa.setJugadorCortar(numJugador);
                        out.writeObject(mesa);
                        break;
                }
            } else {
                out.writeObject(mesa);
            }

            mesa = (Mesa) in.readObject();

            if (mesa.isMus()) {
                System.out.println("Mus aceptado");
                System.out.println("Indique separadas por comas las cartes que desea eliminar");
                String eliminar = sc.nextLine();
                String[] deleteCartas = eliminar.split(",");
                ArrayList<Carta> delete = new ArrayList<>();
                for (int i = 0; i < deleteCartas.length; i++)
                    delete.add(mano.get(Integer.parseInt(deleteCartas[i]) - 1));
                for (int i = 0; i < deleteCartas.length; i++) {
                    mano.remove(delete.get(i));
                }
                out.writeObject(mesa);
                mus(mano, in, out, numJugador);
            } else {
                System.out.println("Se ha cortado");
                mesa.setNumRonda(1);
                mesa.setNumJugadorApuestaMasAlta(-1);
                out.writeObject(mesa);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    private static void grandes(ArrayList<Carta> mano, ObjectInputStream in, ObjectOutputStream out, int numJugador) {

        try {
            Mesa mesa = (Mesa) in.readObject();
            if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 1 && mesa.getPasadas() != 4) {
                if (mesa.getApuestaMasAlta() > 0)
                    System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " en grandes ***");


                if (mesa.getJugadorCortar() == numJugador) {
                    //Le toca al que ha cortado
                    realizarApuesta(mesa, numJugador);
                    mesa.setJugadorCortar(-1);
                    out.writeObject(mesa);
                    grandes(mano, in, out, numJugador);
                } else {
                    if (mesa.getJugadorCortar() == -1) {

                        if (numJugador == 1) {
                            if (mesa.getNumJugadorApuestaMasAlta() != 3 && mesa.getApuestaMasAlta() >= 0) {
                                System.out.println("Grandes");
                                mostrarCartas(mano);
                                realizarApuesta(mesa, numJugador);
                            } else {
                                if (mesa.getNumJugadorApuestaMasAlta() == 3)
                                    System.out.println("No puedes apostar a grandes, tu compañero ya ha apostado");
                            }


                        } else if (numJugador == 2) {

                            if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                                System.out.println("Grandes");
                                mostrarCartas(mano);
                                realizarApuesta(mesa, numJugador);
                            } else {
                                if (mesa.getNumJugadorApuestaMasAlta() == 4)
                                    System.out.println("No puedes apostar a grandes, tu compañero ya ha apostado");
                            }


                        } else if (numJugador == 3) {

                            if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                                System.out.println("Grandes");
                                mostrarCartas(mano);
                                realizarApuesta(mesa, numJugador);
                            } else {
                                if (mesa.getNumJugadorApuestaMasAlta() == 1)
                                    System.out.println("No puedes apostar a grandes, tu compañero ya ha apostado");
                            }


                        } else if (numJugador == 4) {

                            if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                                System.out.println("Grandes");
                                mostrarCartas(mano);
                                realizarApuesta(mesa, numJugador);
                            } else {
                                if (mesa.getNumJugadorApuestaMasAlta() == 2)
                                    System.out.println("No puedes apostar a grandes, tu compañero ya ha apostado");
                            }


                        }
                        out.writeObject(mesa);
                        grandes(mano, in, out, numJugador);
                    } else {
                        out.writeObject(mesa);
                        grandes(mano, in, out, numJugador);
                    }
                }


            } else {
                if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(2) > 0) {
                    //nadie ha igualado
                    mesa.addPuntos(1, numJugador);
                }

                mesa.setNumRonda(2);
                mesa.setNumJugadorApuestaMasAlta(-1);
                mesa.setPasadas(0);
                out.writeObject(mesa);


            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static void chicas(ArrayList<Carta> mano, ObjectInputStream in, ObjectOutputStream out, int numJugador) {

        try {
            Mesa mesa = (Mesa) in.readObject();
            if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 2 && mesa.getPasadas() != 4) {
                if (mesa.getApuestaMasAlta() > 0)
                    System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " en chicas ***");


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
                            if (mesa.getNumJugadorApuestaMasAlta() == 3)
                                System.out.println("No puedes apostar a chicas, tu compañero ya ha apostado");
                        }
                    }

                } else if (numJugador == 2) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Chicas");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 4)
                            System.out.println("No puedes apostar a chicas, tu compañero ya ha apostado");
                    }


                } else if (numJugador == 3) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Chicas");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 1)
                            System.out.println("No puedes apostar a chicas, tu compañero ya ha apostado");
                    }


                } else if (numJugador == 4) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Chicas");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 2)
                            System.out.println("No puedes apostar a chicas, tu compañero ya ha apostado");
                    }


                }
                out.writeObject(mesa);
                chicas(mano, in, out, numJugador);
            } else {
                if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(2) >= 0) {
                    //nadie ha igualado
                    mesa.addPuntos(1, numJugador);
                }

                mesa.setNumRonda(3);
                mesa.setNumJugadorApuestaMasAlta(-1);
                mesa.setPasadas(0);
                out.writeObject(mesa);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static void pares(ArrayList<Carta> mano, ObjectInputStream in, ObjectOutputStream out, int numJugador) {

        try {
            Mesa mesa = (Mesa) in.readObject();
            if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 3 && mesa.getPasadas() != 4) {

                if (mesa.getApuestaMasAlta() > 0)
                    System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " en pares ***");


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
                            if (mesa.getNumJugadorApuestaMasAlta() == 3)
                                System.out.println("No puedes apostar a pares, tu compañero ya ha apostado");
                        }
                    }

                } else if (numJugador == 2 && hayPares(mano)) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Pares");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 4)
                            System.out.println("No puedes apostar a pares, tu compañero ya ha apostado");
                    }


                } else if (numJugador == 3 && hayPares(mano)) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Pares");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 1)
                            System.out.println("No puedes apostar a pares, tu compañero ya ha apostado");
                    }


                } else if (numJugador == 4 && hayPares(mano)) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Pares");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 2)
                            System.out.println("No puedes apostar a pares, tu compañero ya ha apostado");
                    }


                }
                if (!hayPares(mano)) {
                    mesa.setPasadas(mesa.getPasadas() + 1);
                }

                out.writeObject(mesa);
                pares(mano, in, out, numJugador);
            } else {
                if (!hayPares(mano)) System.out.println("No puedes apostar, no tienes pares");
                if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(3) > 0) {
                    //nadie ha igualado
                    mesa.addPuntos(1, numJugador);
                }
                mesa.setNumRonda(4);
                mesa.setNumJugadorApuestaMasAlta(-1);
                mesa.setPasadas(0);
                out.writeObject(mesa);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static void juego(ArrayList<Carta> mano, ObjectInputStream in, ObjectOutputStream out, int numJugador) {

        try {
            Mesa mesa = (Mesa) in.readObject();
            if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 4 && mesa.getPasadas() != 4) {
                if (mesa.getApuestaMasAlta() > 0)
                    System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " en juego ***");


                if (numJugador == 1 && hayJuego(mano)) {
                    mesa.setJuego(true);
                    if (mesa.getApuestaMasAlta() == -1) {
                        //Primera ronda todavia no se ha podido apostar
                        System.out.println("Juego");
                        mostrarCartas(mano);
                        mesa.setApuestaMasAlta(0, 1);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        //alguien ha apostado
                        if (mesa.getNumJugadorApuestaMasAlta() != 3 && mesa.getApuestaMasAlta() >= 0) {
                            System.out.println("Juego");
                            mostrarCartas(mano);
                            realizarApuesta(mesa, numJugador);
                        } else {
                            if (mesa.getNumJugadorApuestaMasAlta() == 3)
                                System.out.println("No puedes apostar a juego, tu compañero ya ha apostado");
                        }
                    }

                } else if (numJugador == 2 && hayJuego(mano)) {
                    mesa.setJuego(true);
                    if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Juego");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 4)
                            System.out.println("No puedes apostar a juego, tu compañero ya ha apostado");
                    }


                } else if (numJugador == 3 && hayJuego(mano)) {
                    mesa.setJuego(true);
                    if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Juego");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 1)
                            System.out.println("No puedes apostar a juego, tu compañero ya ha apostado");
                    }


                } else if (numJugador == 4 && hayJuego(mano)) {
                    mesa.setJuego(true);
                    if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Juego");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 2)
                            System.out.println("No puedes apostar a juego, tu compañero ya ha apostado");
                    }


                }
                if (!hayJuego(mano)) {
                    mesa.setPasadas(mesa.getPasadas() + 1);
                }

                out.writeObject(mesa);
                juego(mano, in, out, numJugador);
            } else {
                if (!hayJuego(mano)) System.out.println("No puedes apostar, no tienes juego");

                if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(4) > 0) {
                    //nadie ha igualado
                    mesa.addPuntos(1, numJugador);
                }

                mesa.setNumRonda(5);
                mesa.setNumJugadorApuestaMasAlta(-1);
                mesa.setPasadas(0);

                out.writeObject(mesa);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static void puntos(ArrayList<Carta> mano, ObjectInputStream in, ObjectOutputStream out, int numJugador) {
        try {
            Mesa mesa = (Mesa) in.readObject();
            if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 5 && mesa.getPasadas() != 4 && !mesa.hayJuego()) {
                if (mesa.getApuestaMasAlta() > 0)
                    System.out.println("*** Apuesta actual: " + mesa.getApuestaMasAlta() + " en puntos ***");


                if (numJugador == 1) {
                    if (mesa.getApuestaMasAlta() == -1) {
                        //Primera ronda todavia no se ha podido apostar
                        System.out.println("Puntos");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        //alguien ha apostado
                        if (mesa.getNumJugadorApuestaMasAlta() != 3 && mesa.getApuestaMasAlta() >= 0) {
                            System.out.println("Puntos");
                            mostrarCartas(mano);
                            realizarApuesta(mesa, numJugador);
                        } else {
                            if (mesa.getNumJugadorApuestaMasAlta() == 3)
                                System.out.println("No puedes apostar, tu compañero ya ha apostado");
                        }
                    }

                } else if (numJugador == 2) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 4 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Puntos");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 4)
                            System.out.println("No puedes apostar, tu compañero ya ha apostado");
                    }


                } else if (numJugador == 3) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 1 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Puntos");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 1)
                            System.out.println("No puedes apostar, tu compañero ya ha apostado");
                    }


                } else if (numJugador == 4) {

                    if (mesa.getNumJugadorApuestaMasAlta() != 2 && mesa.getApuestaMasAlta() >= 0) {
                        System.out.println("Puntos");
                        mostrarCartas(mano);
                        realizarApuesta(mesa, numJugador);
                    } else {
                        if (mesa.getNumJugadorApuestaMasAlta() == 2)
                            System.out.println("No puedes apostar, tu compañero ya ha apostado");
                    }


                }
                out.writeObject(mesa);
                puntos(mano, in, out, numJugador);
            } else {
                if (mesa.getNumJugadorApuestaMasAlta() == numJugador && mesa.getApuestaMasAlta(5) >= 0) {
                    //nadie ha igualado
                    mesa.addPuntos(1, numJugador);
                }

                mesa.setNumRonda(6);
                mesa.setNumJugadorApuestaMasAlta(-1);
                mesa.setPasadas(0);
                out.writeObject(mesa);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static void asignarPuntos(ArrayList<Carta> mano, ObjectInputStream in, ObjectOutputStream out, int numJugador, Jugador jugador) {

        try {
            Mesa mesa = (Mesa) in.readObject();
            if (mesa.getNumJugadorApuestaMasAlta() != numJugador && mesa.getNumRonda() == 6 && !mesa.finalizado()) {


                if (numJugador == 1 && mesa.getManos().size() == 3) {


                    ArrayList<Carta> manoJ2 = mesa.getManos().get(0);
                    ArrayList<Carta> manoJ3 = mesa.getManos().get(1);
                    ArrayList<Carta> manoJ4 = mesa.getManos().get(2);

                    for (int i = 1; i < 6; i++) {
                        if (mesa.getApuestaMasAlta(i) < 0) {
                            switch (i) {
                                case 1: //grandes
                                    Carta cartaAltaE1 = Baraja.getCartaAlta(mano);
                                    Carta cartaAltaE2 = Baraja.getCartaAlta(manoJ2);

                                    if (cartaAltaE1.getNumero() < Baraja.getCartaAlta(manoJ3).getNumero()) {
                                        cartaAltaE1 = Baraja.getCartaAlta(manoJ3);
                                    }
                                    if (cartaAltaE2.getNumero() < Baraja.getCartaAlta(manoJ4).getNumero()) {
                                        cartaAltaE2 = Baraja.getCartaAlta(manoJ4);
                                    }

                                    if (cartaAltaE1.getNumero() > cartaAltaE2.getNumero()) {

                                        mesa.addPuntos(mesa.getApuestaMasAlta(1) * -1, 1);
                                    } else {

                                        mesa.addPuntos(mesa.getApuestaMasAlta(1) * -1, 2);
                                    }
                                    break;
                                case 2: //Chicas
                                    if (!mesa.finalizado()) {
                                        Carta cartaBajaE1 = Baraja.getCartaBaja(mano);
                                        Carta cartaBajaE2 = Baraja.getCartaBaja(manoJ2);
                                        if (cartaBajaE1.getNumero() < Baraja.getCartaBaja(manoJ3).getNumero()) {
                                            cartaBajaE1 = Baraja.getCartaAlta(manoJ3);
                                        }
                                        if (cartaBajaE2.getNumero() < Baraja.getCartaBaja(manoJ4).getNumero()) {
                                            cartaBajaE2 = Baraja.getCartaAlta(manoJ4);
                                        }
                                        if (cartaBajaE1.getNumero() < cartaBajaE2.getNumero()) {
                                            mesa.addPuntos(mesa.getApuestaMasAlta(2) * -1, 1);
                                        } else {
                                            mesa.addPuntos(mesa.getApuestaMasAlta(2) * -1, 2);
                                        }
                                    }
                                    break;
                                case 3: //Pares
                                    if (!mesa.finalizado()) {
                                        int p1 = puntuajePares(mano);
                                        int p2 = puntuajePares(manoJ2);
                                        int p3 = puntuajePares(manoJ3);
                                        int p4 = puntuajePares(manoJ4);
                                        p1 = Math.max(p1, p3);
                                        p2 = Math.max(p2, p4);
                                        if (p1 > p2) mesa.addPuntos(mesa.getApuestaMasAlta(3) * -1, 1);
                                        else mesa.addPuntos(mesa.getApuestaMasAlta(3) * -1, 2);
                                    }
                                    break;


                                case 4: //Juego
                                    if (!mesa.finalizado()) {
                                        mesa.addPuntos(mesa.getApuestaMasAlta(4) * -1, ganadorJuego(mano, manoJ2, manoJ3, manoJ4));
                                    }
                                    break;

                                case 5: // Puntos
                                    if (!mesa.finalizado()) {
                                        if (!mesa.hayJuego()) {
                                            mesa.addPuntos(mesa.getApuestaMasAlta(5) * -1, ganadorPuntos(mano, manoJ2, manoJ3, manoJ4));
                                        }
                                        break;
                                    }
                            }
                        }
                    }
                    //mesa.showPuntuaciones();
                    if (!mesa.finalizado()) mesa.reiniciarPartida();
                    out.writeObject(mesa);
                    asignarPuntos(mano, in, out, numJugador, jugador);


                } else {
                    if (numJugador != 1 && mesa.getManos().size() < 3) {
                        mesa.addMano(mano);
                    }
                    out.writeObject(mesa);
                    asignarPuntos(mano, in, out, numJugador, jugador);
                }
/*
                if (numJugador != 1) {
                    out.writeObject(mesa);
                    asignarPuntos(mano, in, out, numJugador, jugador);
                }*/

            }else {

                //if (numJugador == 4 && !mesa.finalizado()) out.writeObject(mesa);
                //if (numJugador != 4 ) out.writeObject(mesa);
                if(numJugador == 1 && !mesa.finalizado()) out.writeObject(mesa);
                if(numJugador != 1 ) out.writeObject(mesa);

                if (mesa.finalizado()) {

                    if (mesa.ganador() == 0 && (numJugador == 1 || numJugador == 3)) {
                        jugador.setCartera(jugador.getCartera() + 10);
                        update(jugador);
                    }
                    if (mesa.ganador() == 0 && (numJugador == 2 || numJugador == 4)) {
                        jugador.setCartera(jugador.getCartera() - 10);
                        update(jugador);
                    }
                    if (mesa.ganador() == 1 && (numJugador == 2 || numJugador == 4)) {
                        jugador.setCartera(jugador.getCartera() + 10);
                        update(jugador);
                    }
                    if (mesa.ganador() == 1 && (numJugador == 1 || numJugador == 3)) {
                        jugador.setCartera(jugador.getCartera() - 10);
                        update(jugador);
                    }
                    mesa.showPuntuaciones();
                    out.close();
                    in.close();
                    System.out.println("FIN");
                    System.exit(0);

                }
            }



        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    private static void realizarApuesta(Mesa mesa, int numJugador) {
        Scanner sc = new Scanner(System.in);
        System.out.println("1 - Envidar\n2 - Pasar\n3 - Apostar\n4 - Igualar");
        int seleccion = Integer.parseInt(sc.nextLine());
        while (seleccion != 1 && seleccion != 2 && seleccion != 3 && seleccion != 4 && seleccion != 5) {
            System.out.println("1 - Envidar\n2 - Pasar\n3 - Apostar\n4 - Igualar");
            seleccion = Integer.parseInt(sc.nextLine());
        }
        switch (seleccion) {
            case 1:
                System.out.println("Envidamos");
                if (!mesa.puedeApostar(2)) {
                    System.out.println("La cantidad apostada debe ser superior a " + mesa.getApuestaMasAlta());
                    realizarApuesta(mesa, numJugador);
                } else mesa.setApuestaMasAlta(2, numJugador);
                mesa.setPasadas(0);
                break;
            case 2:
                System.out.println("Pasamos");
                mesa.setPasadas(mesa.getPasadas() + 1);
                if (mesa.getApuestaMasAlta() < 0) mesa.setApuestaMasAlta(0, mesa.getNumJugadorApuestaMasAlta());
                break;
            case 3:
                System.out.println("Introduce la cantidad: ");
                int cantidad = Integer.parseInt(sc.nextLine());
                while (!mesa.puedeApostar(cantidad)) {
                    System.out.println("La cantidad apostada debe ser superior a " + mesa.getApuestaMasAlta());
                    System.out.println("1 - Envidar\n2 - Pasar\n3 - Apostar\n4 - Igualar");
                    cantidad = Integer.parseInt(sc.nextLine());
                }
                mesa.setApuestaMasAlta(cantidad, numJugador);
                mesa.setPasadas(0);
                System.out.println("Apostamos");
                break;
            case 4:
                System.out.println("Igualamos");
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

    private static int ganadorJuego(ArrayList<Carta> mano1, ArrayList<Carta> mano2, ArrayList<Carta> mano3, ArrayList<Carta> mano4) {


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

    private static int puntuajePares(ArrayList<Carta> mano) {
        /**
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

    private static int ganadorPuntos(ArrayList<Carta> mano1, ArrayList<Carta> mano2, ArrayList<Carta> mano3, ArrayList<Carta> mano4) {

        int p1 = puntosJuego(mano1);
        int p2 = puntosJuego(mano2);
        int p3 = puntosJuego(mano3);
        int p4 = puntosJuego(mano4);

        ArrayList<Carta> bestP1 = null;
        ArrayList<Carta> bestP2 = null;


        if (p1 >= p3) {
            bestP1 = mano1;
        } else if (p3 > p1) {
            bestP1 = mano1;
        }

        if (p2 >= p4) {
            bestP2 = mano2;
        } else if (p4 > p2) {
            bestP2 = mano4;
        }

        if (puntosJuego(bestP1) > puntosJuego(bestP2)) return 1;
        return 2;


    }


}


