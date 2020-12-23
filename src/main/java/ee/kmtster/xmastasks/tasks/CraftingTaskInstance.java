package ee.kmtster.xmastasks.tasks;

import org.bukkit.ChatColor;

public class CraftingTaskInstance implements TaskInstance {
    private final CraftingTask task;
    private int leftToCraft;

    public CraftingTaskInstance(CraftingTask task, int leftToCraft) {
        this.task = task;
        this.leftToCraft = leftToCraft;
    }

    public CraftingTask getTask() {
        return task;
    }

    public int getLeftToCraft() {
        return leftToCraft;
    }

    public void decrease(int by) {
        this.leftToCraft = Math.max(0, this.leftToCraft - by);
    }

    @Override
    public boolean isFinished() {
        return this.leftToCraft == 0;
    }

    @Override
    public String progress() {
        return String.format("%s(Christmas Task) %s%s %sof %s%s %sleft to craft.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCraft,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getItemToCraft().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW);
    }

    @Override
    public String display() {
        return String.format("%sYour task is to craft %s%s %sof %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCraft,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getItemToCraft().name().toLowerCase().replace("_", " "));
    }
}
