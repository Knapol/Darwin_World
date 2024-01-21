package agh.ics.oop.model.util;

import agh.ics.oop.model.Vector2d;

import java.util.*;

public class RandomLGCPositionGenerator implements Iterable<Vector2d> {

    private final List<Vector2d> vectorsNormal;
    private final List<Vector2d> vectorsNearCorpses;
    private int size;

    public RandomLGCPositionGenerator(int width, int height, int grassCount){
        this.vectorsNormal = new ArrayList<>();
        this.vectorsNearCorpses = new ArrayList<>();
        this.size = grassCount;

        for (int x=0; x<width; x++){
            for (int y=0; y<height; y++){
                vectorsNormal.add(new Vector2d(x,y));
            }
        }

        Collections.shuffle(vectorsNormal);
    }

    @Override
    public Iterator<Vector2d> iterator(){
        return new RandomLGCPositionGenerator.PositionIterator();
    }

    public void addNormalEmptyPosition(Vector2d pos, boolean nearCorpsesContains){
        vectorsNormal.add(pos);
        if (nearCorpsesContains){
            vectorsNearCorpses.remove(pos);
        }
    }

    public void addNearCorpsesPosition(Vector2d pos, boolean normalContains){
        vectorsNearCorpses.add(pos);
        if(normalContains) {
            vectorsNormal.remove(pos);
        }
    }

    public void prepareRandomPositionGenerator(int grassCount){
        Collections.shuffle(vectorsNormal);
        Collections.shuffle(vectorsNearCorpses);
        this.size = grassCount;
    }

    private class PositionIterator implements Iterator<Vector2d> {

        private int nearCorpsesIndex = 0;
        private int normalIndex = 0;

        private final Random random = new Random();

        @Override
        public boolean hasNext(){
            return normalIndex+nearCorpsesIndex < size && !(vectorsNearCorpses.isEmpty() && vectorsNormal.isEmpty());
        }

        @Override
        public Vector2d next(){
            if (!hasNext()){
                throw new NoSuchElementException();
            }

            if ((random.nextDouble() < 0.8 && !vectorsNearCorpses.isEmpty()) || vectorsNormal.isEmpty()){
                nearCorpsesIndex++;
                Vector2d pos = vectorsNearCorpses.get(vectorsNearCorpses.size()-1);
                vectorsNearCorpses.remove(vectorsNearCorpses.size()-1);
                return pos;
            }
            normalIndex++;
            Vector2d pos = vectorsNormal.get(vectorsNormal.size()-1);
            vectorsNormal.remove(vectorsNormal.size()-1);
            return pos;
        }
    }
}
