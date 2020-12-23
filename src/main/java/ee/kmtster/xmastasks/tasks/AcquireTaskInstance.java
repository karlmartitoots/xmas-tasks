package ee.kmtster.xmastasks.tasks;

import org.bukkit.ChatColor;

public class AcquireTaskInstance implements TaskInstance<AcquireTask> {
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

    public int getAmount() {
        return amount;
    }

    public void finish() {
        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public String progress() {
        return String.format("%sYour task to obtain a/an %s%s %sis %sfinished.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getItemToAcquire().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW,
                isFinished() ? "" : "not ");
    }

    @Override
    public String display() {
        return String.format("%sYour task is to obtain %s%s %ss.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                amount,
                task.getItemToAcquire().name().toLowerCase().replace("_", " "));
    }
}
