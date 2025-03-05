package com.example;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public class Graph {

    public static VBox createGraph(SimulationData[] runs) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Simulation Run");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Placeholder");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Simulation Score Comparisons");
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Performance Score", "Average Wait Time", "Max Wait Time", "Average Queue Length");
        comboBox.setValue("Performance Score");

        updateChart(barChart, runs, "score");

        comboBox.setOnAction(event -> {
            String selectedMetric = comboBox.getValue();
            switch (selectedMetric) {
                case "Max Wait Time":
                    updateChart(barChart, runs, "maxWaitTime");
                    break;
                case "Average Queue Length":
                    updateChart(barChart, runs, "avgQueueLength");
                    break;
                case "Average Wait Time":
                    updateChart(barChart, runs, "avgWaitTime");
                    break;
                default:
                    updateChart(barChart, runs, "score");
                    break;
            }
        });

        VBox vbox = new VBox(comboBox, barChart);
        VBox.setVgrow(barChart, javafx.scene.layout.Priority.ALWAYS); 
        return vbox;
    }

    private static void updateChart(BarChart<String, Number> barChart, SimulationData[] runs, String metric) {
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
                    barChart.getYAxis().setLabel("Average Queue Length (Cars)");
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
