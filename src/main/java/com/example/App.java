package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        DynamicComponents.junction_elements = new ArrayList<>(Arrays.asList(new JunctionElement[] {
            new Road(3, 350, 2),
            new Road(2, 50, 2),
            new Road(0, 50, 2),
            new Road(0, 50, 2),
            new PedestrianCrossing(0, 1)
        }));

        try {
            StatCalculator sc = new StatCalculator(0);
            System.out.println(sc.run().toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        System.out.println("done");
    }

}