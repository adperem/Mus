package Juego;


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
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Jugador implements Serializable {
    private String name;
    private double cartera;
    private String passwd;

    public Jugador() {

    }

    public Jugador(String name, double cartera, String passwd) {
        this.name = name;
        this.cartera = cartera;
        this.passwd = passwd;
    }

    private void setName(String name1) {
        this.name = name1;
    }

    public String getName() {
        return this.name;
    }

    public void setCartera(double cartera) {
        this.cartera = cartera;
    }

    public double getCartera() {
        return cartera;
    }









    public String getPasswd() {
        return passwd;
    }
}
