package agh.ics.oop;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MoveDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;

import java.util.ArrayList;
import java.util.Iterator;
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
            Animal newAnimal = new Animal(pos, map);
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
            for (int i = 0; i < 100; i++){ // for test purpose 100 is ten days

                Iterator<Animal> it = animals.iterator();
                while (it.hasNext()){
                    Animal animal = it.next();
                    if (animal.getEnergy() <= 0){
                        it.remove();
                    }
                    map.move(animal);
                    Thread.sleep(1000);
                }

                if (animals.size() == 0){
                    System.out.println("All dead sadge");
                    return;
                }
//
//                for (Animal animal : animals){
//                    if (animal.getEnergy() <= 0){
//                        animals.remove(animal);
//                    }
//                    map.move(animal);
//                    Thread.sleep(5000);
//                }
                map.breedAnimals(animals);
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void moveAllAnimals(){
        for (Animal animal : animals){
            map.move(animal);
        }
    }

    public List<Animal> getAnimals(){
        return Collections.unmodifiableList(animals);
    }
}
