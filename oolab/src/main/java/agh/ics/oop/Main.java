package agh.ics.oop;

import agh.ics.oop.model.ConsoleMapDisplay;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.map.AbstractWorldMap;
import agh.ics.oop.model.map.RectangularMap;
import javafx.application.Application;

import java.util.List;

public class Main {
    public static void main(String[] args){
        System.out.println("Start");

//        AbstractWorldMap map = new RectangularMap(5,5);
//        ConsoleMapDisplay consoleMapDisplay = new ConsoleMapDisplay();
//        map.addObserver(consoleMapDisplay);
//        List<Vector2d> positions = List.of(new Vector2d(0, 0));
//
//        Simulation simulation = new Simulation(positions, map);
//        SimulationEngine simulationEngine = new SimulationEngine(List.of(simulation));
//
//        simulationEngine.runAsyncInThreadPool();
//        try {
//            simulationEngine.awaitSimulationsEnd();
//        }catch(InterruptedException e){
//            e.printStackTrace();
//        }
        Application.launch(SimulationApp.class, args);

        System.out.println("Stop");
    }
}
