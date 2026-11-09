package com.example.progettoessame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
public class CardController {
    @FXML
    private ImageView imgProdotto;
    @FXML
    private Label lblNome;
    @FXML
    private Label lblPrezzo;

    private String nomeProdotto;
    private double prezzoProdotto;
    private String fotoFrontePath;
    private String fotoRetroPath;
    private String descrizioneProdotto;

    public void setDatiProdotto(String nome, double prezzo, String fotoPath, String fotoRetro, String descrizione ) {

        this.nomeProdotto = nome;
        this.prezzoProdotto = prezzo;
        this.fotoFrontePath = fotoPath;
        this.fotoRetroPath = fotoRetro;
        this.descrizioneProdotto = descrizione;

        lblNome.setText(nome);
        lblPrezzo.setText(String.format("€ %.2f", prezzo));

        try {
            Image image = new Image(getClass().getResourceAsStream(fotoPath));
            imgProdotto.setImage(image);
        } catch (Exception e) {
            System.err.println("Impossibile trovare l'immagine: " + fotoPath);
        }
    }
    @FXML
    void apriDettaglio(javafx.event.Event event) {
        try {
            System.out.println("va");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("paneEvidenzia.fxml"));
            Parent schermataDettaglio = loader.load();
            DettaglioProdottoController controllerDettaglio = loader.getController();

            String[] dettagliExtra = DatabaseManager.getDettagliProdotto(nomeProdotto);

            String descrizioneReale = dettagliExtra[0];
            String fotoRetroReale = dettagliExtra[1];

            System.out.println("--- VERIFICA FINALE CARD CONTROLLER ---");
            System.out.println("Descrizione pronta per il dettaglio: [" + descrizioneReale + "]");

            controllerDettaglio.caricaDatiProdotto(
                    nomeProdotto,
                    prezzoProdotto,
                    descrizioneReale,
                    fotoFrontePath,
                    fotoRetroReale
            );

            HelloController.getIstanza().caricaSchermataCentraleDinamica(schermataDettaglio);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
