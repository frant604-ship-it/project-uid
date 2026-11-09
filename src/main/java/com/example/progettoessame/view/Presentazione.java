package com.example.progettoessame.view;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class Presentazione {
    private Presentazione() {
    }

    public static String prezzo(BigDecimal importo) {
        return NumberFormat.getCurrencyInstance(Locale.ITALY).format(importo);
    }

    public static Region foto(String percorso, double larghezza, boolean ritaglia) {
        return foto(Risorse.immagine(percorso, larghezza, false), ritaglia);
    }

    public static Region foto(Image immagine, boolean ritaglia) {
        Region foto = new Region();
        var fondo = new BackgroundFill(Color.rgb(240, 240, 240), CornerRadii.EMPTY, Insets.EMPTY);
        if (immagine == null) {
            foto.setBackground(new Background(fondo));
        } else {
            var misura = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO,
                    false, false, !ritaglia, ritaglia);
            var sfondo = new BackgroundImage(immagine, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, misura);
            foto.setBackground(new Background(new BackgroundFill[]{fondo}, new BackgroundImage[]{sfondo}));
        }
        return foto;
    }
}
