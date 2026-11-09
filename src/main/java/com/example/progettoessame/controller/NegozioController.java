package com.example.progettoessame.controller;

import com.example.progettoessame.model.Prodotto;
import com.example.progettoessame.model.SessioneUtente;
import com.example.progettoessame.repository.CarrelloRepository;
import com.example.progettoessame.repository.CatalogoRepository;
import com.example.progettoessame.repository.Database;
import com.example.progettoessame.repository.UtenteRepository;
import com.example.progettoessame.view.GrigliaCatalogo;
import com.example.progettoessame.view.Presentazione;
import com.example.progettoessame.view.Risorse;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class NegozioController {
    private static final PseudoClass SU_HERO = PseudoClass.getPseudoClass("su-hero");
    private static final PseudoClass ATTIVA = PseudoClass.getPseudoClass("attiva");
    private static final List<Long> VETRINA = List.of(52L, 65L, 42L, 34L, 30L);
    private final SessioneUtente sessione = new SessioneUtente();
    private final Map<String, Image> miniature = new HashMap<>();

    @FXML private StackPane radice;
    @FXML private StackPane pagina;
    @FXML private ScrollPane scorrimento;
    @FXML private StackPane testata;
    @FXML private ImageView logo;
    @FXML private Button ricerca;
    @FXML private Button account;
    @FXML private Button borsa;
    @FXML private Button uomo;
    @FXML private Button donna;
    @FXML private Button tutti;

    private CatalogoRepository catalogo;
    private DialoghiNegozio dialoghi;
    private Region hero;
    private Image logoBianco;
    private Image logoNero;

    public void avvia(Database database) {
        catalogo = new CatalogoRepository(database);
        dialoghi = new DialoghiNegozio(radice, sessione, new UtenteRepository(database), new CarrelloRepository(database));

        logoBianco = Risorse.immagine("immagini/Nuove Immagini/Logo Bianco", 640, false);
        logoNero = Risorse.immagine("immagini/Nuove Immagini/Logo Nero", 640, false);
        icona(ricerca, "M9 15.5a6.5 6.5 0 1 1 0-13a6.5 6.5 0 0 1 0 13 M14 14l6 6");
        icona(account, "M11 11a4 4 0 1 1 0-8a4 4 0 0 1 0 8 M3 20a8 8 0 0 1 16 0");
        icona(borsa, "M3 7h16v14H3z M7.5 7V5a3.5 3.5 0 0 1 7 0v2");

        scorrimento.vvalueProperty().addListener((osservato, prima, dopo) -> aggiornaTestata());
        scorrimento.viewportBoundsProperty().addListener((osservato, prima, dopo) -> aggiornaTestata());
        mostraTutti();
    }

    @FXML
    private void mostraTutti() {
        seleziona(tutti);
        hero = Presentazione.foto("immagini/Nuove Immagini/Main HomePage.jpg", 0, true);
        hero.prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(480, radice.getHeight()), radice.heightProperty()));
        hero.setMinHeight(Region.USE_PREF_SIZE);

        VBox sezione = creaCatalogo("Nuovi arrivi", "", "", true);
        sezione.getStyleClass().add("vetrina");
        mostraCatalogo(new VBox(hero, sezione));
    }

    @FXML private void mostraUomo() { mostraCollezione(uomo, "Uomo", "uomo", ""); }
    @FXML private void mostraDonna() { mostraCollezione(donna, "Donna", "donna", ""); }
    @FXML private void apriAccount() { dialoghi.account(); }
    @FXML private void apriCarrello() { dialoghi.carrello(); }

    @FXML
    private void cerca() {
        dialoghi.cerca().filter(testo -> !testo.isBlank()).ifPresent(testo ->
                mostraCollezione(null, "Risultati per «" + testo.strip() + "»", "", testo));
    }

    private void mostraCollezione(Button voce, String titolo, String sesso, String testo) {
        seleziona(voce);
        hero = null;
        VBox sezione = creaCatalogo(titolo, sesso, testo, false);
        sezione.getStyleClass().add("pagina-interna");
        mostraCatalogo(sezione);
    }

    private void seleziona(Button voce) {
        for (Button pulsante : List.of(uomo, donna, tutti)) {
            pulsante.pseudoClassStateChanged(ATTIVA, pulsante == voce);
        }
    }

    private VBox creaCatalogo(String titolo, String sesso, String testo, boolean editoriale) {
        ComboBox<String> categoria = new ComboBox<>();
        categoria.setAccessibleText("Categoria");
        categoria.getStyleClass().add("filtro-categoria");
        categoria.getItems().addAll("Tutte le categorie", "Magliette", "Pantaloni", "Felpe");
        categoria.getSelectionModel().selectFirst();

        CheckBox soloDonna = new CheckBox("Solo donna");
        soloDonna.getStyleClass().add("filtro-donna");
        HBox filtri = new HBox(8, categoria);
        filtri.setAlignment(Pos.CENTER_RIGHT);
        if (editoriale) {
            filtri.getChildren().add(soloDonna);
        }
        VBox sezione = sezione(titolo, filtri);
        DoubleBinding larghezzaGriglia = Bindings.createDoubleBinding(
                () -> Math.max(0, scorrimento.getViewportBounds().getWidth()
                        - sezione.getInsets().getLeft() - sezione.getInsets().getRight()),
                scorrimento.viewportBoundsProperty(), sezione.insetsProperty());
        VBox risultati = new VBox();
        sezione.getChildren().add(risultati);
        Runnable aggiorna = () -> {
            String filtro = categoria.getSelectionModel().getSelectedIndex() <= 0
                    ? "" : categoria.getValue().toLowerCase(Locale.ROOT);
            riempi(risultati, larghezzaGriglia, soloDonna.isSelected() ? "donna" : sesso, filtro, testo, editoriale);
        };
        categoria.valueProperty().addListener((osservato, precedente, selezione) -> aggiorna.run());
        soloDonna.setOnAction(evento -> aggiorna.run());
        aggiorna.run();
        return sezione;
    }

    private void riempi(VBox area, DoubleBinding larghezza, String sesso, String categoria, String testo, boolean editoriale) {
        try {
            List<Prodotto> prodotti = catalogo.cerca(sesso, categoria, testo);
            if (editoriale) {
                prodotti.sort(Comparator.comparingInt(prodotto -> {
                    int posizione = VETRINA.indexOf(prodotto.id());
                    return posizione < 0 ? VETRINA.size() : posizione;
                }));
            }

            if (prodotti.isEmpty()) {
                area.getChildren().setAll(messaggio("Nessun capo trovato."));
            } else {
                area.getChildren().setAll(new GrigliaCatalogo(prodotti, editoriale, miniature, larghezza, this::apriDettaglio));
            }
        } catch (SQLException errore) {
            area.getChildren().setAll(messaggio("Impossibile caricare i capi. Riapri il catalogo per riprovare."));
            dialoghi.errore(errore);
        }
    }

    private void apriDettaglio(Prodotto prodotto) {
        double posizione = scorrimento.getVvalue();
        Region heroPrecedente = hero;

        try {
            var loader = Risorse.vista("dettaglio.fxml");
            Node dettaglio = loader.load();
            DettaglioController controller = loader.getController();
            controller.imposta(prodotto, dialoghi, () -> {
                hero = heroPrecedente;
                pagina.getChildren().setAll(scorrimento);
                radice.requestFocus();
                radice.layout();
                scorrimento.setVvalue(posizione);
                aggiornaTestata();
            });
            hero = null;
            pagina.getChildren().setAll(dettaglio);
            radice.requestFocus();
            aggiornaTestata();
        } catch (IOException errore) {
            dialoghi.errore(errore);
        }
    }

    private void mostraCatalogo(Node contenuto) {
        scorrimento.setContent(contenuto);
        if (scorrimento.getParent() != pagina) {
            pagina.getChildren().setAll(scorrimento);
        }
        radice.requestFocus();
        scorrimento.setVvalue(0);
        aggiornaTestata();
    }

    private void aggiornaTestata() {
        Node pagina = scorrimento.getContent();
        if (pagina == null) {
            return;
        }
        double altezzaScorribile = Math.max(0,
                pagina.getLayoutBounds().getHeight() - scorrimento.getViewportBounds().getHeight());
        double spostamento = scorrimento.getVvalue() * altezzaScorribile;
        boolean suHero = hero != null && spostamento < hero.prefHeight(-1) - testata.getHeight() / 2;
        testata.pseudoClassStateChanged(SU_HERO, suHero);
        logo.setImage(suHero ? logoBianco : logoNero);
    }

    private static VBox sezione(String testo, Node filtro) {
        Label titolo = new Label(testo);
        titolo.getStyleClass().add("titolo-catalogo");
        titolo.setWrapText(true);
        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);
        HBox intestazione = new HBox(20, titolo, spazio, filtro);
        intestazione.setAlignment(Pos.CENTER_LEFT);
        VBox sezione = new VBox(18, intestazione);
        sezione.getStyleClass().add("sezione-catalogo");
        return sezione;
    }

    private static Label messaggio(String testo) {
        Label etichetta = new Label(testo);
        etichetta.getStyleClass().add("messaggio-vuoto");
        return etichetta;
    }

    private static void icona(Button pulsante, String percorso) {
        SVGPath tracciato = new SVGPath();
        tracciato.setContent(percorso);
        tracciato.getStyleClass().add("icona");
        pulsante.setGraphic(tracciato);
    }
}
