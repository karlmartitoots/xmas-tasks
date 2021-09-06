package ee.kmtster.missions.missions;

import java.util.Random;

public abstract class Mission {
    private final String category;
    private int weight = -1;

    public Mission(String category, int weight) {
        this.category = category;
        this.weight = weight;
    }

    public Mission(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public int getWeight() {
        return weight;
    }

    public abstract MissionInstance<? extends Mission> generate(Random random);
}
