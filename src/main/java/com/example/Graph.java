package com.example;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class Graph {

    public static VBox createGraph(SimulationData[] runs) {
        // VBox to hold all the graphs
        VBox vbox = new VBox();

        // Title label with "METRICS" text
        Label titleLabel = new Label("METRICS");
        titleLabel.getStyleClass().add("title-label");

        // Create a StackPane for absolute positioning of the title
        StackPane titlePane = new StackPane();
        titlePane.getChildren().add(titleLabel);
        titlePane.setPrefSize(600, 100);  // Set the preferred size of the title pane

        // Manually set the position of the title inside the StackPane
        StackPane.setAlignment(titleLabel, Pos.CENTER);

        // Create the GridPane to hold the four charts
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20); // Horizontal gap between charts
        gridPane.setVgap(20); // Vertical gap between charts

        // Set the grid to take up the whole available width and height
        gridPane.setPrefWidth(1921);
        gridPane.setPrefHeight(800);

        // Make each column and row in the GridPane grow to fill the available space
        for (int i = 0; i < 2; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);  // Make the column grow horizontally
            gridPane.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);  // Make the row grow vertically
            gridPane.getRowConstraints().add(row);
        }

        // Create the BarChart for each metric
        BarChart<String, Number> performanceScoreChart = createChart(runs, "score");
        BarChart<String, Number> maxWaitTimeChart = createChart(runs, "maxWaitTime");
        BarChart<String, Number> avgQueueLengthChart = createChart(runs, "avgQueueLength");
        BarChart<String, Number> avgWaitTimeChart = createChart(runs, "avgWaitTime");

        // Add all the charts to the grid, in different quarters of the screen
        gridPane.add(performanceScoreChart, 0, 0); // Top-left
        gridPane.add(maxWaitTimeChart, 1, 0); // Top-right
        gridPane.add(avgQueueLengthChart, 0, 1); // Bottom-left
        gridPane.add(avgWaitTimeChart, 1, 1); // Bottom-right

        // Add the titlePane and gridPane to the VBox
        vbox.getChildren().addAll(titlePane, gridPane);

        vbox.setPrefSize(1000, 1000);
        return vbox;
    }

    private static BarChart<String, Number> createChart(SimulationData[] runs, String metric) {
        // Create the X and Y axes for the BarChart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Simulation Run");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(getYAxisLabel(metric));

        // Create BarChart using the axes
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(getChartTitle(metric));

        // Update the chart with the corresponding data
        updateChart(barChart, runs, metric);

        return barChart;
    }

    // Get Y-Axis label based on the metric
    private static String getYAxisLabel(String metric) {
        switch (metric) {
            case "score":
                return "Performance Score";
            case "maxWaitTime":
                return "Maximum Wait Time (Mins)";
            case "avgQueueLength":
                return "Max Queue Length (Cars)";
            case "avgWaitTime":
                return "Average Wait Time (Mins)";
            default:
                return "Unknown Metric";
        }
    }

    // Get chart title based on the metric
    private static String getChartTitle(String metric) {
        switch (metric) {
            case "score":
                return "Performance Score Comparisons";
            case "maxWaitTime":
                return "Maximum Wait Time Comparisons";
            case "avgQueueLength":
                return "Max Queue Length Comparisons";
            case "avgWaitTime":
                return "Average Wait Time Comparisons";
            default:
                return "Unknown Metric";
        }
    }



    private static void updateChart(BarChart<String, Number> barChart, SimulationData[] runs, String metric) {
        System.out.println("RUNS: " + runs.length);

        if (runs.length == 0 || runs == null) {
            // If there are no runs
            return;

        }
        barChart.getData().clear();

        if (metric.equals("score")) {
            XYChart.Series<String, Number> seriesScore = new XYChart.Series<>();
            barChart.getYAxis().setLabel("Performance Score");
            seriesScore.setName("Performance Score");

            for (SimulationData run : runs) {
                seriesScore.getData().add(new XYChart.Data<>(run.getName(), run.getScore()));
            }

            barChart.getData().add(seriesScore);
            return;
        }

        Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();
        String[] directions = {"North", "East", "South", "West"};

        for (int i = 0; i < directions.length; i++) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(directions[i]);
            seriesMap.put(directions[i], series);
        }

        for (SimulationData run : runs) {
            Double[] values;
            switch (metric) {
                case "maxWaitTime":
                    values = run.getMaxWaitTime();
                    barChart.getYAxis().setLabel("Maximum Wait Time (Mins)");
                    break;
                case "avgQueueLength":
                    values = run.getMaxQueueLength();
                    barChart.getYAxis().setLabel("Max Queue Length (Cars)");
                    break;
                case "avgWaitTime":
                    values = run.getAvgWaitTime();
                    barChart.getYAxis().setLabel("Average Wait Time (Mins)");
                    break;
                default:
                    continue;
            }

            for (int i = 0; i < directions.length; i++) {
                seriesMap.get(directions[i]).getData().add(new XYChart.Data<>(run.getName(), values[i]));
            }
        }

        // Add all series to the chart
        barChart.getData().addAll(seriesMap.values());
            
    }
}
