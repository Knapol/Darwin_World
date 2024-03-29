package agh.ics.oop.presenter;

import agh.ics.oop.model.worldElements.AnimalBehavior;
import agh.ics.oop.model.Settings;
import agh.ics.oop.model.map.AbstractWorldMap;
import agh.ics.oop.model.map.ForestedEquators;
import agh.ics.oop.model.map.LifeGivingCorpses;
import agh.ics.oop.model.map.MapType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class InputPresenter {

    private final List<TextField> textFields = new ArrayList<>();

    @FXML
    private TextField mapWidth;

    @FXML
    private TextField mapHeight;

    @FXML
    private TextField numberOfAnimals;

    @FXML
    private TextField genomeSize;

    @FXML
    private TextField minMutations;

    @FXML
    private TextField maxMutations;

    @FXML
    private ComboBox<AnimalBehavior> animalBehaviorComboBox;

    @FXML
    private TextField startingEnergy;

    @FXML
    private TextField moveCost;

    @FXML
    private TextField minEnergyToBreed;

    @FXML
    private TextField energyUseForBreeding;

    @FXML
    private TextField grassCount;

    @FXML
    private TextField grassEnergy;

    @FXML
    private TextField grassPerDay;

    @FXML
    private ComboBox<MapType> mapTypeComboBox;

    @FXML
    private TextField betterFieldDuration;

    @FXML
    private TextField currentSettingsName;

    @FXML
    private ComboBox<String> savedSettings;

    @FXML
    private CheckBox saveStatsCheckBox;

    @FXML TextField statsName;

    @FXML
    private void onStart() throws IOException {
        if (validateSettings()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
            BorderPane viewRoot = loader.load();

            Stage simulationStage = new Stage();
            simulationStage.setTitle("Simulation");

            SimulationPresenter presenter = loader.getController();
            AbstractWorldMap map = switch(mapTypeComboBox.getValue()) {
                case FORESTED_EQUATORS -> new ForestedEquators(createSettings());
                case LIFE_GIVING_CORPSES -> new LifeGivingCorpses(createSettings());
            };

            map.addObserver(presenter);
            presenter.setWorldMap(map);

            simulationStage.setScene(new Scene(viewRoot));
            simulationStage.minWidthProperty().bind(viewRoot.minWidthProperty());
            simulationStage.minHeightProperty().bind(viewRoot.minHeightProperty());

            simulationStage.show();

            presenter.createSimulation(simulationStage, statsName.getText());
        }
    }

    @FXML
    private void initialize(){
        textFields.add(mapWidth);
        textFields.add(mapHeight);
        textFields.add(numberOfAnimals);
        textFields.add(genomeSize);
        textFields.add(minMutations);
        textFields.add(maxMutations);
        textFields.add(startingEnergy);
        textFields.add(moveCost);
        textFields.add(minEnergyToBreed);
        textFields.add(energyUseForBreeding);
        textFields.add(grassCount);
        textFields.add(grassEnergy);
        textFields.add(grassPerDay);
        textFields.add(betterFieldDuration);

        initializeSettingsComboBox();
        initializeAnimalBehaviorComboBox();
        initializeMapTypeComboBox();
        initializeTextFields();

        addInputValidators();
    }

    private void initializeMapTypeComboBox(){
        ObservableList<MapType> mapTypes = FXCollections.observableArrayList(MapType.values());
        mapTypeComboBox.setItems(mapTypes);
    }

    private void initializeAnimalBehaviorComboBox(){
        ObservableList<AnimalBehavior> animalBehaviors = FXCollections.observableArrayList(AnimalBehavior.values());
        animalBehaviorComboBox.setItems(animalBehaviors);
    }

    private void initializeSettingsComboBox(){
        File directory = new File("src/main/resources/savedSettings");

        if (directory.isDirectory()){
            File[] files = directory.listFiles();
            if (files != null){
                Arrays.stream(files)
                        .filter(File::isFile)
                        .forEach(file -> savedSettings.getItems().add(file.getName()));
            }
        }
    }

    private void initializeTextFields(){
        for (TextField textField : textFields){
            textField.setText("0");
        }
        betterFieldDuration.setDisable(true);
        statsName.setDisable(true);
    }

    @FXML
    private void onMapTypeChange(){
        if (mapTypeComboBox.getValue() == MapType.FORESTED_EQUATORS) {
            betterFieldDuration.setDisable(true);
            betterFieldDuration.setText("0");
        }
        else{
            betterFieldDuration.setDisable(false);
        }
    }

    @FXML
    private void onSaveStatisticChange(){
        if (saveStatsCheckBox.isSelected()) {
            statsName.setDisable(false);
        }
        else{
            statsName.setDisable(true);
            statsName.setText("");
        }
    }

    private Settings createSettings(){
        return new Settings(
                parseInt(mapWidth.getText()),
                parseInt(mapHeight.getText()),
                parseInt(numberOfAnimals.getText()),
                parseInt(genomeSize.getText()),
                parseInt(minMutations.getText()),
                parseInt(maxMutations.getText()),
                animalBehaviorComboBox.getValue(),
                parseInt(startingEnergy.getText()),
                parseInt(moveCost.getText()),
                parseInt(minEnergyToBreed.getText()),
                parseInt(energyUseForBreeding.getText()),
                parseInt(grassCount.getText()),
                parseInt(grassEnergy.getText()),
                parseInt(grassPerDay.getText()),
                mapTypeComboBox.getValue(),
                parseInt(betterFieldDuration.getText())
        );
    }

    @FXML
    private void saveSettingsToFile(){
        if (!currentSettingsName.getText().isEmpty() && validateSettings()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/savedSettings/" + currentSettingsName.getText(), true))) {
                for (TextField textField : textFields) {
                    writer.write(textField.getText());
                    writer.write(",");
                }
                writer.newLine();
                writer.write(mapTypeComboBox.getValue().name());
                writer.newLine();
                writer.write(animalBehaviorComboBox.getValue().name());
                writer.newLine();
                savedSettings.getItems().add(currentSettingsName.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void getSettingsFromFile(){
        if (!savedSettings.getSelectionModel().isEmpty()) {
            try (Scanner scanner = new Scanner(new File("src/main/resources/savedSettings/" + savedSettings.getValue()))) {
                int lineCounter = 0;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    switch (lineCounter) {
                        case 0 -> {
                            String[] dane = line.split(",");

                            int i = 0;
                            for (TextField textField : textFields) {
                                textField.setText(dane[i]);
                                i++;
                            }
                        }
                        case 1 -> mapTypeComboBox.setValue(MapType.valueOf(line));
                        case 2 -> animalBehaviorComboBox.setValue(AnimalBehavior.valueOf(line));
                    }
                    lineCounter++;
                }
                betterFieldDuration.setDisable(mapTypeComboBox.getValue() == MapType.FORESTED_EQUATORS);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateSettings(){
        if (mapTypeComboBox.getValue() == null){
            return false;
        }

        if (animalBehaviorComboBox.getValue() == null){
            return false;
        }

        for(TextField textField : textFields){
            if (textField.getText().isEmpty()){
                return false;
            }
        }

        if (parseInt(minMutations.getText()) >= parseInt(maxMutations.getText())){
            showWarningAlert("Minimal number of mutations must be lower than maximum number!");
            return false;
        }

        return true;
    }

    private void showWarningAlert(String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addInputValidators(){
        addSingleInputValidator(mapWidth, 1, 30);
        addSingleInputValidator(mapHeight, 1, 30);
        addSingleInputValidator(numberOfAnimals, 1, 100);
        addSingleInputValidator(genomeSize, 1, 20);
        addSingleInputValidator(minMutations, 0, 20);
        addSingleInputValidator(maxMutations, 0, 20);
        addSingleInputValidator(startingEnergy, 1, 1000);
        addSingleInputValidator(moveCost, 0, 1000);
        addSingleInputValidator(minEnergyToBreed, 0, 1000);
        addSingleInputValidator(energyUseForBreeding, 0, 1000);
        addSingleInputValidator(grassCount, 0, 900);
        addSingleInputValidator(grassEnergy, 0, 1000);
        addSingleInputValidator(grassPerDay, 0, 900);
        addSingleInputValidator(betterFieldDuration, 0, 1000);
    }

    private void addSingleInputValidator(TextField textField, int minValue, int maxValue){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.equals("")){
                    return;
                }
                textField.setText(String.valueOf(validateInput(parseInt(textField.getText()), minValue, maxValue)));
            }catch(NumberFormatException e){
                textField.setText(Integer.toString(minValue));
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) ->{
            if (!newValue) {
                if (textField.getText().equals("")){
                    textField.setText(Integer.toString(minValue));
                }

                textField.setText(String.valueOf(validateInput(parseInt(textField.getText()), minValue, maxValue)));
            }
        });
    }

    private int validateInput(int value, int minValue, int maxValue){
        if (value < minValue){
            return minValue;
        }
        if (value > maxValue){
            return maxValue;
        }
        return value;
    }
}
