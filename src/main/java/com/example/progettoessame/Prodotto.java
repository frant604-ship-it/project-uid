package com.example.progettoessame;

public class Prodotto {
    String nome;
    double prezzo;
    String fotoPath;
    String categoria;
    String sesso;
    String fotoRetroPath;
    String descrizione;

    public Prodotto(String nome, double prezzo, String fotoPath, String categoria, String sesso) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.fotoPath = fotoPath;
        this.sesso = sesso;
        this.fotoRetroPath = fotoRetroPath;
        this.descrizione = descrizione;
    }
    public String getNome() { return nome; }
    public double getPrezzo() { return prezzo; }
    public String getFotoPath() { return fotoPath; }
    public String getCategoria() {return categoria;}
    public String getSesso(){ return sesso; }
    public String getFotoRetroPath() { return fotoRetroPath; }
    public String getDescrizione() { return descrizione; }

    public Prodotto(String nome, double prezzo, String immagine) {
        this.nome = nome;
        this.prezzo = prezzo;
        this.fotoPath = immagine;
    }
}

