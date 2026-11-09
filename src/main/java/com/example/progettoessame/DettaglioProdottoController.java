package com.example.progettoessame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;

public class DettaglioProdottoController {
    private Prodotto prodottoSpecifico;
    @FXML
    private VBox Acquisto;

    @FXML
    private ScrollPane Colonna_Centrale;

    @FXML
    private Button btnAggiungi;

    @FXML
    private VBox contenitoreFoto;

    @FXML
    private HBox contenitoreTaglie;

    @FXML
    private ImageView imgFronte;

    @FXML
    private ImageView imgRetro;

    @FXML
    private VBox info;

    @FXML
    private Label lblDescrizione;

    @FXML
    private Label lblNome;

    @FXML
    private Label lblPrezzo;
    @FXML private Button btnXS;
    @FXML private Button btnS;
    @FXML private Button btnM;
    @FXML private Button btnL;
    @FXML private Button btnXL;
    @FXML private Button btnXXL;

    private Button bottoneSelezionato;



    @FXML
    public void initialize(){


        contenitoreFoto.setMaxWidth(Double.MAX_VALUE);
        contenitoreFoto.widthProperty().addListener((obs, oldVal, newVal) -> {
            double larghezzaReale = newVal.doubleValue() - 2.0;
            if (larghezzaReale > 0) {
                imgFronte.setFitWidth(larghezzaReale);
                imgRetro.setFitWidth(larghezzaReale);
            }
        });
    }

    public void caricaDatiProdotto(String nome, double prezzo, String descrizione, String pathFronte, String pathRetro){
        lblNome.setText(nome.toUpperCase());
        lblPrezzo.setText(String.format("%.2f EUR", prezzo));
        lblDescrizione.setText(descrizione);
        try {
            imgFronte.setImage(new Image(getClass().getResourceAsStream(pathFronte)));
            imgRetro.setImage(new Image(getClass().getResourceAsStream(pathRetro)));
        } catch (Exception e) {
            System.err.println("Errore nel caricamento delle immagini del prodotto.");

        }
    }
    @FXML
    void gestisciSelezioneTaglia(javafx.event.ActionEvent event) {
        Button bottoneCliccato = (Button) event.getSource();

        if (bottoneSelezionato != null) {
            bottoneSelezionato.getStyleClass().remove("button-taglia-selezionato");
        }

        bottoneCliccato.getStyleClass().add("button-taglia-selezionato");

        bottoneSelezionato = bottoneCliccato;

        String tagliaScelta = bottoneCliccato.getText();
        System.out.println("Taglia selezionata: " + tagliaScelta);
    }



    public void aggiungiAlCarrello(javafx.event.ActionEvent event) {
        String nomeVestito = lblNome.getText();

        DatabaseManager.copiaNelCarrello(nomeVestito);
        System.out.println("Query inviata per il prodotto: " + nomeVestito);

    }
}
