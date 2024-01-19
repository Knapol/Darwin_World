package agh.ics.oop.presenter;

import agh.ics.oop.OptionsParser;
import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;

import agh.ics.oop.model.map.WorldMap;
import com.sun.util.reentrant.ReentrantContextProviderCLQ;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

public class SimulationPresenter implements MapChangeListener {
    private WorldMap worldMap;
    private Simulation simulation;

    @FXML
    private GridPane mapGrid;
    private int CELL_WIDTH;
    private int CELL_HEIGHT;
    private Image animalFullHpImage;
    private Image animalHalfHpImage;
    private Image animalLowHpImage;
    private Image grassImage;

    //Statistics
    @FXML
    private Label statDay;
    @FXML
    private Label statAnimalsNumber;
    @FXML
    private Label statGrassNumber;
    @FXML
    private Label statFreeFieldNumber;
    @FXML
    private Label statMostPopularGenome;
    @FXML
    private Label statAverageEnergy;
    @FXML
    private Label statAverageLifespan;
    @FXML
    private Label statAverageNumberOfChildren;

    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;

        int side = 500/(Math.max(worldMap.getHeight(), worldMap.getWidth())+1);

        CELL_HEIGHT = side;
        CELL_WIDTH = side;
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message){
        Platform.runLater(() -> {
            drawMap();
            updateStatistic();
        });
    }

    @FXML
    private void initialize(){
        try {
            animalFullHpImage = new Image(new FileInputStream("src/main/resources/images/animalFullHp.png"));
            animalHalfHpImage = new Image(new FileInputStream("src/main/resources/images/animalHalfHp.png"));
            animalLowHpImage = new Image(new FileInputStream("src/main/resources/images/animalLowHp.png"));
            grassImage = new Image(new FileInputStream("src/main/resources/images/grass.png"));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private void drawMap(){
        clearGrid();
        Boundary currentBound = worldMap.getCurrentBounds();

        initializeGrid(currentBound);
        drawCoordinates(currentBound);
        drawWorldElements(currentBound);
    }

    private void initializeGrid(Boundary boundary){
        for (int i=boundary.lowerLeft().getX(); i<=boundary.upperRight().getX()+1; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        }

        for (int i=boundary.lowerLeft().getY(); i<=boundary.upperRight().getY()+1; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));
        }
    }

    private void drawCoordinates(Boundary boundary){
        Label corner = new Label("y\\x");
        mapGrid.add(corner, 0, 0);
        GridPane.setHalignment(corner, HPos.CENTER);

        int currentCol = 1;
        for (int i=boundary.lowerLeft().getX(); i<=boundary.upperRight().getX(); i++) {
            Label label = new Label(String.valueOf(i));
            label.prefHeight(CELL_WIDTH);
            label.prefWidth(CELL_HEIGHT);
            mapGrid.add(label, currentCol, 0);
            GridPane.setHalignment(label, HPos.CENTER);
            currentCol++;
        }

        int currentRow = 1;
        for (int i=boundary.upperRight().getY(); i>=boundary.lowerLeft().getY(); i--) {
            Label label = new Label(String.valueOf(i));
            label.prefHeight(CELL_WIDTH);
            label.prefWidth(CELL_HEIGHT);
            mapGrid.add(label, 0, currentRow);
            GridPane.setHalignment(label, HPos.CENTER);
            currentRow++;
        }
    }

    private void drawWorldElements(Boundary boundary) {
        int width = worldMap.getWidth();
        int height = worldMap.getHeight();

        for (int x = 1; x <= width; x++) {
            for (int y = height; y >= 1; y--) {
                Vector2d pos = new Vector2d(x - 1 + boundary.lowerLeft().getX(), y - 1 + boundary.lowerLeft().getY());

                Rectangle rectangle;
                if (worldMap.isBetterField(pos)){
                    rectangle = new Rectangle(CELL_WIDTH, CELL_HEIGHT, Color.rgb(222,184,135));
                }
                else{
                    rectangle = new Rectangle(CELL_WIDTH, CELL_HEIGHT, Color.rgb(255,235,205));
                }

                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(1);

                mapGrid.add(rectangle, x, height - y + 1);
                GridPane.setHalignment(rectangle, HPos.CENTER);

                if (worldMap.isOccupied(pos)) {
                    ImageView imageView;
                    if (worldMap.objectAt(pos) instanceof Grass) {
                        imageView = new ImageView(grassImage);
                    }
                    else{
                        Animal animal = (Animal) worldMap.objectAt(pos);
                        imageView = new ImageView(getAnimalImage(animal));
                    }

                    imageView.setFitHeight(CELL_HEIGHT);
                    imageView.setFitWidth(CELL_WIDTH);

                    mapGrid.add(imageView, x, height - y + 1);
                    GridPane.setHalignment(imageView, HPos.CENTER);
                }
            }
        }
    }

    private Image getAnimalImage(Animal animal){
        if (animal.getEnergy() >= simulation.getStartingEnergy()){
            return animalFullHpImage;
        }
        else if (animal.getEnergy() >= simulation.getStartingEnergy() / 2 && animal.getEnergy() < simulation.getStartingEnergy()){
            return animalHalfHpImage;
        }
        return animalLowHpImage;
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void createSimulation(Stage simulationStage){
        simulation = new Simulation(worldMap);
        Thread thread = new Thread(simulation);
        thread.start();
        simulationStage.setOnCloseRequest(event -> simulation.end());
    }

    @FXML
    private void onPause(){
        if (simulation.getSimulationState() == SimulationState.RUNNING) {
            simulation.pause();
        }

        else{
            simulation.start();
        }
    }

    private void updateStatistic(){
        statDay.setText(String.valueOf(simulation.getDay()));
        statAnimalsNumber.setText(String.valueOf(simulation.getAnimalCount()));
        statGrassNumber.setText(String.valueOf(simulation.getGrassCount()));
        statFreeFieldNumber.setText(String.valueOf(simulation.getEmptyFieldsCount()));
        statMostPopularGenome.setText(simulation.getMostPopularGenomes());
        statAverageEnergy.setText(String.format("%.2f", simulation.getAverageEnergy()));
        statAverageLifespan.setText(String.format("%.2f", simulation.getAverageDaysLived()));
        statAverageNumberOfChildren.setText(String.format("%.2f", simulation.getAverageChildCount()));
    }
}
