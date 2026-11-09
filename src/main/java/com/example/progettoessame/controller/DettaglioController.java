package com.example.progettoessame.controller;

import com.example.progettoessame.model.Prodotto;
import com.example.progettoessame.view.Presentazione;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public final class DettaglioController {
    @FXML private ScrollPane scorrimentoFoto;
    @FXML private VBox foto;
    @FXML private Label nome;
    @FXML private Label prezzo;
    @FXML private Label descrizione;
    @FXML private FlowPane taglie;
    @FXML private Label stato;
    @FXML private Button indietro;
    @FXML private Button aggiungi;

    void imposta(Prodotto prodotto, DialoghiNegozio dialoghi, Runnable tornaIndietro) {
        nome.setText(prodotto.nomeVisualizzato());
        prezzo.setText(Presentazione.prezzo(prodotto.prezzo()));
        descrizione.setText(prodotto.descrizione() == null ? "" : prodotto.descrizione().strip());
        aggiungiFoto(prodotto.immagine());
        if (prodotto.immagineRetro() != null && !prodotto.immagineRetro().isBlank()) {
            aggiungiFoto(prodotto.immagineRetro());
        }

        ToggleGroup gruppo = new ToggleGroup();
        for (String taglia : List.of("XS", "S", "M", "L", "XL", "XXL")) {
            ToggleButton bottone = new ToggleButton(taglia);
            bottone.setToggleGroup(gruppo);
            taglie.getChildren().add(bottone);
        }

        indietro.setOnAction(evento -> tornaIndietro.run());
        aggiungi.setOnAction(evento -> {
            stato.setText("");
            if (!dialoghi.puoAggiungere()) {
                return;
            }
            if (!(gruppo.getSelectedToggle() instanceof ToggleButton selezione)) {
                stato.setText("Seleziona una taglia.");
                return;
            }
            if (dialoghi.aggiungi(prodotto, selezione.getText())) {
                stato.setText("Aggiunto al carrello.");
            }
        });
    }

    private void aggiungiFoto(String percorso) {
        Region immagine = Presentazione.foto(percorso, 1040, false);
        immagine.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
            var viewport = scorrimentoFoto.getViewportBounds();
            return Math.min(viewport.getWidth() * 1.2, viewport.getHeight());
        }, scorrimentoFoto.viewportBoundsProperty()));
        immagine.setMinHeight(Region.USE_PREF_SIZE);
        foto.getChildren().add(immagine);
    }
}
