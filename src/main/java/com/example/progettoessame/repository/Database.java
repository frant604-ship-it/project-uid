package com.example.progettoessame.repository;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private final String url;
    private final SQLiteConfig configurazione = new SQLiteConfig();

    private Database(Path file) {
        url = "jdbc:sqlite:" + file;
        configurazione.resetOpenMode(SQLiteOpenMode.CREATE);
        configurazione.setTransactionMode(SQLiteConfig.TransactionMode.IMMEDIATE);
    }

    public static Database apri() throws IOException, SQLException {
        Path file = Path.of("prodotti.db");
        if (!Files.isRegularFile(file)) {
            file = Path.of("project-uid").resolve(file);
        }
        if (!Files.isRegularFile(file)) {
            throw new IOException("File prodotti.db non trovato. Avvia l’app dalla cartella del progetto.");
        }

        Database database = new Database(file);
        database.aggiornaSchema();
        return database;
    }

    Connection connessione() throws SQLException {
        return DriverManager.getConnection(url, configurazione.toProperties());
    }

    private void aggiornaSchema() throws SQLException {
        try (var connessione = connessione()) {
            connessione.setAutoCommit(false);
            boolean haTaglia;
            try (var colonne = connessione.getMetaData().getColumns(null, null, "carrello", "taglia")) {
                haTaglia = colonne.next();
            }
            if (!haTaglia) {
                try (var statement = connessione.createStatement()) {
                    statement.executeUpdate("ALTER TABLE carrello ADD COLUMN taglia TEXT");
                }
            }
            connessione.commit();
        }
    }
}
