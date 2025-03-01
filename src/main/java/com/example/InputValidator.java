package com.example;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class InputValidator {
    public static void restrictToNumbers(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change ->
                (change.getText().matches("[0-9]*")) ? change : null));
    }
}
