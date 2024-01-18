package agh.ics.oop.model;

public enum AnimalBehavior {
    NORMAL,
    THERE_AND_BACK;

    @Override
    public String toString(){
        return switch(this){
            case NORMAL -> "Normal";
            case THERE_AND_BACK -> "There and back";
        };
    }
}
