package com.example;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;



    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Traffic Simulation");
        //                                           junction arm: top - right - bottom - left
        SimulationComponents simComponent = new SimulationComponents(
                3,4,3,3, false,
                false, true,
                true, true,
                true, false,
                true, true,
                true,false,
                false, false
        );
        AnchorPane root = simComponent.getRoot();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(148,148,148), CornerRadii.EMPTY, Insets.EMPTY)));
        stage.setScene(new Scene(root, SimulationComponents.sim_w,SimulationComponents.sim_h));
        stage.show();

        for (Rectangle rect : simComponent.getCorners()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect : simComponent.getLane_separation()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect : simComponent.getCrossings()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect: simComponent.getArrows()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect: simComponent.getLights()){
            root.getChildren().add(rect);
        }
//        root.getChildren().addAll(simComponent.carsToAdd);

        // simComponent.turn_right(3, 0);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(33), e -> animation(root, simComponent))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();

        // Rectangle car1 = simComponent.get_first_car("right",1).getShape();

    }



    private static  void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private void animation(AnchorPane root, SimulationComponents simComponent){
        // Rectangle car1 = simComponent.get_first_car("right",1).getShape();
        // car1.setY(car1.getY()-1);
        simComponent.update(root);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}