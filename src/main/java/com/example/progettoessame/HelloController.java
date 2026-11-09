package com.example.progettoessame;


import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class HelloController {
    private static HelloController istanza;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button btnBurger;

    @FXML
    private Button btnLogo;

    @FXML
    private TextField txtCerca;
    @FXML private Button btnBorsa;
    @FXML
    private VBox sidebar;
    private boolean sidebarAperta = false;
    private boolean menuUtenteAperto = false;
    @FXML
    private VBox boxMenuUtente;
    private Label lblBenvenutoHome;
    private ProdottiController prodottiController;
    @FXML
    private void gestisciSidebar() {
        double larghezzaSidebar = sidebar.getPrefWidth();

        if (!sidebarAperta) {
            sidebar.setVisible(true);

            sidebar.setTranslateX(-larghezzaSidebar);

            TranslateTransition entra = new TranslateTransition(Duration.millis(250), sidebar);
            entra.setToX(0);
            entra.play();

            sidebarAperta = true;
        } else {
            TranslateTransition esce = new TranslateTransition(Duration.millis(250), sidebar);
            esce.setToX(-larghezzaSidebar);

            esce.setOnFinished(event -> sidebar.setVisible(false));
            esce.play();

            sidebarAperta = false;
        }

    }

    @FXML
    private ToggleButton btnUomo;
    @FXML
    private ToggleButton btnDonna;

    private ToggleGroup gruppoSesso;
    @FXML
    private AnchorPane centroContenitore;

    private Parent schermataAttuale = null;
    @FXML
    private Button btnUtente;
    @FXML
    private Button btnMagliette;
    @FXML
    private Button btnPantaloni;
    @FXML
    private Button BtnScarpe;
    @FXML
    public void initialize() {
        istanza = this;
        gruppoSesso = new ToggleGroup();
        btnUomo.setToggleGroup(gruppoSesso);
        btnDonna.setToggleGroup(gruppoSesso);

        btnUomo.setSelected(true);
        caricaSchermataCentrale("PaneHome.fxml");

    }

    public static HelloController getIstanza() {
        return istanza;
    }
    public void caricaSchermataCentraleDinamica(Parent nuovaSchermata) {
        try {
            if (schermataAttuale != null) {
                centroContenitore.getChildren().remove(schermataAttuale);
            }

            schermataAttuale = nuovaSchermata;

            AnchorPane.setTopAnchor(schermataAttuale, 0.0);
            AnchorPane.setBottomAnchor(schermataAttuale, 0.0);
            AnchorPane.setLeftAnchor(schermataAttuale, 0.0);
            AnchorPane.setRightAnchor(schermataAttuale, 0.0);

            centroContenitore.getChildren().add(schermataAttuale);
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dinamico della schermata");
            e.printStackTrace();
        }
    }

    private void caricaSchermataCentrale(String fxmlFile) {
        try {
            if (schermataAttuale != null) {
                centroContenitore.getChildren().remove(schermataAttuale);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            schermataAttuale = loader.load();

            AnchorPane.setTopAnchor(schermataAttuale, 0.0);
            AnchorPane.setBottomAnchor(schermataAttuale, 0.0);
            AnchorPane.setLeftAnchor(schermataAttuale, 0.0);
            AnchorPane.setRightAnchor(schermataAttuale, 0.0);

            centroContenitore.getChildren().add(schermataAttuale);

        } catch (IOException e) {
            System.err.println("Impossibile caricare il file: " + fxmlFile);
            e.printStackTrace();
        }

    }


    public void clickMagliette(MouseEvent mouseEvent) {
        cambiaSchermataProdotti("magliette");
    }
    @FXML
    private void clickPantaloni(javafx.event.ActionEvent event) {
        cambiaSchermataProdotti("pantaloni");
    }
    @FXML
    private void clickScarpe(javafx.event.ActionEvent event) {
        cambiaSchermataProdotti("felpe");
    }


    private void cambiaSchermataProdotti(String categoria) {
        System.out.println("ciao");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Paneprodotti.fxml"));
            Parent pannelloProdotti = loader.load();

            centroContenitore.getChildren().clear();
            centroContenitore.getChildren().add(pannelloProdotti);

            AnchorPane.setTopAnchor(pannelloProdotti, 0.0);
            AnchorPane.setBottomAnchor(pannelloProdotti, 0.0);
            AnchorPane.setLeftAnchor(pannelloProdotti, 0.0);
            AnchorPane.setRightAnchor(pannelloProdotti, 0.0);

            ProdottiController prodottiCtrl = loader.getController();

            this.prodottiController = prodottiCtrl;

            String sessoSelezionato = btnUomo.isSelected() ? "uomo" : "donna";

            prodottiCtrl.impostaProdotti(categoria, sessoSelezionato);

        } catch (IOException e) {
            System.err.println("Errore nel caricamento della pagina prodotti"+getClass().getResource("Paneprodotti.fxml"));
            e.printStackTrace();
        }
    }
    @FXML
    void tornaHome(javafx.event.ActionEvent event) {
        caricaSchermataCentrale("PaneHome.fxml");
    }

    @FXML
    void mostraCarrelloPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("carrelloPopup.fxml"));
            javafx.scene.Parent carrelloGrafica = loader.load();

            CarrelloPopupController popupController = loader.getController();
            List<Prodotto> prodottiNelCarrello = DatabaseManager.getProdottiCarrello();
            popupController.aggiornaProdotti(prodottiNelCarrello);

            javafx.scene.layout.AnchorPane.setTopAnchor(carrelloGrafica, 70.0);
            javafx.scene.layout.AnchorPane.setRightAnchor(carrelloGrafica, 20.0);

            rootPane.getChildren().add(carrelloGrafica);
            rootPane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, new javafx.event.EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (!carrelloGrafica.getBoundsInParent().contains(event.getX(), event.getY())) {

                        rootPane.getChildren().remove(carrelloGrafica);

                        rootPane.removeEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, this);
                    }
                }
            });

        } catch (IOException e) {
            System.err.println(" Errore nel caricamento del carrello: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    void apriFinestraLogin(ActionEvent event) {

        if (SessioneUtente.isLoggato()) {
            boxMenuUtente.toFront();
            if (!menuUtenteAperto) {
                boxMenuUtente.setVisible(true);
                FadeTransition fade = new FadeTransition(Duration.millis(200), boxMenuUtente);
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                TranslateTransition scivola = new TranslateTransition(Duration.millis(200), boxMenuUtente);
                scivola.setFromY(-15);
                scivola.setToY(0);
                fade.play();
                scivola.play();
                menuUtenteAperto = true;
            }else {
                FadeTransition fadeClose = new FadeTransition(Duration.millis(200), boxMenuUtente);
                fadeClose.setFromValue(1.0);
                fadeClose.setToValue(0.0);
                TranslateTransition scivolaClose = new TranslateTransition(Duration.millis(200), boxMenuUtente);
                scivolaClose.setToY(-15);
                fadeClose.setOnFinished(e -> {
                    boxMenuUtente.setVisible(false);
                    boxMenuUtente.setTranslateY(0);
                });
                fadeClose.play();
                scivolaClose.play();
                menuUtenteAperto = false;
            }
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Accedi / Registrati");
            loginStage.setScene(new Scene(root, 380, 500));
            loginStage.setResizable(false);
            loginStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            loginStage.showAndWait();

            Stage homeStage = (Stage) btnUtente.getScene().getWindow();
            homeStage.toFront();
            homeStage.requestFocus();

            if (SessioneUtente.isLoggato()) {
                btnUtente.setText(SessioneUtente.getUtenteLoggato());
                lblBenvenutoHome.setText("Ciao, " + SessioneUtente.getUtenteLoggato());
            }

        } catch (Exception e) {
            System.err.println("Errore durante l'apertura del login: " + e.getMessage());
        }
    }
    @FXML
    void gestisciLogoutHome(ActionEvent event) {
        SessioneUtente.effettuaLogout();
        btnUtente.setText("");
        boxMenuUtente.setVisible(false);
        System.out.println("Disconnessione completata con successo.");
    }
    public void setProdottiController(ProdottiController prodottiController) {
        this.prodottiController = prodottiController;
    }
    @FXML
    void gestisciRicerca() {
        String testo = txtCerca.getText();
        DatabaseManager.testoCercato = testo;

        if (testo.trim().isEmpty()) {
            cambiaSchermataProdotti("tutti");
            return;
        }

        if (prodottiController == null) {
            cambiaSchermataProdotti("tutti");
        }

        if (prodottiController != null) {
            prodottiController.mostraProdottiCercati();
        }
    }
}

