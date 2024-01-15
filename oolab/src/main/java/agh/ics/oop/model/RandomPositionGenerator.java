package agh.ics.oop.model;

import java.util.*;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private final int equatorStart;
    private final int equatorEnd;

    private final List<Vector2d> vectorsOnEquator;
    private final List<Vector2d> vectorsBeyondEquator;
    private int size;

    public RandomPositionGenerator(int width, int height, int grassCount){
        this.equatorStart = 2 * (height / 5);
        this.equatorEnd = 3 * (height / 5)-1;
        this.vectorsOnEquator = new ArrayList<>();
        this.vectorsBeyondEquator = new ArrayList<>();
        this.size = grassCount;

        for (int x=0; x<width; x++){
            for (int y=0; y<equatorStart; y++){
                vectorsBeyondEquator.add(new Vector2d(x,y));
            }

            for (int y=equatorStart; y<=equatorEnd; y++){
                vectorsOnEquator.add(new Vector2d(x,y));
            }

            for (int y=equatorEnd+1; y<height; y++){
                vectorsBeyondEquator.add(new Vector2d(x,y));
            }
        }

        System.out.println(vectorsBeyondEquator.size()+" "+vectorsOnEquator.size());

        Collections.shuffle(vectorsOnEquator);
        Collections.shuffle(vectorsBeyondEquator);
    }

    @Override
    public Iterator<Vector2d> iterator(){
        return new PositionIterator();
    }

    public void addEmptyPosition(Vector2d pos){
        if (pos.getY() < equatorStart || pos.getY() > equatorEnd){
            vectorsBeyondEquator.add(pos);
        }
        vectorsOnEquator.add(pos);
    }

    public void prepareRandomPositionGenerator(int grassCount){
        Collections.shuffle(vectorsOnEquator);
        Collections.shuffle(vectorsBeyondEquator);
        this.size = grassCount;
    }

    private class PositionIterator implements Iterator<Vector2d> {

        private int equatorIndex = 0;
        private int beyondEquatorIndex = 0;

        private final Random random = new Random();

        @Override
        public boolean hasNext(){
            return equatorIndex+beyondEquatorIndex < size || (vectorsOnEquator.isEmpty() && vectorsBeyondEquator.isEmpty());
        }

        @Override
        public Vector2d next(){
            if (!hasNext()){
                throw new NoSuchElementException();
            }

            if (random.nextDouble() < 0.8 && !vectorsOnEquator.isEmpty()){
                equatorIndex++;
                Vector2d pos = vectorsOnEquator.get(vectorsOnEquator.size()-1);
                vectorsOnEquator.remove(vectorsOnEquator.size()-1);
                return pos;
            }
            beyondEquatorIndex++;
            Vector2d pos = vectorsBeyondEquator.get(vectorsBeyondEquator.size()-1);
            vectorsBeyondEquator.remove(vectorsBeyondEquator.size()-1);
            return pos;
        }
    }
}
