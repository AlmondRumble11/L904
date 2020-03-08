package com.example.l9;
//vastaa L904
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;

import java.text.DateFormat;

import java.text.SimpleDateFormat;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Context context;
    String nimi;
    ListView elokuvat;
    String ajankohta;
    String minimi;
    Date tunti;
    EditText aika;
    EditText pvm;
    Spinner teatteriValikko;
    List<String> teatterilista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        teatteriValikko = (Spinner)findViewById(R.id.teatteriValikko);
        TeatteriLista tl = TeatteriLista.getInstance();
        tl.lueXML();//saadaan teatterien tiedot

        //dopdown menun tekeminen annetuista tiedoista
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,tl.getTeatterilista());
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        teatteriValikko.setAdapter(adapter);

        //elokuva tietojen hankkiminen
        nimi = teatteriValikko.getSelectedItem().toString();
        pvm = (EditText)findViewById(R.id.paivamaara);
        ajankohta = (String)pvm.getText().toString();
        //tl.luePvm(nimi,ajankohta);


        //telokuvien tulostus


    }
    public void ilmotus(String s){
        Toast.makeText(MainActivity.this, s,Toast.LENGTH_SHORT).show();
    }
    // tapahtuu kun painaa haku nappia
    public void nappi(View v) {
        //aloitus ajat jos käyttjäjä ei antanut mitään aikaväliä
        String[] ajat={"00","00"};
        //saadaan teatterlista luokka tänne
        TeatteriLista tl = TeatteriLista.getInstance();
        nimi = teatteriValikko.getSelectedItem().toString();
        pvm = (EditText) findViewById(R.id.paivamaara);
        aika = (EditText) findViewById(R.id.ajankohta);
        minimi = (String)aika.getText().toString();
        ajankohta = (String) pvm.getText().toString();

        //jos käyttäjä on jättänyt  pvm antamatta
        if (ajankohta.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            ajankohta = dateFormat.format(date);

        }
        if (minimi.isEmpty()){

            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            //aloitus ajankohta on nykyinen aika HUOM ei ole Suomen ajassa vaan brittejen ajassa
            ajat[0] = dateFormat.format(date);
            System.out.println("aika "+ajat[0]);
            //lopetus ajankoha on nyt päivän viimeinen minuutti
            ajat[1]="23:59";
        }else{
            //jos köyttäjä on osannut hommansa ja antanut tiedot
             ajat = minimi.split("-");
        }
        System.out.println("pvm "+ajankohta);

        //
        tl.luePvm(nimi, ajankohta);

        elokuvat = (ListView) findViewById(R.id.listview);

        tl.tulostaElokuvat(ajat[0],ajat[1]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, tl.getElokuvalista());
        elokuvat.setAdapter(arrayAdapter);
        pvm.setText("");
        aika.setText("");
    }
}

