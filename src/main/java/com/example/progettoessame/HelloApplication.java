package com.example.progettoessame;

import com.example.progettoessame.controller.NegozioController;
import com.example.progettoessame.repository.Database;
import com.example.progettoessame.view.Risorse;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Bangers");
        stage.getIcons().setAll(Risorse.iconeFinestra());

        Database database;
        try {
            database = Database.apri();
        } catch (SQLException | IOException errore) {
            Alert avviso = new Alert(Alert.AlertType.ERROR, errore.getMessage());
            avviso.initOwner(stage);
            avviso.setHeaderText("Impossibile aprire il catalogo");
            avviso.showAndWait();
            return;
        }

        var loader = Risorse.vista("negozio.fxml");
        Scene scene = new Scene(loader.load(), 1280, 800);
        NegozioController controller = loader.getController();
        controller.avvia(database);
        scene.getStylesheets().add(Risorse.css());

        stage.setMinWidth(760);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
