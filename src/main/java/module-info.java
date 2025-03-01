module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires fontawesomefx;

    opens com.example to javafx.fxml;
    exports com.example;
}
