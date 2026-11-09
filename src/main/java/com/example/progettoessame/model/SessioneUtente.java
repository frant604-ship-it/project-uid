package com.example.progettoessame.model;

public final class SessioneUtente {
    private String username;

    public String username() {
        return username;
    }

    public boolean attiva() {
        return username != null;
    }

    public void accedi(String username) {
        this.username = username;
    }

    public void esci() {
        username = null;
    }
}
