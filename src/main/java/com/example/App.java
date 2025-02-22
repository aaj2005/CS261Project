package com.example;

import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        SimulationComponents simComponent = new SimulationComponents(1,1,1,1);
        AnchorPane root = new AnchorPane();
        stage.setScene(new Scene(root, SimulationComponents.sim_w,SimulationComponents.sim_h));
        stage.show();

        for (Rectangle rect : simComponent.getCorners()){
            root.getChildren().add(rect);
        }
        for (Rectangle rect : simComponent.getLane_separation()){
            root.getChildren().add(rect);
        }
        root.getChildren().addAll(simComponent.carsToAdd);
        double carX = simComponent.carsToAdd[2].getX() + Car.CAR_WIDTH/2;
        double carY = simComponent.carsToAdd[2].getY();

        double startX_trans = simComponent.to_00_coordinate(carX, carY)[0];
        double startY_trans = simComponent.to_00_coordinate(carX, carY)[1];

        double endX = simComponent.from_00_coordinates(startY_trans*-1 ,startX_trans*-1)[0]+Car.CAR_WIDTH;
        double endY = simComponent.from_00_coordinates(startY_trans*-1,startX_trans*-1 )[1];
        MoveTo moveTo = new MoveTo();
        moveTo.setX(carX);
        moveTo.setY(carY);
        double pivot_point_x = carX;
        double pivot_point_y = endY;

        QuadCurveTo quad = new QuadCurveTo();
        quad.setX(endX);
        quad.setY(endY);
        quad.setControlX(pivot_point_x);
        quad.setControlY(pivot_point_y);

        double totalDuration = 1.5; // seconds
        Path path = new Path();
        path.getElements().add(moveTo);
        path.getElements().add (quad);
        PathTransition pathTransition = new PathTransition();
//        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setDuration(Duration.millis(totalDuration*1000));
        pathTransition.setNode(simComponent.carsToAdd[2]);
        pathTransition.setPath(path);
//        pathTransition.setCycleCount(4);


        AnimationTimer timer = new AnimationTimer() {
            double startTime = 0;
            @Override
            public void handle(long now) {
                double elapsedTime = (now - startTime); // Convert ns to seconds
//                System.out.println(startTime);
                if (startTime == 0.0){
                    startTime = elapsedTime;
                    elapsedTime = (now - startTime);
//                    System.out.println(elapsedTime +  "IN");
                }
                if (elapsedTime/1_000_000_000.0 >= totalDuration) {
                    startTime = 0;
                    elapsedTime = totalDuration* 1_000_000_000.0 ; // Clamp at 2s
                }

                double progress = (elapsedTime/1_000_000_000.0) / totalDuration; // Normalize [0,1]
                double newAngle = //Math.atan(1);
                        Math.atan(
                        (-2*(1-progress)*carY + 2*pivot_point_y* (-2 * progress + 1)+2*progress*endY)
                            /(-2*(1-progress)*carX + 2*pivot_point_x* (-2 * progress + 1)+2*progress*endX)
                        )* 180/Math.PI+90;
                System.out.println(elapsedTime/1_000_000_000.0);
//                System.out.println(newAngle);
                simComponent.carsToAdd[2].setRotate(newAngle);
                pathTransition.jumpTo(Duration.seconds(progress*totalDuration));
                if ((elapsedTime/1_000_000_000.0) >= totalDuration) {
                    stop();
                } // Stop after reaching 180°
            }
        };
        pathTransition.play();
        timer.start();





        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(33), e -> animation(root, simComponent))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();

        Rectangle car1 = simComponent.get_first_car("right",1).getShape();


//        PathTransition path_transition = new PathTransition();

    }



    private static  void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private void animation(AnchorPane root, SimulationComponents simComponent){
        Rectangle car1 = simComponent.get_first_car("right",1).getShape();
        car1.setY(car1.getY()-1);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}