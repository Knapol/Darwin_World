package agh.ics.oop;

import agh.ics.oop.model.map.AbstractWorldMap;
import agh.ics.oop.model.map.ForestedEquators;
import agh.ics.oop.presenter.InputPresenter;
import javafx.application.Application;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import agh.ics.oop.presenter.SimulationPresenter;
import javafx.scene.Scene;

import java.io.IOException;

public class SimulationApp extends Application {
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("input.fxml"));
        BorderPane viewRoot = loader.load();

        configureStage(primaryStage, viewRoot);

        primaryStage.show();
    }

    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation app");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }
}
