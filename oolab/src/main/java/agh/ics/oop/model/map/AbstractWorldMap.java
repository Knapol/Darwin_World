package agh.ics.oop.model.map;

import agh.ics.oop.model.*;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;
// ziemia to po prostu abstract worldMap (BO TAK PROSCIEJ OKOKOK)
public abstract class AbstractWorldMap implements WorldMap {
    private static final Vector2d LOWER_LEFT_BORDER = new Vector2d(0,0);
    private final Vector2d UPPER_RIGHT_BORDER;
    protected UUID id = UUID.randomUUID();
    protected HashMap<Vector2d, List<Animal>> animals = new HashMap<>();
    protected List<MapChangeListener> observers = new ArrayList<>();
    protected MapVisualizer mapVisualizer = new MapVisualizer(this);

    protected int genomeSize;
    protected int startingEnergy;
    protected int minEnergyToBreed;
    protected int moveCost;

    public AbstractWorldMap(int width, int height, int genomeSize, int startingEnergy, int minEnergyToBreed, int moveCost){
        this.genomeSize = genomeSize;
        this.startingEnergy = startingEnergy;
        this.minEnergyToBreed = minEnergyToBreed;
        this.moveCost = moveCost;
        this.UPPER_RIGHT_BORDER = new Vector2d(width-1, height-1);

    }

    @Override
    public String toString(){
        Boundary currentBound = getCurrentBounds();
        return mapVisualizer.draw(currentBound.lowerLeft(), currentBound.upperRight());
    }

    public UUID getId(){
        return id;
    }

    @Override
    public void move(Animal animal){
        if (animal.getEnergy() <= 0){
            animals.get(animal.getPosition()).remove(animal);
            if (animals.get(animal.getPosition()).isEmpty()){
                animals.remove(animal.getPosition());
            }
            return;
        }

        Vector2d startingPosition = animal.getPosition();
        animal.update(this);

        if (canMoveTo(animal.getPosition())){
            animals.putIfAbsent(animal.getPosition(), new ArrayList<>());
            animals.get(animal.getPosition()).add(animal);
            animals.get(startingPosition).remove(animal);

            if (animals.get(startingPosition).isEmpty()){
                animals.remove(startingPosition);
            }
            mapChanged("Animal moved from position " + startingPosition + " into position " + animal.getPosition());
        }
    }

    @Override
    public void breedAnimals(List<Animal> animalsList){
        for (List<Animal> animalsOnPos : animals.values()){
            if (animalsOnPos.size() < 2) {
                continue;
            }

            //need to implement function that will sort the animals on position
            Animal animal1 = animalsOnPos.get(0);
            Animal animal2 = animalsOnPos.get(1);

            Animal child = animal1.breed(animal2);
            if (child != null) {
                animals.get(child.getPosition()).add(child);
                animalsList.add(child);
            }
        }
    }

    @Override
    public void place(Animal animal) throws PositionAlreadyOccupiedException {
        if (!canMoveTo(animal.getPosition())) {
            throw new PositionAlreadyOccupiedException(animal.getPosition());
        }
        animals.putIfAbsent(animal.getPosition(), new ArrayList<>());
        animals.get(animal.getPosition()).add(animal);

        mapChanged("Animal was added into position " + animal.getPosition());
    }

    @Override
    public boolean canMoveTo(Vector2d position){
        // should later change it to sth more cool
        return true;
//        return !animals.containsKey(position);
    }

    @Override
    public boolean isOccupied(Vector2d position){
//        System.out.println(animals.get(position));
        return animals.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position){
        if (animals.get(position) != null) {
                return animals.get(position).get(0);
        }
        return null;
    }

    @Override
    public ArrayList<WorldElement> getElements(){
        ArrayList<WorldElement> allAnimals = new ArrayList<>();
        for (List<Animal> setOfAnimals : animals.values()){
            allAnimals.addAll(setOfAnimals);
        }
        return allAnimals;
    }

    @Override
    public Boundary getCurrentBounds(){
        return new Boundary(LOWER_LEFT_BORDER, UPPER_RIGHT_BORDER);
    }

    public void addObserver(MapChangeListener mapChangeListener){
        observers.add(mapChangeListener);
    }

    public void removeObserver(MapChangeListener mapChangeListener){
        observers.remove(mapChangeListener);
    }

    private void mapChanged(String message){
        for (MapChangeListener mapChangeListener : observers){
            mapChangeListener.mapChanged(this, message);
        }
    }

    @Override
    public int getStartingEnergy(){
        return startingEnergy;
    }

    @Override
    public int getGenomeSize(){
        return genomeSize;
    }

    @Override
    public int getMinEnergyToBreed(){
        return minEnergyToBreed;
    }

    @Override
    public int getMoveCost() {
        return moveCost;
    }
}
