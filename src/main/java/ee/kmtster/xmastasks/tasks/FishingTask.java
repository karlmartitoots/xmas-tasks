package ee.kmtster.xmastasks.tasks;

import org.bukkit.Material;

import java.util.Random;

public class FishingTask extends XmasTask {
    private Material fishToCatch;
    private int min;
    private int max;

    public FishingTask(int weight, Material fishToCatch, int min, int max) {
        super("fish", weight);
        this.fishToCatch = fishToCatch;
        this.min = min;
        this.max = max;
    }

    public Material getFishToCatch() {
        return fishToCatch;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public TaskInstance generate(Random random) {
        return new FishingTaskInstance(this, min + random.nextInt(max - min));
    }
}
