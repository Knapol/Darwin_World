package agh.ics.oop.model.map;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap {
    private static final Vector2d LOWER_LEFT_BORDER = new Vector2d(0,0);
    private final Vector2d UPPER_RIGHT_BORDER;
    protected UUID id = UUID.randomUUID();
    protected HashMap<Vector2d, HashSet<Animal>> animals = new HashMap<>();
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

    private final AnimalComparator animalComparator;

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
        animal.update();

        animals.putIfAbsent(animal.getPosition(), new HashSet<>());

        if (startingPosition != animal.getPosition()) {
            animals.get(animal.getPosition()).add(animal);
            animals.get(startingPosition).remove(animal);
        }

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
        for (Set<Animal> animalsOnPos : animals.values()){
            if (animalsOnPos.size() < 2) {
                continue;
            }
            Set<Animal> animalsOnPosCopy = new HashSet<>(animalsOnPos);

            Animal animal1 = animalsOnPosCopy.stream()
                    .max(animalComparator)
                    .orElse(null);

            animalsOnPosCopy.remove(animal1);

            Animal animal2 = animalsOnPosCopy.stream()
                    .max(animalComparator)
                    .orElse(null);


            if(animal1 == null || animal2 == null){
                continue;
            }

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

                Animal animal = animals.get(pos).stream()
                        .max(animalComparator)
                        .orElse(null);

                if (animal != null) {
                    animal.eat(this.grassEnergy);
                }

                grasses.remove(pos);
                updateRandomPositionGenerator(pos);
            }
        }
    }

    protected abstract void updateRandomPositionGenerator(Vector2d pos);

    @Override
    public void place(Animal animal) {
        animals.putIfAbsent(animal.getPosition(), new HashSet<>());
        animals.get(animal.getPosition()).add(animal);
        animalCount++;
    }

    @Override
    public boolean isOccupied(Vector2d position){
        return animals.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position){
        if (animals.get(position) != null) {
            return animals.get(position).stream()
                    .max(animalComparator)
                    .orElse(null);
        }
        return null;
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

    //statistics
    @Override
    public int getAnimalCount(){
        return animalCount;
    }

    @Override
    public int getNextAnimalID(){
        return animalCount+1;
    }

    @Override
    public int getGrassCount(){
        return grasses.size();
    }

    @Override
    public int getEmptyFieldsCount(){
        Set<Vector2d> worldElements = new HashSet<>(animals.keySet());
        worldElements.addAll(grasses.keySet());
        return getHeight()*getWidth() - worldElements.size();
    }
}
