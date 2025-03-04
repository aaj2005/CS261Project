package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
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

        Font.loadFont(getClass().getResource("fonts/IstokWeb-Regular.ttf").toExternalForm(), 18);
        Font.loadFont(getClass().getResource("fonts/IstokWeb-Bold.ttf").toExternalForm(), 18);

        scene = new Scene(loadFXML("proj"), 1920, 1200);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
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
            new Road(10, 300, 1),
            new Road(10, 300, 1),
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