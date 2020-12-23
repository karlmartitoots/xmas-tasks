package ee.kmtster.xmastasks.tasks;

import org.bukkit.Material;

import java.util.Random;

public class CraftingTask extends XmasTask {
    private Material itemToCraft;
    private int min;
    private int max;

    public CraftingTask(int weight, Material itemToCraft, int min, int max) {
        super("craft", weight);
        this.itemToCraft = itemToCraft;
        this.min = min;
        this.max = max;
    }

    public CraftingTask(Material itemToCraft) {
        super("craft");
        this.itemToCraft = itemToCraft;
    }

    public Material getItemToCraft() {
        return itemToCraft;
    }

    @Override
    public TaskInstance generate(Random random) {
        return new CraftingTaskInstance(this,
                min == max ? min : min + random.nextInt(max - min));
    }
}
