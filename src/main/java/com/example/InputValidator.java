package com.example;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class InputValidator {
    public static void restrictToNumbers(TextField textField, int maxValue) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.matches("[0-9]*")) {
                // Ensure the number does not exceed the maxValue
                if (!newText.isEmpty()) {
                    try {
                        int value = Integer.parseInt(newText);
                        if (value > maxValue) {
                            return null; // Reject change if it exceeds maxValue
                        }
                    } catch (NumberFormatException e) {
                        return null; // Reject invalid numbers
                    }
                }
                return change; // Allow valid numbers
            }
            return null; // Reject invalid input
        }));

        // Ensure empty text fields reset to "0" when focus is lost
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                if (textField.getText().isEmpty()) {
                    textField.setText("0");
                } else {
                    // Ensure value does not exceed maxValue when focus is lost
                    int value = Integer.parseInt(textField.getText());
                    if (value > maxValue) {
                        textField.setText(String.valueOf(maxValue));
                    }
                }
            }
        });
    }
}
