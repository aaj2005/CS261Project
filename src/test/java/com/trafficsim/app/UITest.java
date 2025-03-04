package com.trafficsim.app;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.Start;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import javafx.stage.Stage;
//import javafx.scene.StackPane;
import javafx.scene.control.Button;


@ExtendWith(ApplicationExtension.class)
public class UITest {
    private Button run_button;

    @Start
    private void start(Stage stage) {
//        run_button = new Button("Run");
//        run_button.setOnAction(event -> run_button.setDisable(true));
//
//        StackPane root = new StackPane(run_button);
//        Scene scene = new Scene(root, 300, 200);
//        stage.setScene(scene);
//        stage.show();
    }
    @Test
    void testClick(FxRobot robot) {
        robot.clickOn(run_button);
        System.out.println("Click test running");

        Assertions.assertThat(run_button.isDisabled()).isTrue();
    }
}
