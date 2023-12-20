package agh.ics.oop;

import agh.ics.oop.model.MoveDirection;

import java.util.ArrayList;
import java.util.List;

public class OptionsParser {
    public static List<MoveDirection> returnMoveDirection(String[] args){
        List<MoveDirection> array = new ArrayList<>();

        for (String arg: args) {
            switch (arg) {
                case "f" -> array.add(MoveDirection.FORWARD);
                case "b" -> array.add(MoveDirection.BACKWARD);
                case "r" -> array.add(MoveDirection.RIGHT);
                case "l" -> array.add(MoveDirection.LEFT);
                default -> throw new IllegalArgumentException(arg + " is not legal move specification");
            }
        }
        return array;
    }
}
