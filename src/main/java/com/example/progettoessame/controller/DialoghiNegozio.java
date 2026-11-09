package com.example.progettoessame.controller;

import com.example.progettoessame.model.Prodotto;
import com.example.progettoessame.model.SessioneUtente;
import com.example.progettoessame.model.VoceCarrello;
import com.example.progettoessame.repository.CarrelloRepository;
import com.example.progettoessame.repository.UtenteRepository;
import com.example.progettoessame.view.Presentazione;
import com.example.progettoessame.view.Risorse;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

final class DialoghiNegozio {
    private final Node proprietario;
    private final SessioneUtente sessione;
    private final UtenteRepository utenti;
    private final CarrelloRepository carrello;

    DialoghiNegozio(Node proprietario, SessioneUtente sessione, UtenteRepository utenti, CarrelloRepository carrello) {
        this.proprietario = proprietario;
        this.sessione = sessione;
        this.utenti = utenti;
        this.carrello = carrello;
    }

    Optional<String> cerca() {
        Dialog<String> dialogo = finestra("Cerca nel catalogo", 480);
        TextField ricerca = new TextField();
        ricerca.setPromptText("Es. felpa, Classic…");
        ButtonType cerca = new ButtonType("Cerca", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().setAll(ButtonType.CANCEL, cerca);
        Node invia = dialogo.getDialogPane().lookupButton(cerca);
        invia.getStyleClass().add("azione");
        invia.disableProperty().bind(ricerca.textProperty().map(String::isBlank));
        dialogo.getDialogPane().setContent(campo("Nome del capo", ricerca));
        dialogo.setResultConverter(pulsante -> pulsante == cerca ? ricerca.getText().strip() : null);
        dialogo.setOnShown(evento -> Platform.runLater(ricerca::requestFocus));
        return dialogo.showAndWait();
    }

    boolean puoAggiungere() {
        if (sessione.attiva()) {
            return true;
        }

        ButtonType accedi = new ButtonType("Accedi o registrati");
        Alert avviso = new Alert(Alert.AlertType.WARNING,
                "Per aggiungere al carrello accedi o crea un account!", accedi, ButtonType.CANCEL);
        prepara(avviso, "Accesso richiesto");
        avviso.setHeaderText(null);
        if (avviso.showAndWait().orElse(ButtonType.CANCEL) == accedi) {
            account();
        }
        return sessione.attiva();
    }

    boolean aggiungi(Prodotto prodotto, String taglia) {
        try {
            if (carrello.aggiungi(sessione.username(), prodotto.id(), taglia)) {
                return true;
            }
            Alert avviso = new Alert(Alert.AlertType.WARNING, "Il capo non è più disponibile nel catalogo.");
            prepara(avviso, "Carrello");
            avviso.setHeaderText(null);
            avviso.showAndWait();
        } catch (SQLException errore) {
            errore(errore);
        }
        return false;
    }

    void account() {
        if (sessione.attiva()) {
            Dialog<Void> dialogo = finestra("Account", 410);
            Button esci = new Button("Esci");
            esci.getStyleClass().add("azione");
            esci.setOnAction(evento -> {
                sessione.esci();
                dialogo.close();
            });
            Label utente = new Label("Accesso effettuato come " + sessione.username());
            utente.setWrapText(true);
            VBox contenuto = new VBox(16, utente, esci);
            dialogo.getDialogPane().setContent(contenuto);
            dialogo.showAndWait();
            return;
        }

        Dialog<Void> dialogo = finestra("Il tuo account", 430);
        TabPane schede = new TabPane(
                new Tab("Accedi", moduloAccount(dialogo, false)),
                new Tab("Registrati", moduloAccount(dialogo, true)));
        schede.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        schede.getStyleClass().add("schede-account");
        dialogo.getDialogPane().setContent(schede);
        dialogo.showAndWait();
    }

    private VBox moduloAccount(Dialog<Void> dialogo, boolean registrazione) {
        TextField nome = new TextField();
        PasswordField password = new PasswordField();
        PasswordField conferma = new PasswordField();
        Label stato = new Label();
        stato.getStyleClass().add("errore-campo");
        stato.setWrapText(true);
        Button invia = new Button(registrazione ? "Crea account" : "Accedi");
        invia.getStyleClass().add("azione");
        invia.setMaxWidth(Double.MAX_VALUE);
        invia.setOnAction(evento -> {
            String username = nome.getText().strip();
            String segreto = password.getText();
            if (username.isEmpty() || segreto.isEmpty() || (registrazione && segreto.isBlank())) {
                stato.setText("Inserisci nome utente e password.");
                return;
            }
            if (registrazione && !segreto.equals(conferma.getText())) {
                stato.setText("Le password non coincidono.");
                return;
            }
            try {
                boolean riuscito = registrazione ? utenti.registra(username, segreto) : utenti.verifica(username, segreto);
                if (!riuscito) {
                    stato.setText(registrazione ? "Questo nome utente è già in uso." : "Nome utente o password errati.");
                    return;
                }
                sessione.accedi(username);
                dialogo.close();
            } catch (SQLException errore) {
                errore(errore);
            }
        });
        password.setOnAction(evento -> invia.fire());
        conferma.setOnAction(evento -> invia.fire());

        VBox modulo = new VBox(12, campo("Nome utente", nome), campo("Password", password));
        if (registrazione) {
            modulo.getChildren().add(campo("Conferma password", conferma));
        }
        modulo.getChildren().addAll(invia, stato);
        modulo.getStyleClass().add("modulo-account");
        return modulo;
    }

    private static VBox campo(String testo, TextField controllo) {
        Label etichetta = new Label(testo);
        etichetta.setLabelFor(controllo);
        etichetta.getStyleClass().add("etichetta-campo");
        return new VBox(6, etichetta, controllo);
    }

    void carrello() {
        if (!sessione.attiva()) {
            account();
            if (!sessione.attiva()) {
                return;
            }
        }

        Dialog<Void> dialogo = finestra("Il tuo carrello", 510);
        VBox lista = new VBox(8);
        ScrollPane scroll = new ScrollPane(lista);
        scroll.getStyleClass().addAll("scorrimento", "lista-carrello");
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPrefViewportHeight(270);
        Label totale = new Label();
        totale.getStyleClass().add("totale");
        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);
        HBox riepilogo = new HBox(12, new Label("Totale"), spazio, totale);
        riepilogo.getStyleClass().add("riepilogo-carrello");
        riepilogo.setAlignment(Pos.CENTER_LEFT);
        dialogo.getDialogPane().setContent(new VBox(16, scroll, riepilogo));
        aggiornaCarrello(lista, totale);
        dialogo.showAndWait();
    }

