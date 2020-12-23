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

    public void decrease(int by) {
        this.leftToCatch = Math.max(0, this.leftToCatch - by);
    }

    @Override
    public boolean isFinished() {
        return leftToCatch == 0;
    }

    public String progress() {
        return String.format("%s(Christmas Task) %s%s %sof %s%s %sleft to go.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCatch,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getFishToCatch().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW);
    }

    @Override
    public String display() {
        return String.format("%sYour task is to catch %s%s %sof %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCatch,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getFishToCatch().name().toLowerCase().replace("_", " "));
    }
}
