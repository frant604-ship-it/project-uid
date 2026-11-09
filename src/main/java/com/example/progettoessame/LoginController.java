package com.example.progettoessame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class LoginController {
    @FXML private TextField txtUserLogin;
    @FXML private PasswordField txtPassLogin;

    @FXML private TextField txtUserReg;
    @FXML private PasswordField txtPassReg;
    @FXML private PasswordField txtPassRegConferma;

    @FXML
    void gestisciLogin(ActionEvent event) {
        String username = txtUserLogin.getText().trim();
        String password = txtPassLogin.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println(" Riempi tutti i campi del login!");
            return;
        }

        boolean loginValido = DatabaseManager.verificaLogin(username, password);

        if (loginValido) {
            System.out.println(" Login effettuato con successo!");

            SessioneUtente.effettuaLogin(username);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.close();

        } else {
            System.out.println(" Errore: Username o Password errati!");
        }
    }

    @FXML
    void gestisciRegistrazione(ActionEvent event) {
        String username = txtUserReg.getText().trim();
        String password = txtPassReg.getText();
        String conferma = txtPassRegConferma.getText();

        if (username.isEmpty() || password.isEmpty() || conferma.isEmpty()) {
            System.out.println("️ Riempi tutti i campi della registrazione!");
            return;
        }

        if (!password.equals(conferma)) {
            System.out.println(" Le password non coincidono!");
            return;
        }

        String passwordHassata = DatabaseManager.hassaPassword(password);

        boolean registrato = DatabaseManager.registraUtente(username, passwordHassata);

        if (registrato) {
            System.out.println(" Utente registrato con successo nel database: " + username);

            txtUserReg.clear();
            txtPassReg.clear();
            txtPassRegConferma.clear();
        } else {
            System.out.println(" Errore: Impossibile registrarsi (l'utente esiste già?).");
        }
    }

}