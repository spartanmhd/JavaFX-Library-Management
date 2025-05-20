module com.example.librarymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.example.librarymanagement to javafx.fxml;
    opens com.example.librarymanagement.controller to javafx.fxml;
    opens com.example.librarymanagement.model to javafx.base;
    exports com.example.librarymanagement;
}
