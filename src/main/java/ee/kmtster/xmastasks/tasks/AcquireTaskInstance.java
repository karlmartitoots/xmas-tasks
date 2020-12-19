package ee.kmtster.xmastasks.tasks;

import org.bukkit.ChatColor;

public class AcquireTaskInstance implements TaskInstance {
    private final AcquireTask task;
    private int amount;
    private boolean finished;

    public AcquireTaskInstance(AcquireTask task, int amount) {
        this.task = task;
        this.amount = amount;
        this.finished = false;
    }

    public AcquireTask getTask() {
        return task;
    }

    public void finish() {
        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public String toString() {
        return String.format("%sYour task is to acquire %s%s %sof %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                amount,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getItemToAcquire().name().toLowerCase().replace("_", " "));
    }
}
