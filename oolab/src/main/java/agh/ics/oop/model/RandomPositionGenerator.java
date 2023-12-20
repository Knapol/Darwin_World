package agh.ics.oop.model;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;

public class RandomPositionGenerator implements Iterable<Vector2d> {

    private final List<Vector2d> vectors;
    private final int size;

    public RandomPositionGenerator(int maxWidth, int maxHeight, int grassCount){
        vectors = new ArrayList<>(maxWidth * maxHeight);
        size = grassCount;

        for (int x=0; x<=maxWidth; x++){
            for (int y=0; y<=maxHeight; y++){
                vectors.add(new Vector2d(x,y));
            }
        }

        Collections.shuffle(vectors);
    }

    @Override
    public Iterator<Vector2d> iterator(){
        return new PositionIterator();
    }

    private class PositionIterator implements Iterator<Vector2d> {

        private int cursor = 0;

        @Override
        public boolean hasNext(){
            return cursor < size;
        }

        @Override
        public Vector2d next(){
            if (!hasNext()){
                throw new NoSuchElementException();
            }
            return vectors.get(cursor++);
        }
    }
}
