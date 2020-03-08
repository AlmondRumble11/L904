package com.example.l9;

public class Teatteri {
    private String id;
    private String name;
    public Teatteri(String id, String nimi){
        this.id = id;
        this.name = nimi;

    }
    public  String getName(){
        return  name;
    }
    public String getId(){
        return id;
    }
}

