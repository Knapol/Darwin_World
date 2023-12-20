package agh.ics.oop.model;

import agh.ics.oop.model.map.WorldMap;

public class ConsoleMapDisplay implements MapChangeListener {
    private int changeCounter = 0;
    @Override
    public synchronized void mapChanged(WorldMap worldMap, String message) {
        changeCounter++;
        System.out.println(worldMap.getId());
        System.out.println(message);
        System.out.println(worldMap.toString());
        System.out.println(changeCounter + "\n");
    }
}
