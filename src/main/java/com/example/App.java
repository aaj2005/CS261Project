package com.example;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.util.Duration;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        Font.loadFont(getClass().getResource("fonts/IstokWeb-Regular.ttf").toExternalForm(), 18);
        Font.loadFont(getClass().getResource("fonts/IstokWeb-Bold.ttf").toExternalForm(), 18);

        scene = new Scene(loadFXML("proj"), 1920, 1200);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Traffic Simulation");
        //                                           junction arm: top - right - bottom - left
//        SimulationComponents simComponent = new SimulationComponents(
//                3,4,3,3, true,
//                true, true,
//                true, true,
//                true, true,
//                true, true,
//                true,false,
//                false, false
//        );
//        AnchorPane root = simComponent.getRoot();

//        stage.setScene(new Scene(root, SimulationComponents.sim_w,SimulationComponents.sim_h));
        stage.show();

    }



    private static  void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }



    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }


}