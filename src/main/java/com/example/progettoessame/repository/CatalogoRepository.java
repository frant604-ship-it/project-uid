package com.example.progettoessame.repository;

import com.example.progettoessame.model.Prodotto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CatalogoRepository {
    private final Database database;

    public CatalogoRepository(Database database) {
        this.database = database;
    }

    public List<Prodotto> cerca(String sesso, String categoria, String testo) throws SQLException {
        String sql = """
                SELECT id, nome, prezzo, immagine, immagine_retro, descrizione
                FROM prodotti
                WHERE (? = '' OR sesso = ?)
                  AND (? = '' OR categoria = ? OR (? = 'felpe' AND categoria = 'felpa'))
                  AND instr(lower(replace(nome, char(10), ' ')), lower(?)) > 0
                ORDER BY id
                """;
        // instr restituisce 1 quando il testo cercato è vuoto.
        List<Prodotto> prodotti = new ArrayList<>();
        try (var connessione = database.connessione();
             var query = connessione.prepareStatement(sql)) {
            query.setString(1, sesso);
            query.setString(2, sesso);
            query.setString(3, categoria);
            query.setString(4, categoria);
            query.setString(5, categoria);
            query.setString(6, testo.strip());
            try (var righe = query.executeQuery()) {
                while (righe.next()) {
                    prodotti.add(new Prodotto(righe.getLong("id"), righe.getString("nome"),
                            righe.getBigDecimal("prezzo"), righe.getString("immagine"),
                            righe.getString("immagine_retro"), righe.getString("descrizione")));
                }
            }
        }
        return prodotti;
    }
}
