package com.example.progettoessame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL_DB = "jdbc:sqlite:prodotti.db";

    public static List<Prodotto> getProdotti(String categoria, String sesso) {
        List<Prodotto> lista = new ArrayList<>();

        String sql = "SELECT nome, prezzo, immagine FROM prodotti WHERE categoria = ? AND sesso = ?";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoria);
            pstmt.setString(2, sesso);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                double prezzo = rs.getDouble("prezzo");
                String urlImg = rs.getString("immagine");

                lista.add(new Prodotto(nome, prezzo, urlImg, categoria, sesso));
            }

        } catch (Exception e) {
            System.err.println("Errore durante la query al database");
            e.printStackTrace();
        }

        return lista;
    }
    public static String[] getDettagliProdotto(String nomeProdotto) {
        String[] datiExtra = new String[2];
        datiExtra[0] = "Nessuna descrizione disponibile.";
        datiExtra[1] = "";

        String sql = "SELECT descrizione, immagine_retro FROM prodotti WHERE nome = ?";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nomeProdotto);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String desc = rs.getString("descrizione");
                String retro = rs.getString("immagine_retro");

                if (desc != null) datiExtra[0] = desc;
                if (retro != null) datiExtra[1] = retro;
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dei dettagli del prodotto");
            e.printStackTrace();
        }
        return datiExtra;
    }


    public static String hassaPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean registraUtente(String username, String passwordHassata) {
        String sql = "INSERT INTO utenti(username, password) VALUES(?, ?)";

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(URL_DB);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, passwordHassata);
            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            System.err.println("Errore registrazione: " + e.getMessage());
            return false;
        }
    }

    public static boolean verificaLogin(String username, String passwordInChiaro) {
        String sql = "SELECT password FROM utenti WHERE username = ?";

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(URL_DB);
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String passwordSalvata = rs.getString("password");
                String passwordInseritaHassata = hassaPassword(passwordInChiaro);

                return passwordSalvata.equals(passwordInseritaHassata);
            }
        } catch (Exception e) {
            System.err.println("Errore verifica login: " + e.getMessage());
        }
        return false;
    }
    public static void copiaNelCarrello(String nomeProdotto) {
        String utente = SessioneUtente.getUtenteLoggato();

        if (nomeProdotto != null) {
            nomeProdotto = nomeProdotto.trim();
        }

        String query = "INSERT INTO carrello (username, nome_prodotto, prezzo, percorso_foto, quantita) " +
                "SELECT ?, nome, prezzo, immagine, 1 " +
                "FROM prodotti " +
                "WHERE nome LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, utente);
            pstmt.setString(2, "%" + nomeProdotto + "%");

            int righeInserite = pstmt.executeUpdate();

            if (righeInserite > 0) {
                System.out.println(nomeProdotto + " aggiunto correttamente! Righe scritte: " + righeInserite);
            } else {
                System.out.println("⚠ SQLite ha eseguito il comando ma ha inserito 0 righe.");
                System.out.println("Verifica -> Utente: [" + utente + "] | Nome cercato nel DB: [%" + nomeProdotto + "%]");
            }

        } catch (SQLException e) {
            System.err.println(" Errore durante la copia nel carrello: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Prodotto> getProdottiCarrello() {
        List<Prodotto> listaCarrello = new ArrayList<>();
        String utente = SessioneUtente.getUtenteLoggato();
        String sql = "SELECT nome_prodotto, prezzo, percorso_foto FROM carrello WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, utente);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Prodotto p = new Prodotto(
                        rs.getString("nome_prodotto"),
                        rs.getDouble("prezzo"),
                        rs.getString("percorso_foto")
                );
                listaCarrello.add(p);
            }
        } catch (SQLException e) {
            System.err.println(" Errore durante il recupero dal carrello: " + e.getMessage());
        }
        return listaCarrello;
    }

    public static double getTotaleCarrello() {
        String utente = SessioneUtente.getUtenteLoggato();
        double totale = 0.0;

        String query = "SELECT SUM(prezzo * quantita) AS totale_complessivo " +
                "FROM carrello " +
                "WHERE username = ?;";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, utente);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totale = rs.getDouble("totale_complessivo");
                }
            }
        } catch (SQLException e) {
            System.err.println(" Errore nel calcolo del totale del carrello: " + e.getMessage());
            e.printStackTrace();
        }

        return totale;
    }
    public static void rimuoviDalCarrello(String nomeProdotto) {
        String utente = SessioneUtente.getUtenteLoggato();
        String query = "DELETE FROM carrello WHERE username = ? AND nome_prodotto = ?;";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, utente);
            pstmt.setString(2, nomeProdotto);

            pstmt.executeUpdate();
            System.out.println( nomeProdotto + " rimosso dal database!");

        } catch (SQLException e) {
            System.err.println(" Errore durante la rimozione dal carrello: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static String testoCercato = "";

    public static List<Prodotto> getProdottiCercati() {
        List<Prodotto> lista = new ArrayList<>();
        String query = "SELECT * FROM prodotti WHERE nome LIKE ?;";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + testoCercato.trim() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    double prezzo = rs.getDouble("prezzo");
                    String immagine = rs.getString("immagine");
                    String immagineRetro = rs.getString("immagine_retro");
                    String descrizione = rs.getString("descrizione");

                    Prodotto p = new Prodotto(nome, prezzo, immagine, immagineRetro, descrizione);
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public static void svuotaCarrello() {
        String utente = SessioneUtente.getUtenteLoggato();
        String query = "DELETE FROM carrello WHERE username = ?;";

        try (Connection conn = DriverManager.getConnection(URL_DB);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, utente);
            pstmt.executeUpdate();
            System.out.println(" Il carrello dell'utente " + utente + " è stato svuotato nel DB!");

        } catch (SQLException e) {
            System.err.println(" Errore durante lo svuotamento del carrello: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
