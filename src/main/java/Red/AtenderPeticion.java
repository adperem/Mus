package Red;

import Juego.Jugador;
import Juego.Mesa;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.*;

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
        try  {
            ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());

            String accion = (String) in.readObject();
            System.out.println(accion);

            //Se quiere crear una nueva mesa
            if (accion.equals("NEW")) {
                //Creamos una mesa

                int codMesa = 1;
                for (Mesa mesa : mesasPrivadas) {
                    if (mesa.getCodMesa() == codMesa) codMesa++;
                }


                Mesa mesa = new Mesa(codMesa);
                out.writeObject(codMesa);
                mesa.addIp(this.socket.getInetAddress().getHostAddress());
                mesasPrivadas.add(mesa);

                while (mesa.getNumPlayers() != 4) { //Esperamos a que el num de jugadores sea 4
                    Thread.sleep(1000);
                    System.out.println("Jugadores unidos: " + mesa.getNumPlayers());
                }
                out.writeInt(1); // Enviamos el numJugador
                out.writeObject(mesa);

            } else if (accion.equals("JOIN")) {
                //nos unimos a una mesa
                int codMesa = (int) in.readObject();
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
                        if(numJugador == 1) out.writeObject(mesa); // Enviamos la mesa


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

                    }
                }
            } else if (accion.equals("LOGIN")) {

                Jugador jugador = (Jugador) in.readObject();

                if (!exist(jugador.getName())){
                    out.writeObject(false);
                }
                else {
                    out.writeBoolean(logIn(jugador));
                    out.writeObject(jugador);
                }


            } else if (accion.equals("SINGUP")) {
                Jugador jugador = (Jugador) in.readObject();
                out.writeObject(true);
                if (exist(jugador.getName())) out.writeBoolean(false);
                else out.writeBoolean(singUp(jugador));

            } else if (accion.equals("UPDATE")) {
                Jugador jugador = (Jugador) in.readObject();
                update(jugador);
            } else if (accion.equals("BUY")) {
                Jugador jugador = (Jugador) in.readObject();
            }
            socket.close();


        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public static void update(Jugador jugador) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(new File("src\\main\\resources\\Jugadores.xml"));


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(d);
            StreamResult result = new StreamResult(new File("src\\main\\resources\\Jugadores.xml"));

            NodeList list = d.getElementsByTagName("jugador");

            for (int i = 0; i < list.getLength(); i++) {

                Element hijo = (Element) list.item(i);
                Element nombre = (Element) hijo.getElementsByTagName("nombre");
                if (nombre.getTextContent().equals(jugador.getName())) {
                    Element cartera = (Element) hijo.getElementsByTagName("cartera");
                    cartera.setTextContent(String.valueOf(jugador.getCartera()));
                }
            }





            transformer.transform(source, result);

        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            System.err.println("Error en el update");
        }

    }


    public static boolean singUp(Jugador jugador) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse("src\\main\\resources\\Jugadores.xml");


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(d);
            StreamResult result = new StreamResult(new File("src\\main\\resources\\Jugadores.xml"));


            Element player = d.createElement("jugador");

            Element nombre = d.createElement("nombre");
            nombre.setTextContent(jugador.getName());
            player.appendChild(nombre);

            Element cartera = d.createElement("cartera");
            cartera.setTextContent(String.valueOf(jugador.getCartera()));
            player.appendChild(cartera);

            Element passwd = d.createElement("passwd");
            passwd.setTextContent(jugador.getPasswd());
            player.appendChild(passwd);

            d.getDocumentElement().appendChild(player);


            transformer.transform(source, result);
            return true;


        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            System.err.println("Error en el sing up");
        }
        return false;

    }

    public static boolean exist(String name) {
        //Comprueba si el jugador existe en la base de datos
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(new File("src\\main\\resources\\Jugadores.xml"));

            NodeList list = d.getElementsByTagName("jugador");

            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element hijo = (Element) list.item(i);
                    String nombre = hijo.getElementsByTagName("nombre").item(0).getTextContent();
                    if (name.equals(nombre)) return true;

                }
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            System.err.println("Error en el exist");
        }
        return false;
    }

    public static boolean logIn(Jugador jugador) {


        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(new File("src//main//resources//Jugadores.xml"));

            NodeList list = d.getElementsByTagName("jugador");

            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element hijo = (Element) list.item(i);
                    String nombre = hijo.getElementsByTagName("nombre").item(0).getTextContent();
                    String passwd1 = hijo.getElementsByTagName("passwd").item(0).getTextContent();
                    if (nombre.equals(jugador.getName()) && passwd1.equals(jugador.getPasswd())) {
                        String cartera = hijo.getElementsByTagName("cartera").item(0).getTextContent();

                        jugador.setCartera(Double.parseDouble(cartera));

                        return true;
                    }
                }
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            System.err.println("Error en el login");
        }

        return false;
    }

}
