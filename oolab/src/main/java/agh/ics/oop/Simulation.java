package agh.ics.oop;
import agh.ics.oop.model.*;
import agh.ics.oop.model.map.WorldMap;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;

import java.util.*;
import java.util.stream.Collectors;

public class Simulation implements Runnable {
    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    private SimulationState simulationState = SimulationState.RUNNING;
    private int deadAnimalsCount;
    private int sumOfLivedDays;
    private final int startingEnergy;
    private String dominantGenome;
    private int day;

    public Simulation(WorldMap map){
        this.map = map;
        this.startingEnergy = map.getStartingEnergy();
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
                deadAnimalsCount++;
                sumOfLivedDays += animal.getDaysAlive();
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
        day++;
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

    //statistics
    public int getDay(){
        return day;
    }

    public int getAnimalCount(){
        return map.getAnimalCount();
    }

    public int getGrassCount(){
        return map.getGrassCount();
    }

    public int getEmptyFieldsCount(){
        return map.getEmptyFieldsCount();
    }

    public String getMostPopularGenomes(){
        Map<String, Integer> genomeMap = animals.stream()
                .collect(Collectors.groupingBy(animal -> animal.getGenome().toString(), Collectors.summingInt(x -> 1)));

        String dominantGenome = "";
        int dominantCount = 0;
        for (Map.Entry<String, Integer> entry : genomeMap.entrySet()){
            String genome = entry.getKey();
            int count = entry.getValue();
            if (dominantCount < count){
                dominantCount = count;
                dominantGenome = genome;
            }
        }
        //I do this attribute, so I don't have to repeat all of those operations, when user pause the game
        this.dominantGenome = dominantGenome;
        return dominantGenome + " " + dominantCount;
    }

    public List<Animal> dominantGenomeAnimals(){
        return animals.stream()
                .filter(animal -> animal.getGenome().toString().equals(this.dominantGenome))
                .collect(Collectors.toList());
    }

    public double getAverageEnergy(){
        int energySum = 0;
        for(Animal animal : animals){
            energySum += animal.getEnergy();
        }
        return (double)energySum / animals.size();
    }

    public double getAverageDaysLived(){
        return (double) sumOfLivedDays / deadAnimalsCount;
    }

    public double getAverageChildCount(){
        int childSum = 0;
        for (Animal animal : animals){
            childSum += animal.getNumberOfChildren();
        }
        return (double) childSum / animals.size();
    }

    public int getStartingEnergy(){
        return this.startingEnergy;
    }
}
