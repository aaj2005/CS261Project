package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;



    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Traffic Simulation");
        SimulationComponents simComponent = new SimulationComponents(5,4,3,2);
        AnchorPane root = new AnchorPane();
        for (Rectangle rect : simComponent.getCorners()){
            System.out.println("Dim: "+rect.getX() + " " + rect.getY() + " " + rect.getWidth() + " " + rect.getHeight() );
            root.getChildren().add(rect);
        }
        for (Rectangle rect : simComponent.getLane_separation()){
//            System.out.println("Dim: "+rect.getX() + " " + rect.getY() + " " + rect.getWidth() + " " + rect.getHeight() );
            root.getChildren().add(rect);
        }
        stage.setScene(new Scene(root, 600,600));
        stage.show();
    }

    static  void setRoot(String fxml) throws IOException {
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