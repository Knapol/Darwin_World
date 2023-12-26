package agh.ics.oop.presenter;

import agh.ics.oop.OptionsParser;
import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;

import agh.ics.oop.model.map.WorldMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.List;

public class SimulationPresenter implements MapChangeListener {
    private WorldMap worldMap;

//    @FXML
//    private Label infoLabel;
    @FXML
    private TextField movesField;
    @FXML
    private Label moveInfo;
    @FXML
    private GridPane mapGrid;

    private final static int CELL_WIDTH = 30;
    private final static int CELL_HEIGHT = 30;

    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message){
        Platform.runLater(() -> {
            drawMap();
            moveInfo.setText(message);
        });
    }

    private void drawMap(){
//        infoLabel.setText(worldMap.toString());
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
            mapGrid.add(label, currentCol, 0);
            GridPane.setHalignment(label, HPos.CENTER);
            currentCol++;
        }

        int currentRow = 1;
        for (int i=boundary.upperRight().getY(); i>=boundary.lowerLeft().getY(); i--) {
            Label label = new Label(String.valueOf(i));
            mapGrid.add(label, 0, currentRow);
            GridPane.setHalignment(label, HPos.CENTER);
            currentRow++;
        }
    }

    private void drawWorldElements(Boundary boundary) {
        int width = boundary.upperRight().getX() - boundary.lowerLeft().getX() + 1;
        int height = boundary.upperRight().getY() - boundary.lowerLeft().getY() + 1;

        for (int x = 1; x <= width; x++) {
            for (int y = height; y >= 1; y--) {
                Vector2d pos = new Vector2d(x - 1 + boundary.lowerLeft().getX(), y - 1 + boundary.lowerLeft().getY());
                if (worldMap.isOccupied(pos)) {
                    Label label = new Label(worldMap.objectAt(pos).toString());
                    mapGrid.add(label, x, height - y + 1);
                    GridPane.setHalignment(label, HPos.CENTER);
                }
            }
        }
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    public void onSimulationStartClicked(){
        String[] args = movesField.getText().split(" ");
        List<Vector2d> positions = List.of(
                new Vector2d(1, 1),
                new Vector2d(1, 1),
                new Vector2d(1,1),
                new Vector2d(1, 1),
                new Vector2d(1, 1),
                new Vector2d(1,1),
                new Vector2d(1, 1),
                new Vector2d(1, 1),
                new Vector2d(1,1)
        );

        Simulation simulation = new Simulation(positions, worldMap, 6);
        SimulationEngine simulationEngine = new SimulationEngine(List.of(simulation));

        simulationEngine.runAsync();
    }
}
