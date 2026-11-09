package com.example.progettoessame;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import javafx.scene.image.ImageView;

public class ControllerHome {
    @FXML private VBox contenitoreImmagini;
    @FXML private ImageView img1;
    @FXML private ImageView img2;
    @FXML private ImageView img3;
    @FXML private ImageView img4;
    @FXML private ScrollPane scrollPaneHome;
    @FXML
    public void initialize() {
        System.out.println("HomeController avviato correttamente!");
        img1.setFitWidth(0);
        img1.setFitHeight(0);
        img2.setFitWidth(0);
        img2.setFitHeight(0);
        img3.setFitWidth(0);
        img3.setFitHeight(0);
        img4.setFitWidth(0);
        img4.setFitHeight(0);


        contenitoreImmagini.setMaxWidth(Double.MAX_VALUE);
        contenitoreImmagini.widthProperty().addListener((obs, oldVal, newVal) -> {
            double larghezzaReale = newVal.doubleValue();
            if (larghezzaReale > 0) {
                img1.setFitWidth(larghezzaReale);
                img2.setFitWidth(larghezzaReale);
                img3.setFitWidth(larghezzaReale);
                img4.setFitWidth(larghezzaReale);
            }
        });


    }
}
