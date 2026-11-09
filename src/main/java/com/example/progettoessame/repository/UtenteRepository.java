package com.example.progettoessame.repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HexFormat;

public final class UtenteRepository {
    private final Database database;

    public UtenteRepository(Database database) {
        this.database = database;
    }

    public boolean registra(String username, String password) throws SQLException {
        String sql = "INSERT INTO utenti (username, password) VALUES (?, ?) ON CONFLICT(username) DO NOTHING";
        try (var connessione = database.connessione();
             var query = connessione.prepareStatement(sql)) {
            query.setString(1, username);
            query.setString(2, hash(password));
            return query.executeUpdate() == 1;
        }
    }

    public boolean verifica(String username, String password) throws SQLException {
        try (var connessione = database.connessione();
             var query = connessione.prepareStatement("SELECT password FROM utenti WHERE username = ?")) {
            query.setString(1, username);
            try (var righe = query.executeQuery()) {
                return righe.next() && hash(password).equals(righe.getString("password"));
            }
        }
    }

    private static String hash(String password) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException errore) {
            throw new IllegalStateException(errore);
        }
    }
}
