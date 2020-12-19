package ee.kmtster.xmastasks.tasks;

import org.bukkit.entity.EntityType;

import java.util.Random;

public class SlayTask extends XmasTask {
    private EntityType mobToKill;
    private int min;
    private int max;

    public SlayTask(int weight, EntityType mobToKill, int min, int max) {
        super("slay", weight);
        this.mobToKill = mobToKill;
        this.min = min;
        this.max = max;
    }

    public EntityType getMobToKill() {
        return mobToKill;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public TaskInstance generate(Random random) {
        return new SlayTaskInstance(this, min + random.nextInt(max - min));
    }
}
