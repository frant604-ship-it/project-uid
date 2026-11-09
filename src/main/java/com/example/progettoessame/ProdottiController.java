package com.example.progettoessame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ProdottiController {
    @FXML
    public FlowPane grigliaProdotti;

    public void impostaProdotti(String categoria, String sesso) {
        try {
            grigliaProdotti.getChildren().clear();

            var listaProdotti = DatabaseManager.getProdotti(categoria, sesso);

            System.out.println("-> Il database ha trovato " + listaProdotti.size() + " prodotti per " + categoria + " - " + sesso);

            if (listaProdotti.isEmpty()) {
                System.out.println(" ATTENZIONE: La tabella prodotti è vuota o i filtri non corrispondono a nessuna riga!");
            }

            for (Prodotto p : listaProdotti) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CardProdotto.fxml"));
                Parent card = loader.load();

                CardController cardCtrl = loader.getController();
                cardCtrl.setDatiProdotto(p.getNome(), p.getPrezzo(), p.getFotoPath(), p.getFotoRetroPath(), p.getDescrizione());
                grigliaProdotti.getChildren().add(card);
            }

        } catch (IOException e) {
            System.err.println("Errore nel caricamento grafico delle card");
            e.printStackTrace();
        }

    }
    public void mostraProdottiCercati() {
        try {
            grigliaProdotti.getChildren().clear();

            List<Prodotto> listaFiltrata = DatabaseManager.getProdottiCercati();

            for (Prodotto p : listaFiltrata) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CardProdotto.fxml"));
                Parent card = loader.load();

                CardController cardCtrl = loader.getController();
                cardCtrl.setDatiProdotto(p.getNome(), p.getPrezzo(), p.getFotoPath(), p.getFotoRetroPath(), p.getDescrizione());

                grigliaProdotti.getChildren().add(card);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento grafico delle card di ricerca");
            e.printStackTrace();
        }
    }


}
