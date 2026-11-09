package com.example.progettoessame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CarrelloPopupController {
    @FXML private Label lblTotale;
    @FXML private VBox vboxProdotti;
    public void initialize(URL location, ResourceBundle resources) {
        List<Prodotto> prodotti = DatabaseManager.getProdottiCarrello();
        aggiornaProdotti(prodotti);
    }

    public void aggiornaProdotti(List<Prodotto> prodotti) {
        vboxProdotti.getChildren().clear();

        if (prodotti.isEmpty()) {
            System.out.println("Il carrello è vuoto.");
            return;
        }

        for (Prodotto p : prodotti) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ElementoCarrello.fxml"));
                HBox rigaGrafica = loader.load();

                ElementoCarrelloController controllerRow = loader.getController();
                controllerRow.impostaDati(p);
                controllerRow.setCarrelloController(this);

                vboxProdotti.getChildren().add(rigaGrafica);

            } catch (IOException e) {
                System.err.println("Errore nel caricamento della riga del carrello: " + e.getMessage());
            }
            double totaleCarrello = DatabaseManager.getTotaleCarrello();

            lblTotale.setText(String.format("%.2f €", totaleCarrello));
        }
    }
    public void rinfrescaSchermataCarrello() {
        List<Prodotto> prodottiAggiornati = DatabaseManager.getProdottiCarrello();
        aggiornaProdotti(prodottiAggiornati);
    }
    @FXML
    void gestisciAcquisto() {

        DatabaseManager.svuotaCarrello();


        System.out.println(" Acquisto completato con successo!");


        rinfrescaSchermataCarrello();
    }



}
