module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive ejml.simple;

    opens com.example to javafx.fxml;
    exports com.example;
}
