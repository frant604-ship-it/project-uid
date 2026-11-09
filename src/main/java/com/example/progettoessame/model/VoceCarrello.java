package com.example.progettoessame.model;

import java.math.BigDecimal;

public record VoceCarrello(long id, String nome, BigDecimal prezzo, String immagine,
                          int quantita, String taglia) {
    public BigDecimal totale() {
        return prezzo.multiply(BigDecimal.valueOf(quantita));
    }
}
