package com.example.progettoessame.view;

import com.example.progettoessame.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;

public final class Risorse {
    private static final String BASE_PRECEDENTE = "com/example/progettoessame/";

    private Risorse() {
    }

    public static FXMLLoader vista(String nome) {
        return new FXMLLoader(necessaria("view/" + nome));
    }

    public static String css() {
        return necessaria("view/negozio.css").toExternalForm();
    }

    public static Image immagine(String percorso, double larghezza, boolean inBackground) {
        if (percorso == null || percorso.isBlank()) {
            return null;
        }
        if (percorso.startsWith("/" + BASE_PRECEDENTE)) {
            percorso = percorso.substring(1);
        }
        if (percorso.startsWith(BASE_PRECEDENTE)) {
            percorso = percorso.substring(BASE_PRECEDENTE.length());
        }
        if (percorso.startsWith("/") || percorso.contains(":") || percorso.contains("..")) {
            return null;
        }
        URL risorsa = HelloApplication.class.getResource(percorso);
        return risorsa == null ? null : new Image(risorsa.toExternalForm(), larghezza, 0, true, true, inBackground);
    }

    private static URL necessaria(String percorso) {
        return Objects.requireNonNull(HelloApplication.class.getResource(percorso), percorso);
    }
}
