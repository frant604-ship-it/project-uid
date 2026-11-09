package com.example.progettoessame;

public class SessioneUtente {
    private static String utenteLoggato = null;

    public static void effettuaLogin(String username) {
        utenteLoggato = username;
        System.out.println("Sessione avviata sul programma per l'utente: " + utenteLoggato);
    }

    public static void effettuaLogout() {
        utenteLoggato = null;
        System.out.println("Sessione chiusa.");
    }

    public static String getUtenteLoggato() {
        return utenteLoggato;
    }

    public static boolean isLoggato() {
        return utenteLoggato != null;
    }
}
