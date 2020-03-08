package com.example.l9;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TeatteriLista {
    private int check = 0;
    private String nimi;
    private List<String> elokuva_aika;
    private List<String> teatterilista = null;
    private List<String> ajantLista;
    private ArrayList<Teatteri> nimilista =null;
    private static TeatteriLista tl= new TeatteriLista();
    private  List<String> elokuvalista = new ArrayList<>();


    //instanssin saaminen
    public static TeatteriLista getInstance(){
        return tl;
    }


    //luetaan teatterit ja lisätään ne listaan
    public List lueXML() {

        try {
            //parsetetaan XML
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String teatteritUrl = "https://www.finnkino.fi/xml/TheatreAreas/";
            Document doc = builder.parse(teatteritUrl);
            doc.getDocumentElement().normalize();

            //tehdään lista siitä
            NodeList nList = doc.getDocumentElement().getElementsByTagName("TheatreArea");
            nimilista = new ArrayList<>();

            //etsitään halutut tiedot ja tallennetaan ne listaan
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                System.out.println("Element" + node.getNodeName());

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.print("ID on: ");
                    System.out.println(element.getElementsByTagName("ID").item(0).getTextContent());
                    System.out.print("Teatterin nimi on: ");
                    System.out.println(element.getElementsByTagName("Name").item(0).getTextContent());
                    Teatteri theater = new Teatteri(element.getElementsByTagName("ID").item(0).getTextContent(), element.getElementsByTagName("Name").item(0).getTextContent());
                    nimilista.add(theater);
                }
            }
            //tehdään listan joka tulostetaan spinneriin ja palauetetaan se mainactivityyn jotta se voidaan tulostaa sovelluksessa käyttäjälle
            teatterilista = new ArrayList<>();
            teatterilista.add("Valitse teatteri");
            for (int j = 0; j < nimilista.size(); j++) {
                nimi = nimilista.get(j).getName();
                if (nimilista.get(j).getName().contains(":")){
                    teatterilista.add(nimi);
                }
            }
            return teatterilista;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            System.out.println("*********tehty*******");
        }
        return teatterilista;
    }

    public List<String> getTeatterilista() {
        return teatterilista;
    }
    public ArrayList<Teatteri> getNimilista(){
        return  nimilista;
    }
    public List<String> getElokuvalista(){return elokuva_aika;}


    public  void tulostaElokuvat(String alku, String loppu){
        //nämä on sitä varten että voidaan verrata minuutit ja tunnit erikseen
        int alku_tunti;
        int alku_minuutti;
        int loppu_tunti;
        int loppu_minuutti;
        int elokuvan_h;
        int elokuvan_min;

        String elokuva;
        String ajankohta;

        //lista jossa on vain elokuvat halutulla aika välillä
        elokuva_aika = new ArrayList<>();

        //splitataan alku ja loppu ajankodat tunneiksi ja minuuteiksi
        String[] alkuajankohta = alku.split(":");
        String[] loppuajankohta = loppu.split(":");
        String[] erottettu;

        //äskettäin splitut tunnit aj minuiti sitten muunnetaan kokonaisluvuiksi
        alku_tunti = Integer.parseInt(alkuajankohta[0]);
        alku_minuutti = Integer.parseInt(alkuajankohta[1]);
        loppu_tunti = Integer.parseInt(loppuajankohta[0]);
        loppu_minuutti = Integer.parseInt(loppuajankohta[1]);

        //luetaan ajankohta listaa lävitse
        for (int i=0; i<ajantLista.size();i++) {
            //System.out.println("HEi ollaan täällä");

            //erotetaan listassa oleva ajankohta tunneiksi ja minuteiksi aj muunnetaan ne kokonaisluvuiksi
            erottettu = ajantLista.get(i).split(":");
            elokuvan_h = Integer.parseInt(erottettu[0]);
            elokuvan_min = Integer.parseInt(erottettu[1]);


            //System.out.println(alku_tunti+" ja elokuva tunti "+elokuvan_h+" alku minuutti "+elokuvan_min+"loppu tunti "+loppu_tunti+" loppp minutti "+loppu_minuutti);

            //jos elokuvan alkamistunti ja alkamisminuutti ovat suuremmat/yhtä suuret kuin annettu aloitus ajankohta niin tehdää toiomenpiteet
            if ((alku_tunti <= elokuvan_h) && (alku_minuutti <= elokuvan_min)){
                alku_minuutti=0; //nollataan alku minuutti koska sitä ei enää tarvita

                System.out.println(alku_minuutti+" elokuvan minuutti "+elokuvan_min);

                //jos elokuvan alkaisajankohta on suuremoi kuin lopetusajankohta niin pysäytetään tallennus listaan
                if((loppu_tunti <= elokuvan_h)&& ((loppu_minuutti <= elokuvan_min))) {
                    break;
                }else {
                    System.out.println("nyt toimii");

                    //tallennetaan elokuvalistasta joka tehtii aiemmin aina elokuva ja sen ajankohta sen on halutulla aikavälillä
                    elokuva_aika.add(elokuvalista.get(i));
                }
            }
        }
    }

    public  void luePvm(String teatteri, String pvm){
        String id = null;
        try {
            //etsitään annetun teatteri id
            for (int j=0;j<nimilista.size();j++){
                if (nimilista.get(j).getName() == teatteri){
                    id = nimilista.get(j).getId();
                    //System.out.println(id);
                }
            }
            //jos käyttäjä oli jättänyt ensimmäisen vaihtoehdon eli ei ole teatteria valittu
            if (id == null){
                id = "0000";
            }

            //parsetetaan XML, jossa elokuvat pävittäin jokaisesta teatterista
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String teatteritUrl = "https://www.finnkino.fi/xml/Schedule/?area=teatterinID&dt=p%C3%A4iv%C3%A4m%C3%A4%C3%A4r%C3%A4%20pp.kk.vvvv";
            Document doc = builder.parse(teatteritUrl);
            doc.getDocumentElement().normalize();

            //löytyy show-merkin  alta
            NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");

            elokuvalista = new ArrayList<>();
            ajantLista = new ArrayList<>();

            //täytetään lista joka tulostetaan näytölle
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    /*String[] ajat = element.getElementsByTagName("dttmShowStart").item(0).getTextContent().substring(11,16).split(":");
                    elokuvaTunti = Integer.parseInt(ajat[0]);
                    elokuvaMinuutti = Integer.parseInt(ajat[1]);*/

                    System.out.println(element.getElementsByTagName("TheatreID").item(0).getTextContent() +"ja id oli "+id);
                    if((id == "0000") &&(element.getElementsByTagName("dttmShowStart").item(0).getTextContent().substring(0, 10).equals(pvm) == true)){
                        elokuvalista.add("Teatteri: "+element.getElementsByTagName("Theatre").item(0).getTextContent()+"\nElokuva: " + element.getElementsByTagName("Title").item(0).getTextContent() + "\nAlkamisajankohta: " + element.getElementsByTagName("dttmShowStart").item(0).getTextContent().substring(11, 16));
                        ajantLista.add(element.getElementsByTagName("dttmShowStart").item(0).getTextContent().substring(11, 16));
                    }

                    else {
                        if ((element.getElementsByTagName("TheatreID").item(0).getTextContent().contains(id)) && (element.getElementsByTagName("dttmShowStart").item(0).getTextContent().substring(0, 10).equals(pvm) == true)) {

                            //nämä printit on vain koodin kirjoittajalle
                            System.out.println(element.getElementsByTagName("dttmShowStart").item(0).getTextContent());
                            System.out.print("Leffa on: ");
                            System.out.println(element.getElementsByTagName("Title").item(0).getTextContent());
                            System.out.print("Teatterin ID on: ");
                            System.out.println(element.getElementsByTagName("TheatreID").item(0).getTextContent());

                            //tähän listaan menee kaikki esitys ajat joita sitten voidaan verrata elokuvia etsittäessä
                            ajantLista.add(element.getElementsByTagName("dttmShowStart").item(0).getTextContent().substring(11, 16));

                            //tähän lsitaan menee kaikki kyseinsen päivän teatterin elokuvat ja ajankohdat jotta sitten voidaan kun verrataan yllä olevaa listaa vain ottaa tästä aina löydetty kohta
                            elokuvalista.add("Elokuva: " + element.getElementsByTagName("Title").item(0).getTextContent() + "\nAlkamisajankohta: " + element.getElementsByTagName("dttmShowStart").item(0).getTextContent().substring(11, 16));
                            //Teatteri theater = new Teatteri(element.getElementsByTagName("ID").item(0).getTextContent(), element.getElementsByTagName("Name").item(0).getTextContent());
                        }
                    }
                }
            }
            System.out.println(elokuvalista.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } finally {
            System.out.println("*********tehty*******");
        }


    }
}

