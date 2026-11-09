package com.example.progettoessame.repository;

import com.example.progettoessame.model.VoceCarrello;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CarrelloRepository {
    private final Database database;

    public CarrelloRepository(Database database) {
        this.database = database;
    }

    public boolean aggiungi(String username, long prodottoId, String taglia) throws SQLException {
        String sql = """
                INSERT INTO carrello (username, nome_prodotto, prezzo, percorso_foto, quantita, taglia)
                SELECT ?, nome, prezzo, immagine, 1, ? FROM prodotti WHERE id = ?
                """;
        try (var connessione = database.connessione();
             var query = connessione.prepareStatement(sql)) {
            query.setString(1, username);
            query.setString(2, taglia);
            query.setLong(3, prodottoId);
            return query.executeUpdate() == 1;
        }
    }

    public List<VoceCarrello> elenco(String username) throws SQLException {
        String sql = """
                SELECT id, nome_prodotto, prezzo, percorso_foto, quantita, taglia
                FROM carrello WHERE username = ? ORDER BY id
                """;
        List<VoceCarrello> voci = new ArrayList<>();
        try (var connessione = database.connessione();
             var query = connessione.prepareStatement(sql)) {
            query.setString(1, username);
            try (var righe = query.executeQuery()) {
                while (righe.next()) {
                    voci.add(new VoceCarrello(righe.getLong("id"), righe.getString("nome_prodotto"),
                            righe.getBigDecimal("prezzo"), righe.getString("percorso_foto"),
                            righe.getInt("quantita"), righe.getString("taglia")));
                }
            }
        }
        return voci;
    }

    public void rimuovi(String username, long voceId) throws SQLException {
        try (var connessione = database.connessione();
             var query = connessione.prepareStatement("DELETE FROM carrello WHERE username = ? AND id = ?")) {
            query.setString(1, username);
            query.setLong(2, voceId);
            query.executeUpdate();
        }
    }
}
