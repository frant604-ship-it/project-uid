package com.example.progettoessame.view;

import com.example.progettoessame.model.Prodotto;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class GrigliaCatalogo extends GridPane {
    private static final double SPAZIO = 6;
    private static final List<String> EDITORIALI = List.of(
            "immagini/Nuove Immagini/Modella.jpg",
            "immagini/Nuove Immagini/Modella 3.jpg",
            "immagini/Nuove Immagini/Modella 2.jpg");

    private final List<Prodotto> prodotti;
    private final boolean editoriale;
    private final Map<String, Image> miniature;
    private final Consumer<Prodotto> apriDettaglio;
    private int colonne;

    public GrigliaCatalogo(List<Prodotto> prodotti, boolean editoriale,
                          Map<String, Image> miniature, ObservableDoubleValue larghezza,
                          Consumer<Prodotto> apriDettaglio) {
        this.prodotti = prodotti;
        this.editoriale = editoriale;
        this.miniature = miniature;
        this.apriDettaglio = apriDettaglio;
        setHgap(SPAZIO);
        setVgap(16);
        setMinWidth(0);
        prefWidthProperty().bind(larghezza);
        impagina(numeroColonne(getPrefWidth()));
        prefWidthProperty().addListener((observable, precedente, attuale) -> {
            int nuoveColonne = numeroColonne(attuale.doubleValue());
            if (nuoveColonne != colonne) {
                impagina(nuoveColonne);
            }
        });
    }

    private static int numeroColonne(double larghezza) {
        return larghezza > 0 && larghezza < 980 ? 3 : 5;
    }

    private void impagina(int numeroColonne) {
        colonne = numeroColonne;
        getChildren().clear();
        getColumnConstraints().clear();
        for (int indice = 0; indice < colonne; indice++) {
            ColumnConstraints colonna = new ColumnConstraints();
            colonna.setPercentWidth(100.0 / colonne);
            getColumnConstraints().add(colonna);
        }

        int colonna = 0;
        int riga = 0;
        int fotoInserite = 0;
        int limiteRiga = colonne;
        for (Prodotto prodotto : prodotti) {
            if (editoriale && colonna == 0 && riga > 0 && riga % 2 == 1 && fotoInserite < EDITORIALI.size()) {
                boolean allineataADestra = fotoInserite == 1;
                int ampiezza = allineataADestra ? 1 : 2;
                Node foto = fotoEditoriale(EDITORIALI.get(fotoInserite++));
                add(foto, allineataADestra ? colonne - ampiezza : 0, riga, ampiezza, 1);
                if (allineataADestra) {
                    limiteRiga = colonne - ampiezza;
                } else {
                    colonna = ampiezza;
                }
            }
            add(card(prodotto), colonna++, riga);
            if (colonna == limiteRiga) {
                colonna = 0;
                riga++;
                limiteRiga = colonne;
            }
        }
    }

    private Node fotoEditoriale(String percorso) {
        Image immagine = miniature.computeIfAbsent(percorso, chiave -> Risorse.immagine(chiave, 1080, true));
        Region foto = Presentazione.foto(immagine, true);
        dimensionaFoto(foto);
        VBox spazio = new VBox(foto);
        spazio.setMinWidth(0);
        return spazio;
    }

    private Node card(Prodotto prodotto) {
        String nome = prodotto.nomeVisualizzato();
        Image immagine = miniature.computeIfAbsent(prodotto.immagine(), chiave -> Risorse.immagine(chiave, 520, true));
        Region foto = Presentazione.foto(immagine, false);
        StackPane anteprima = new StackPane(foto);
        dimensionaFoto(anteprima);

        Button apri = new Button();
        apri.setGraphic(anteprima);
        apri.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        anteprima.prefWidthProperty().bind(apri.widthProperty());
        apri.setAccessibleText("Apri " + nome);
        apri.getStyleClass().add("apri-card");
        apri.setMinWidth(0);
        apri.setMaxWidth(Double.MAX_VALUE);
        apri.setOnAction(evento -> apriDettaglio.accept(prodotto));

        Label indicatore = new Label("+");
        indicatore.getStyleClass().add("indicatore-card");
        StackPane.setAlignment(indicatore, Pos.BOTTOM_RIGHT);
        indicatore.setMouseTransparent(true);
        anteprima.getChildren().add(indicatore);

        Label titolo = new Label(nome);
        titolo.setMinWidth(0);
        titolo.setMaxWidth(Double.MAX_VALUE);
        titolo.setTooltip(new Tooltip(nome));
        HBox.setHgrow(titolo, Priority.ALWAYS);
        Label prezzo = new Label(Presentazione.prezzo(prodotto.prezzo()));
        prezzo.setMinWidth(Region.USE_PREF_SIZE);
        HBox didascalia = new HBox(8, titolo, prezzo);
        didascalia.getStyleClass().add("didascalia");
        VBox card = new VBox(apri, didascalia);
        card.setMinWidth(0);
        return card;
    }

    private void dimensionaFoto(Region foto) {
        foto.setMinWidth(0);
        int numeroColonne = colonne;
        foto.prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(0, (getPrefWidth() - (numeroColonne - 1) * SPAZIO) / numeroColonne * 1.45),
                prefWidthProperty()));
        foto.setMinHeight(Region.USE_PREF_SIZE);
    }
}
