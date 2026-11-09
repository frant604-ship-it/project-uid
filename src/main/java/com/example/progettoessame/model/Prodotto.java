package com.example.progettoessame.model;

import java.math.BigDecimal;

public record Prodotto(long id, String nome, BigDecimal prezzo, String immagine,
                       String immagineRetro, String descrizione) {
    public String nomeVisualizzato() {
        return nome.replaceAll("\\s+", " ").strip();
    }
}
