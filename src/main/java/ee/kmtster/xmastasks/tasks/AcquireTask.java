package ee.kmtster.xmastasks.tasks;

import org.bukkit.Material;

import java.util.Random;

public class AcquireTask extends XmasTask {
    private Material itemToAcquire;
    private int min;
    private int max;

    public AcquireTask(int weight, Material itemToAcquire, int min, int max) {
        super("acquire", weight);
        this.itemToAcquire = itemToAcquire;
        this.min = min;
        this.max = max;
    }

    public AcquireTask(int weight, Material itemToAcquire) { // default 1
        super("acquire", weight);
        this.itemToAcquire = itemToAcquire;
        this.min = 1;
        this.max = 1;
    }

    public Material getItemToAcquire() {
        return itemToAcquire;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public TaskInstance generate(Random random) {
        return new AcquireTaskInstance(this,
                min == max ? min : min + random.nextInt(max - min));
    }
}
