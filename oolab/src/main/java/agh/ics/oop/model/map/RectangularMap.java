package agh.ics.oop.model.map;

import agh.ics.oop.model.Boundary;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.map.AbstractWorldMap;

public class RectangularMap extends AbstractWorldMap {
    private static final Vector2d LOWER_LEFT_BORDER = new Vector2d(0,0);
    private final Vector2d UPPER_RIGHT_BORDER;

    public RectangularMap(int width, int height){
        this.UPPER_RIGHT_BORDER = new Vector2d(width-1, height-1);
    }

    @Override
    public Boundary getCurrentBounds(){
        return new Boundary(LOWER_LEFT_BORDER, UPPER_RIGHT_BORDER);
    }

    @Override
    public boolean canMoveTo(Vector2d position){
        return super.canMoveTo(position) && position.follows(LOWER_LEFT_BORDER) && position.precedes(UPPER_RIGHT_BORDER);
    }
}