    private void aggiornaCarrello(VBox lista, Label totale) {
        try {
            var voci = carrello.elenco(sessione.username());
            lista.getChildren().clear();
            lista.setAlignment(voci.isEmpty() ? Pos.CENTER : Pos.TOP_LEFT);
            if (voci.isEmpty()) {
                Label vuoto = new Label("Il carrello è vuoto.");
                vuoto.getStyleClass().add("carrello-vuoto");
                lista.getChildren().add(vuoto);
            }
            BigDecimal somma = BigDecimal.ZERO;
            for (VoceCarrello voce : voci) {
                lista.getChildren().add(rigaCarrello(voce, lista, totale));
                somma = somma.add(voce.totale());
            }
            totale.setText(Presentazione.prezzo(somma));
        } catch (SQLException errore) {
            errore(errore);
        }
    }

    private HBox rigaCarrello(VoceCarrello voce, VBox lista, Label totale) {
        Region foto = Presentazione.foto(voce.immagine(), 120, false);
        foto.setPrefSize(58, 76);
        foto.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        Label nome = new Label(voce.nome().replaceAll("\\s+", " ").strip());
        nome.setWrapText(true);
        nome.getStyleClass().add("nome-voce");
        String taglia = voce.taglia() == null ? "" : " · Taglia " + voce.taglia();
        Label riepilogo = new Label(Presentazione.prezzo(voce.totale()) + " · Qtà " + voce.quantita() + taglia);
        riepilogo.getStyleClass().add("metadati-voce");
        VBox info = new VBox(4, nome, riepilogo);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button rimuovi = new Button("Rimuovi");
        rimuovi.setAccessibleText("Rimuovi " + nome.getText());
        rimuovi.setOnAction(evento -> {
            try {
                carrello.rimuovi(sessione.username(), voce.id());
                aggiornaCarrello(lista, totale);
            } catch (SQLException errore) {
                errore(errore);
            }
        });
        HBox riga = new HBox(10, foto, info, rimuovi);
        riga.getStyleClass().add("riga-carrello");
        riga.setAlignment(Pos.CENTER_LEFT);
        return riga;
    }

    void errore(Exception errore) {
        System.getLogger(DialoghiNegozio.class.getName()).log(System.Logger.Level.ERROR, "Operazione sul negozio", errore);
        Alert avviso = new Alert(Alert.AlertType.ERROR, "L’operazione non è riuscita. Riprova.");
        prepara(avviso, "Errore");
        avviso.setHeaderText(null);
        avviso.showAndWait();
    }

    private <T> Dialog<T> finestra(String titolo, double larghezza) {
        Dialog<T> dialogo = new Dialog<>();
        prepara(dialogo, titolo);
        dialogo.setHeaderText(titolo);
        dialogo.getDialogPane().setPrefWidth(larghezza);
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        return dialogo;
    }

    private void prepara(Dialog<?> dialogo, String titolo) {
        dialogo.initOwner(proprietario.getScene().getWindow());
        dialogo.setTitle(titolo);
        dialogo.getDialogPane().getStylesheets().add(Risorse.css());
        dialogo.getDialogPane().getStyleClass().add("dialogo-negozio");
    }
}
