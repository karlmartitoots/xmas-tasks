package ee.kmtster.xmastasks.tasks;

import java.util.Random;

public abstract class XmasTask {
    private String category;
    private int weight;

    public XmasTask(String category, int weight) {
        this.category = category;
        this.weight = weight;
    }

    public String getCategory() {
        return category;
    }

    public int getWeight() {
        return weight;
    }

    public abstract TaskInstance generate(Random random);
}
