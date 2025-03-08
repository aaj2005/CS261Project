    module com.example {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires javafx.graphics;


    opens com.example to javafx.fxml;
    exports com.example;
}
