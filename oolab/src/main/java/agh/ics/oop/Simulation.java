package agh.ics.oop;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MoveDirection;
import agh.ics.oop.model.SimulationState;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;

import java.util.*;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    private SimulationState simulationState = SimulationState.RUNNING;

    public Simulation(WorldMap map){
        this.map = map;
        createAndPlaceAnimals();
    }

    private void createAndPlaceAnimals(){
        Random random = new Random();
        for (int i=0; i<map.getStartingNumberOfAnimals(); i++){
            int x = random.nextInt(map.getWidth());
            int y = random.nextInt(map.getHeight());

            Vector2d position = new Vector2d(x, y);
            Animal newAnimal = new Animal(position, map);

            map.place(newAnimal);
            this.animals.add(newAnimal);
        }
    }
    public void run() {
        while (true){
            try {
                switch(simulationState){
                    case RUNNING -> newSimulationDay();
                    case PAUSED -> Thread.sleep(100);
                    case ENDED -> {return;}
                }
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void newSimulationDay() throws InterruptedException{
        Iterator<Animal> it = animals.iterator();
        while (it.hasNext()){
            Animal animal = it.next();
            if (animal.getEnergy() <= 0){
                it.remove();
                map.handleAnimalDeath(animal);
                continue;
            }
            map.move(animal);
        }

        if (animals.isEmpty()){
            System.out.println("All dead sadge");
            simulationState = SimulationState.ENDED;
            return;
        }

        map.breedAnimals(animals);

        map.eatGrass();
        map.createNewGrass();
        map.mapChanged("New frame");
        Thread.sleep(500);
    }

    public void pause(){
        simulationState = SimulationState.PAUSED;
    }

    public void start(){
        simulationState = SimulationState.RUNNING;
    }

    public void end(){
        simulationState = SimulationState.ENDED;
    }

    public SimulationState getSimulationState(){
        return simulationState;
    }
}
