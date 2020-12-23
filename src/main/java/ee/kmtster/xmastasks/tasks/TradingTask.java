package ee.kmtster.xmastasks.tasks;

import org.bukkit.Material;

import java.util.Random;

public class TradingTask extends XmasTask {
    private Material materialToReceive;

    public TradingTask(int weight, Material materialToReceive) {
        super("trade", weight);
        this.materialToReceive = materialToReceive;
    }

    public Material getMaterialToReceive() {
        return materialToReceive;
    }

    @Override
    public TaskInstance generate(Random random) {
        return new TradingTaskInstance(this);
    }
}
