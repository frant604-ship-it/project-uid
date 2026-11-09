package com.example.progettoessame.view;

import com.example.progettoessame.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
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

    public static List<Image> iconeFinestra() {
        Image logo = new Image(necessaria("immagini/Nuove Immagini/Logo Bianco").toExternalForm());
        SnapshotParameters sfondo = new SnapshotParameters();
        sfondo.setFill(Color.TRANSPARENT);
        return List.of(16, 32, 48, 128).stream()
                .map(dimensione -> iconaFinestra(logo, sfondo, dimensione))
                .toList();
    }

    private static Image iconaFinestra(Image logo, SnapshotParameters sfondo, int lato) {
        Canvas tela = new Canvas(lato, lato);
        var grafica = tela.getGraphicsContext2D();
        grafica.setFill(Color.web("#171717"));
        grafica.fillRoundRect(0, 0, lato, lato, lato * 0.25, lato * 0.25);

        // Coordinate del simbolo a destra della scritta nel logo originale.
        grafica.drawImage(logo, 1683, 238, 340, 240,
                lato * 0.10, lato * 0.218, lato * 0.80, lato * 0.565);
        return tela.snapshot(sfondo, null);
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
