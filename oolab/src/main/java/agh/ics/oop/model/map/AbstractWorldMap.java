package agh.ics.oop.model.map;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap {
    private static final Vector2d LOWER_LEFT_BORDER = new Vector2d(0,0);
    private final Vector2d UPPER_RIGHT_BORDER;
    protected UUID id = UUID.randomUUID();
    protected HashMap<Vector2d, List<Animal>> animals = new HashMap<>();
    protected HashMap<Vector2d, Grass> grasses = new HashMap<>();
    protected List<MapChangeListener> observers = new ArrayList<>();
    protected MapVisualizer mapVisualizer = new MapVisualizer(this);

    protected int genomeSize;
    private final int minMutations;
    private final int maxMutations;
    protected int startingEnergy;
    private final int minEnergyToBreed;
    private final int energyUseForBreeding;
    protected int moveCost;
    protected int grassEnergy;
    protected int grassPerDay;
    private final int startingNumberOfAnimals;
    private final AnimalBehavior animalBehavior;
    private int animalCount;

    private AnimalComparator animalComparator;

    public AbstractWorldMap(Settings settings){
        this.UPPER_RIGHT_BORDER = new Vector2d(settings.mapWidth()-1, settings.mapHeight()-1);

        this.startingNumberOfAnimals = settings.numberOfAnimals();

        this.genomeSize = settings.genomeSize();

        this.minMutations = settings.minMutations();
        this.maxMutations = settings.maxMutations();

        this.animalBehavior = settings.animalBehavior();

        this.startingEnergy = settings.startingEnergy();
        this.moveCost = settings.moveCost();
        this.minEnergyToBreed = settings.minEnergyToBreed();
        this.energyUseForBreeding = settings.energyUseForBreeding();

        this.grassEnergy = settings.grassEnergy();
        this.grassPerDay = settings.grassPerDay();

        this.animalCount = 0;

        this.animalComparator = new AnimalComparator();
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
        Vector2d startingPosition = animal.getPosition();
        animal.update(this);

        animals.putIfAbsent(animal.getPosition(), new ArrayList<>());
        animals.get(animal.getPosition()).add(animal);
        animals.get(startingPosition).remove(animal);

        if (animals.get(startingPosition).isEmpty()){
            animals.remove(startingPosition);
        }
    }

    @Override
    public void handleAnimalDeath(Animal animal){
        animals.get(animal.getPosition()).remove(animal);
        if (animals.get(animal.getPosition()).isEmpty()){
            animals.remove(animal.getPosition());
        }
    }

    @Override
    public void breedAnimals(List<Animal> animalsList){
        for (List<Animal> animalsOnPos : animals.values()){
            if (animalsOnPos.size() < 2) {
                continue;
            }

            animalsOnPos.sort(animalComparator.reversed());

            Animal animal1 = animalsOnPos.get(0);
            Animal animal2 = animalsOnPos.get(1);

            Animal child = animal1.breed(animal2);
            if (child != null) {
                animals.get(child.getPosition()).add(child);
                animalsList.add(child);
                animalCount++;
            }
        }
    }

    @Override
    public void eatGrass(){
        Iterator<Vector2d> it = new HashSet<>(grasses.keySet()).iterator();
        while (it.hasNext()){
            Vector2d pos = it.next();
            if (animals.get(pos) != null){
                if (animals.get(pos).size() >= 2) {
                    animals.get(pos).sort(animalComparator.reversed());
                }
                animals.get(pos).get(0).eat(this.grassEnergy);

                grasses.remove(pos);
                updateRandomPositionGenerator(pos);
            }
        }
    }

    protected abstract void updateRandomPositionGenerator(Vector2d pos);

    @Override
    public void place(Animal animal) {
        animals.putIfAbsent(animal.getPosition(), new ArrayList<>());
        animals.get(animal.getPosition()).add(animal);
        animalCount++;

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

    public void mapChanged(String message){
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

    @Override
    public int getStartingNumberOfAnimals(){
        return startingNumberOfAnimals;
    }

    @Override
    public int getWidth(){
        return UPPER_RIGHT_BORDER.getX()+1;
    }

    @Override
    public int getHeight(){
        return UPPER_RIGHT_BORDER.getY()+1;
    }

    @Override
    public int getMinMutations(){
        return minMutations;
    }

    @Override
    public int getMaxMutations(){
        return maxMutations;
    }

    @Override
    public AnimalBehavior getAnimalBehavior(){
        return animalBehavior;
    }

    @Override
    public int getEnergyUseForBreeding(){
        return energyUseForBreeding;
    }

    @Override
    public int getAnimalCount(){
        return animalCount;
    }

    @Override
    public int getNextAnimalID(){
        return animalCount+1;
    }
}
