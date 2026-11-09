module com.example.progettoessame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    exports com.example.progettoessame;
    opens com.example.progettoessame.controller to javafx.fxml;
}
