package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;

import agh.ics.oop.model.map.WorldMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SimulationPresenter implements MapChangeListener {
    private WorldMap worldMap;
    private Simulation simulation;

    private final Background betterField = new Background(new BackgroundFill(Color.rgb(222,184,135), CornerRadii.EMPTY, Insets.EMPTY));
    private final Background normalField = new Background(new BackgroundFill(Color.rgb(255,235,205), CornerRadii.EMPTY, Insets.EMPTY));
    private final Border trackedAnimalBorder = new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(2)));
    private final Border defaultBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(0.4)));
    private final Border dominantGenomeBorder = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(2)));

    @FXML
    private GridPane mapGrid;
    private int CELL_WIDTH;
    private int CELL_HEIGHT;
    private Image animalFullHpImage;
    private Image animalHalfHpImage;
    private Image animalLowHpImage;
    private Image grassImage;

    //Global statistics
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
    private List<Label> globalStatsLabels;

    //Tracked animal statistics
    @FXML
    private Label statGenome;
    @FXML
    private Label statActiveGene;
    @FXML
    private Label statEnergy;
    @FXML
    private Label statEatenGrass;
    @FXML
    private Label statNumberOfChildren;
    @FXML
    private Label statNumberOfDescendants;
    @FXML
    private Label statDaysAlive;
    @FXML
    private Label statDeathDay;

    private List<Label> animalStatsLabels;

    @FXML
    private Button disableTrackingButton;
    private Animal trackedAnimal;
    private Set<Vector2d> dominantGenomeAnimalsPositions;

    private String statsFileName;

    private Label[][] labelArray;

    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;

        int side = 500/(Math.max(worldMap.getHeight(), worldMap.getWidth())+1);
        CELL_HEIGHT = side;
        CELL_WIDTH = side;

        labelArray = new Label[worldMap.getWidth()+1][worldMap.getHeight()+1];
        initializeGrid(worldMap.getCurrentBounds());
        initializeMapLabels();
        drawCoordinates(worldMap.getCurrentBounds());
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message){
        Platform.runLater(() -> {
            drawMap();
            updateGlobalStatistic();
            updateAnimalStatistic();
            if (!this.statsFileName.isEmpty()){
                saveStatsToFile();
            }
        });
    }

    @FXML
    private void initialize(){
        try {
            animalFullHpImage = new Image(new FileInputStream("src/main/resources/images/animalFullHp.png"));
            animalHalfHpImage = new Image(new FileInputStream("src/main/resources/images/animalHalfHp.png"));
            animalLowHpImage = new Image(new FileInputStream("src/main/resources/images/animalLowHp.png"));
            grassImage = new Image(new FileInputStream("src/main/resources/images/grass.png"));
            disableTrackingButton.setDisable(true);
            initializeStatLabels();
            this.dominantGenomeAnimalsPositions = new HashSet<>();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private void initializeStatLabels(){
        animalStatsLabels = List.of(
                statGenome,
                statActiveGene,
                statEnergy,
                statEatenGrass,
                statNumberOfChildren,
                statNumberOfDescendants,
                statDaysAlive,
                statDeathDay
        );

        globalStatsLabels = List.of(
                statDay,
                statAnimalsNumber,
                statGrassNumber,
                statFreeFieldNumber,
                statMostPopularGenome,
                statAverageEnergy,
                statAverageLifespan,
                statAverageNumberOfChildren
        );
    }

    private void drawMap(){
        clearGrid();
        Boundary currentBound = worldMap.getCurrentBounds();
        initializeGrid(worldMap.getCurrentBounds());
        initializeMapLabels();
        drawCoordinates(worldMap.getCurrentBounds());
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
            labelArray[currentCol][0] = label;
            GridPane.setHalignment(label, HPos.CENTER);
            currentCol++;
        }

        int currentRow = 1;
        for (int i=boundary.upperRight().getY(); i>=boundary.lowerLeft().getY(); i--) {
            Label label = new Label(String.valueOf(i));
            label.prefHeight(CELL_WIDTH);
            label.prefWidth(CELL_HEIGHT);
            mapGrid.add(label, 0, currentRow);
            labelArray[0][currentRow] = label;
            GridPane.setHalignment(label, HPos.CENTER);
            currentRow++;
        }
    }

    private void initializeMapLabels(){
        int width = worldMap.getWidth();
        int height = worldMap.getHeight();

        for (int x = 1; x <= width; x++) {
            for (int y = height; y >= 1; y--) {
                Label label = new Label();
                label.setMinHeight(CELL_WIDTH);
                label.setMinWidth(CELL_HEIGHT);
                labelArray[x][height - y + 1] = label;
                setAnimalClickHandler(label);
                mapGrid.add(label, x, height - y + 1);
                GridPane.setHalignment(label, HPos.CENTER);
            }
        }
    }

    private void drawWorldElements(Boundary boundary){
        int width = worldMap.getWidth();
        int height = worldMap.getHeight();
        for (int x = 1; x <= width; x++) {
            for (int y = 1; y <= height; y++) {
                Vector2d pos = new Vector2d(x - 1, y - 1);
                Label label = labelArray[x][y];
                label.setBorder(defaultBorder);

                if (worldMap.isBetterField(pos)){
                    label.setBackground(betterField);
                }
                else{
                    label.setBackground(normalField);
                }

                if (worldMap.isOccupied(pos)) {
                    ImageView imageView;
                    if (worldMap.objectAt(pos) instanceof Grass) {
                        imageView = new ImageView(grassImage);
                    }
                    else{
                        Animal animal = (Animal) worldMap.objectAt(pos);
                        imageView = new ImageView(getAnimalImage(animal));
                    }

                    if (this.trackedAnimal != null && this.trackedAnimal.getPosition().equals(pos)){
                        label.setBorder(trackedAnimalBorder);
                        imageView.setFitHeight(CELL_HEIGHT-4);
                        imageView.setFitWidth(CELL_WIDTH-4);
                    }
                    else {
                        imageView.setFitHeight(CELL_HEIGHT);
                        imageView.setFitWidth(CELL_WIDTH);
                    }
                    label.setGraphic(imageView);
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

    public void createSimulation(Stage simulationStage, String statsFileName){
        this.statsFileName = statsFileName;
        if (!this.statsFileName.isEmpty()){
            createStatsFile();
        }
        simulation = new Simulation(worldMap);
        Thread thread = new Thread(simulation);
        thread.start();
        simulationStage.setOnCloseRequest(event -> simulation.end());
    }

    @FXML
    private void onPause(){
        if (simulation.getSimulationState() == SimulationState.RUNNING) {
            simulation.pause();
            this.dominantGenomeAnimalsPositions.addAll(simulation.dominantGenomeAnimals());
            for (Vector2d pos : dominantGenomeAnimalsPositions){
                if (this.trackedAnimal != null && this.trackedAnimal.getPosition().equals(pos)) {
                    labelArray[pos.getX() + 1][pos.getY() + 1].setBorder(trackedAnimalBorder);
                }
                else{
                    labelArray[pos.getX() + 1][pos.getY() + 1].setBorder(dominantGenomeBorder);
                }
                ImageView imageView = new ImageView(getAnimalImage((Animal) worldMap.objectAt(pos)));
                imageView.setFitHeight(CELL_HEIGHT-4);
                imageView.setFitWidth(CELL_WIDTH-4);
                labelArray[pos.getX()+1][pos.getY()+1].setGraphic(imageView);
            }
        }
        else{
            this.dominantGenomeAnimalsPositions.clear();
            simulation.start();
        }
    }

    private void setAnimalClickHandler(Label label){
        label.setOnMouseClicked(event -> {
            if (simulation.getSimulationState() == SimulationState.PAUSED){
                Vector2d position = new Vector2d(GridPane.getColumnIndex(label)-1, GridPane.getRowIndex(label) -1);
                if (worldMap.isOccupied(position)){
                    if (worldMap.objectAt(position) instanceof Animal){
                        if (trackedAnimal != null){
                            if (!dominantGenomeAnimalsPositions.contains(trackedAnimal.getPosition())){
                                ImageView imageView = new ImageView(getAnimalImage(trackedAnimal));
                                imageView.setFitHeight(CELL_HEIGHT);
                                imageView.setFitWidth(CELL_WIDTH);
                                labelArray[trackedAnimal.getPosition().getX()+1][trackedAnimal.getPosition().getY()+1].setBorder(defaultBorder);
                                labelArray[trackedAnimal.getPosition().getX()+1][trackedAnimal.getPosition().getY()+1].setGraphic(imageView);
                            }
                            else {
                                labelArray[trackedAnimal.getPosition().getX()+1][trackedAnimal.getPosition().getY()+1].setBorder(dominantGenomeBorder);
                            }
                        }
                        trackedAnimal = (Animal) worldMap.objectAt(position);
                        disableTrackingButton.setDisable(false);
                        updateAnimalStatistic();
                        ImageView imageView = new ImageView(getAnimalImage(trackedAnimal));
                        label.setBorder(trackedAnimalBorder);
                        imageView.setFitHeight(CELL_HEIGHT-4);
                        imageView.setFitWidth(CELL_WIDTH-4);
                        label.setGraphic(imageView);
                    }
                }
            }
        });
    }

    private void updateGlobalStatistic(){
        statDay.setText(String.valueOf(simulation.getDay()));
        statAnimalsNumber.setText(String.valueOf(simulation.getAnimalCount()));
        statGrassNumber.setText(String.valueOf(simulation.getGrassCount()));
        statFreeFieldNumber.setText(String.valueOf(simulation.getEmptyFieldsCount()));
        statMostPopularGenome.setText(simulation.getMostPopularGenomes());
        statAverageEnergy.setText(String.format("%.2f", simulation.getAverageEnergy()));
        statAverageLifespan.setText(String.format("%.2f", simulation.getAverageDaysLived()));
        statAverageNumberOfChildren.setText(String.format("%.2f", simulation.getAverageChildCount()));
    }

    private void updateAnimalStatistic(){
        if (trackedAnimal != null){
            statGenome.setText(trackedAnimal.getGenome().toString());
            statActiveGene.setText(String.valueOf(trackedAnimal.getGenome().getActiveGene()));
            statEnergy.setText(String.valueOf(trackedAnimal.getEnergy()));
            statEatenGrass.setText(String.valueOf(trackedAnimal.getEatenGrass()));
            statNumberOfChildren.setText(String.valueOf(trackedAnimal.getNumberOfChildren()));
            statNumberOfDescendants.setText(String.valueOf(trackedAnimal.getNumberOfDescendants()));
            statDaysAlive.setText(String.valueOf(trackedAnimal.getDaysAlive()));
            statDeathDay.setText(
                    trackedAnimal.getDeathDay() != 0 ? String.valueOf(trackedAnimal.getDeathDay()) : "..."
            );
        }
    }

    @FXML
    private void disableAnimalTracking(){
        if (!dominantGenomeAnimalsPositions.contains(trackedAnimal.getPosition())){
            ImageView imageView = new ImageView(getAnimalImage(trackedAnimal));
            imageView.setFitHeight(CELL_HEIGHT);
            imageView.setFitWidth(CELL_WIDTH);
            labelArray[trackedAnimal.getPosition().getX()+1][trackedAnimal.getPosition().getY()+1].setBorder(defaultBorder);
            labelArray[trackedAnimal.getPosition().getX()+1][trackedAnimal.getPosition().getY()+1].setGraphic(imageView);
        }
        else {
            labelArray[trackedAnimal.getPosition().getX()+1][trackedAnimal.getPosition().getY()+1].setBorder(dominantGenomeBorder);
        }

        trackedAnimal = null;
        clearAnimalStatsLabels();
        disableTrackingButton.setDisable(true);
    }

    private void clearAnimalStatsLabels(){
        for (Label label : animalStatsLabels){
            label.setText("");
        }
    }

    private void createStatsFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/savedStatistics/"+this.statsFileName, true))){
            for(Label label : globalStatsLabels){
                writer.write(label.getId());
                writer.write(";");
            }
            writer.newLine();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void saveStatsToFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/savedStatistics/"+this.statsFileName, true))){
            for(Label label : globalStatsLabels){
                writer.write(label.getText());
                writer.write(";");
            }
            writer.newLine();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
