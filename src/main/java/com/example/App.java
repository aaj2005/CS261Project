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
        DynamicComponents.roads = new Road[] {
            new Road(10, new double[] {3,0,0,0}, 4, Cardinal.N, true, true),
            new Road(10, new double[] {13.2,0,0,0}, 2, Cardinal.E, true, true),
            new Road(4, new double[] {1,0,0,0}, 1, Cardinal.S, true, false),
            new Road(8.9, new double[] {0,0,10,0}, 2, Cardinal.W)
        };

        DynamicComponents.pedestrian_crossing = new PedestrianCrossing(1, 9.98);

        try {
            StatWrapper sc = new StatWrapper(DynamicComponents.roads);
            System.out.println(sc.run().toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        System.out.println("done");
    }

}