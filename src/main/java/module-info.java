module com.example.se201_bookmanager {
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.desktop;
    requires java.compiler;
    requires org.jsoup;

    opens server.models to org.hibernate.orm.core;

    opens com.example.se201_bookmanager to javafx.fxml;
    exports com.example.se201_bookmanager;
}