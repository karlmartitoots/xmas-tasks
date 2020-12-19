package ee.kmtster.xmastasks.tasks;

import org.bukkit.ChatColor;

public class FishingTaskInstance implements TaskInstance {
    private final FishingTask task;
    private int leftToCatch;

    public FishingTaskInstance(FishingTask task, int leftToCatch) {
        this.task = task;
        this.leftToCatch = leftToCatch;
    }

    public FishingTask getTask() {
        return task;
    }

    public int getLeftToCatch() {
        return leftToCatch;
    }

    @Override
    public boolean isFinished() {
        return leftToCatch == 0;
    }

    @Override
    public String toString() {
        return String.format("%sYour task is to catch %s%s %sof %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCatch,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getFishToCatch().name().toLowerCase().replace("_", " "));
    }
}
