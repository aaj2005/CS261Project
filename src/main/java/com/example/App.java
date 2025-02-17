package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
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
        //                                           junction arm: top - right - bottom - left
        SimulationComponents simComponent = new SimulationComponents(1,5,1,1);
        AnchorPane root = new AnchorPane();

        stage.setScene(new Scene(root, SimulationComponents.sim_w,SimulationComponents.sim_h));
        stage.show();

        for (Rectangle rect : simComponent.getCorners()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect : simComponent.getLane_separation()){
            root.getChildren().add(rect);
        }

        Image img = new Image("car2dtopview.png");
        Rectangle car = new Rectangle(18,43.6,new ImagePattern(img));

        car.setX(300);
        car.setY(350);

//        Rectangle car2 = new Rectangle(18,43.6,new ImagePattern(img));
//
//
//        car2.setX(Car.CAR_HEIGHT);
//        car2.setY(0);
//        Rotate rotate = new Rotate();
//        rotate.setPivotX(car2.getX());
//        rotate.setPivotY(car2.getY());
//        rotate.setAngle(90);
//        car2.getTransforms().add(rotate);

//        root.getChildren().add(car);
        root.getChildren().addAll(simComponent.carsToAdd);

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