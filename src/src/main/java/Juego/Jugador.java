package Juego;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Jugador implements Serializable {
    private String name;
    private double cartera;

    public Jugador(){

    }
    public Jugador(String name, double cartera){
        this.name = name;
        this.cartera = cartera;
    }
    private void setName(String name1){
        this.name=name1;
    }
    public String getName(){
        return this.name;
    }
    private void setCartera(double cartera){
        this.cartera=cartera;
    }

    public double getCartera() {
        return cartera;
    }

    public static boolean logIn(Scanner sc, Jugador jugador){
        System.out.println("Introduce tu nombre de usuario");
        String user = sc.nextLine();
        System.out.println("Introduce la contraseña");
        String passwd = sc.nextLine();

        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(new File("src//src//main//resources//Jugadores.xml"));

            NodeList list = d.getElementsByTagName("jugador");

            for (int i = 0; i< list.getLength(); i++){
                if (list.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element hijo = (Element) list.item(i);
                    String nombre = hijo.getElementsByTagName("nombre").item(0).getTextContent();
                    String passwd1 = hijo.getElementsByTagName("passwd").item(0).getTextContent();
                    if (nombre.equals(user) && passwd1.equals(passwd)) {
                        String cartera = hijo.getElementsByTagName("cartera").item(0).getTextContent();
                        System.out.println("Creditos disponibles: "+cartera);

                        jugador.setName(nombre);
                        jugador.setCartera(Double.parseDouble(cartera));

                        return true;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }


        return false;
    }


    public static boolean singUp(Jugador jugador){
        //añadimos el jugador a la base de datos

        if(!exist(jugador.getName())){
            try{
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document d = db.parse(new File("src//src//main//resources//Jugadores.xml"));

                NodeList list = d.getElementsByTagName("jugador");


            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("El jugador " +jugador.getName()+" ya existe");
        return false;

    }

    public static boolean exist(String name){
        //Comprueba si el jugador existe en la base de datos
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(new File("src//src//main//resources//Jugadores.xml"));

            NodeList list = d.getElementsByTagName("jugador");

            for (int i = 0; i< list.getLength(); i++){
                if (list.item(i).getNodeType() == Node.ELEMENT_NODE){
                    Element hijo = (Element) list.item(i);
                    String nombre = hijo.getElementsByTagName("nombre").item(0).getTextContent();
                    if (name.equals(nombre)) return true;

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


}