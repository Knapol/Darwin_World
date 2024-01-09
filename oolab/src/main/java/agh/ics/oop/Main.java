package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.map.AbstractWorldMap;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args){
        System.out.println("Start");

        Application.launch(SimulationApp.class, args);

        System.out.println("Stop");
    }
}
