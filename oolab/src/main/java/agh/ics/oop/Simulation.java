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
    // Do listy zwierząt używam arrayList, ponieważ operacja dodania i usunięcia zwierzęcia z listy
    // występuje na ten moment zdecydowanie rzadziej niż dostęp do zwierzęcia operatorem get.
    // A operacja get jest szybsza przy użyciu arrayList
    // W przypadku listy ruchów, sytuacja jest nieco inna, ponieważ każdy ruch dodajemy do listy raz
    // oraz każdy ruch pobieramy z listy również raz, dlatego ani linked Lista, ani arrayList nie
    // zyskuje
    private final List<MoveDirection> moves;
    private final List<Animal> animals = new ArrayList<>();
    private final WorldMap map;
    public Simulation(List<Vector2d> startPositions, List<MoveDirection> moves, WorldMap map){
        this.moves = moves;
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
            for (int i = 0; i < moves.size(); i++){
                Animal currentAnimal = animals.get(i % animals.size());
                map.move(currentAnimal, moves.get(i));
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
