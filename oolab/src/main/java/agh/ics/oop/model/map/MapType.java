package agh.ics.oop.model.map;

public enum MapType {
    FORESTED_EQUATORS,
    LIFE_GIVING_CORPSES;

    @Override
    public String toString(){
        return switch(this){
            case FORESTED_EQUATORS -> "Forested equators";
            case LIFE_GIVING_CORPSES ->  "Life-giving corpses";
        };
    }
}
