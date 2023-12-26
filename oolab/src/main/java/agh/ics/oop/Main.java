package agh.ics.oop;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.ConsoleMapDisplay;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.WorldElement;
import agh.ics.oop.model.map.AbstractWorldMap;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args){
        System.out.println("Start");

        Application.launch(SimulationApp.class, args);

        System.out.println("Stop");
    }
}
