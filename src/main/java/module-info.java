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

    exports client;
    opens client.controllers to javafx.fxml;
    exports client.dto;
    exports server.models;
    exports server.dao;
    exports server.service;
    exports shared;
}