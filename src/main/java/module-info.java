module com.example.progettoessame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.progettoessame to javafx.fxml;
    exports com.example.progettoessame;
}