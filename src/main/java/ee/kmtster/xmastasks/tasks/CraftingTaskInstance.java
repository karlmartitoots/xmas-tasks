package ee.kmtster.xmastasks.tasks;

import org.bukkit.Material;

public class CraftingTaskInstance implements TaskInstance {
    private final CraftingTask task;
    private int leftToCraft;

    public CraftingTaskInstance(CraftingTask task, int leftToCraft) {
        this.task = task;
        this.leftToCraft = leftToCraft;
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
