package agh.ics.oop;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MoveDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    public Simulation(List<Vector2d> startPositions, WorldMap map){
        this.map = map;
        createAndPlaceAnimals(startPositions);
    }

    private void createAndPlaceAnimals(List<Vector2d> startPositions){
        for (Vector2d pos : startPositions){
            Animal newAnimal = new Animal(pos);
            try {
                map.place(newAnimal);
                this.animals.add(newAnimal);
            }
            catch (PositionAlreadyOccupiedException e){
                System.out.println("Animal was not placed on the map");
            }
        }
    }
    public void run() {
        try {
            for (int i = 0; i < 10; i++){ // for test purpose 10 is ten days
                map.update();
                Thread.sleep(500);
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public List<Animal> getAnimals(){
        return Collections.unmodifiableList(animals);
    }
}
