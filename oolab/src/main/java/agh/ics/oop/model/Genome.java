package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genome {
    private final List<Integer> genome;
    private final int genomeSize;
    private int activeGenome;
    private int genomeDirection;

    private final Random random;

    // for spawned animal
    public Genome(int size){
        this.random = new Random();
        this.genome = new ArrayList<>();
        this.genomeSize = size;
        createStarterGenome();

        this.activeGenome = random.nextInt(this.genomeSize);
        this.genomeDirection = 1;
    }

    // for child
    public Genome(List<Integer> genome){
        this.random = new Random();
        this.genome = genome;
        this.genomeSize = genome.size();
        this.activeGenome = random.nextInt(this.genomeSize);
        this.genomeDirection = 1;
    }

    public int getNextGene(AnimalBehavior animalBehavior){
        int gene = genome.get(activeGenome);
        activeGenome += genomeDirection;

        switch (animalBehavior){
            case NORMAL -> {
                if (activeGenome >= genomeSize){
                    activeGenome = 0;
                }
            }
            case THERE_AND_BACK -> {
                if (activeGenome >= genomeSize){
                    activeGenome = genomeSize-2;
                    genomeDirection = -genomeDirection;
                }
                else if (activeGenome < 0){
                    activeGenome = 1;
                    genomeDirection = -genomeDirection;
                }
            }
        }

        return gene;
    }

    public Genome createNewGenome(Genome weakerGenome, int energy, int energyWeaker){
        Genome leftGenome;
        Genome rightGenome;
        int leftEnergy;
        int rightEnergy;

        if (Math.random() > 0.5){
            leftGenome = this;
            rightGenome = weakerGenome;
            leftEnergy = energy;
            rightEnergy = energyWeaker;
        }
        else{
            leftGenome = weakerGenome;
            rightGenome = this;
            leftEnergy = energyWeaker;
            rightEnergy = energy;
        }

        double ratio = Math.min( leftEnergy, rightEnergy) / (double)(leftEnergy + rightEnergy);
        int midPoint = (int)Math.round((leftEnergy <= rightEnergy) ? ratio * this.genomeSize : this.genomeSize - ratio * this.genomeSize);

        List<Integer> newGenome = new ArrayList<>();

        for (int i=0; i<midPoint; i++){
            newGenome.add(leftGenome.genome.get(i));
        }

        for (int i=midPoint; i<genomeSize; i++){
            newGenome.add(rightGenome.genome.get(i));
        }

        return new Genome(newGenome);
    }

    //for spawned animals, at the beginning of the simulation
    private void createStarterGenome(){
        for (int i=0; i<genomeSize; i++){
            genome.add(random.nextInt(8));
        }
    }

    //dopytac czy git, czy trzeba inaczej
    public void mutate(int minMutations, int maxMutations){
        int numberOfMutations = random.nextInt(maxMutations - minMutations + 1) + minMutations;
        for (int i=0; i<numberOfMutations; i++){
            int genomeToMutate = random.nextInt(genomeSize);
            this.genome.set(genomeToMutate, random.nextInt(8));
        }
    }
}
