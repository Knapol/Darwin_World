package agh.ics.oop.presenter;

import agh.ics.oop.model.Settings;
import agh.ics.oop.model.map.AbstractWorldMap;
import agh.ics.oop.model.map.ForestedEquators;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class InputPresenter {

    private final List<TextField> textFields = new ArrayList<>();

    @FXML
    private TextField mapWidth;

    @FXML
    private TextField mapHeight;

    @FXML
    private TextField genomeSize;

    @FXML
    private TextField startingEnergy;

    @FXML
    private TextField moveCost;

    @FXML
    private TextField minEnergyToBreed;

    @FXML
    private TextField grassCount;

    @FXML
    private TextField grassEnergy;

    @FXML
    private void onStart() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane viewRoot = loader.load();

        Stage simulationStage = new Stage();
        simulationStage.setTitle("simulation");

        SimulationPresenter presenter = loader.getController();
        AbstractWorldMap map = new ForestedEquators(createSettings());
//        AbstractWorldMap map = new ForestedEquators(20,15, 6, 20, 10, 0, 50, 20);
        map.addObserver(presenter);
        presenter.setWorldMap(map);

        simulationStage.setScene(new Scene(viewRoot));
        simulationStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        simulationStage.minHeightProperty().bind(viewRoot.minHeightProperty());

        simulationStage.show();

        presenter.createSimulation();
    }

    @FXML
    private void initialize(){
        textFields.add(mapWidth);
        textFields.add(mapHeight);
        textFields.add(genomeSize);
        textFields.add(startingEnergy);
        textFields.add(moveCost);
        textFields.add(minEnergyToBreed);
        textFields.add(grassCount);
        textFields.add(grassEnergy);
    }

    private Settings createSettings(){
        return new Settings(
                parseInt(mapWidth.getText()),
                parseInt(mapHeight.getText()),
                parseInt(genomeSize.getText()),
                parseInt(startingEnergy.getText()),
                parseInt(moveCost.getText()),
                parseInt(minEnergyToBreed.getText()),
                parseInt(grassCount.getText()),
                parseInt(grassEnergy.getText())
        );
    }
}
