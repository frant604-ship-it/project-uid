package com.example.progettoessame;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ElementoCarrelloController {
    private CarrelloPopupController carrelloController;
    @FXML private ImageView fotoProdotto;
    @FXML private Label lblNome;
    @FXML private Label lblPrezzo;
    @FXML private Button btnElimina;

    public void impostaDati(Prodotto prodotto) {
        System.out.println("DEBUG PRODOTTO -> Nome: " + prodotto.getNome() + " | Prezzo: " + prodotto.getPrezzo() + " | Foto: " + prodotto.getFotoPath());
        lblNome.setText(prodotto.getNome());
        lblPrezzo.setText(String.valueOf(prodotto.getPrezzo()) + " €");

        if (prodotto.getFotoPath() != null && !prodotto.getFotoPath().isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream(prodotto.getFotoPath()));
                fotoProdotto.setImage(img);
            } catch (Exception e) {
                System.err.println("Impossibile caricare l'immagine del carrello: " + e.getMessage());
            }
        }
    }
    public void setCarrelloController(CarrelloPopupController carrelloController) {
        this.carrelloController = carrelloController;
    }


    @FXML
    void rimuoviProdotto(javafx.event.ActionEvent event) {
        System.out.println("cestino premuti");
        String nomeProdotto = lblNome.getText();

        DatabaseManager.rimuoviDalCarrello(nomeProdotto);

        if (carrelloController != null) {
            carrelloController.rinfrescaSchermataCarrello();
            System.out.println(" Carrello aggiornato e totale ricalcolato!");
        }
    }
}
