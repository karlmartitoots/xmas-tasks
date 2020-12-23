package ee.kmtster.xmastasks.tasks;

import org.bukkit.ChatColor;

public class TradingTaskInstance implements TaskInstance<TradingTask> {
    private final TradingTask task;
    private boolean finished;

    public TradingTaskInstance(TradingTask task) {
        this.task = task;
        this.finished = false;
    }

    public TradingTask getTask() {
        return task;
    }

    public void finish() {
        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public String progress() {
        return String.format("%sYour task to receive any amount of the item %s%s %sin trade with a villager is %sfinished.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getMaterialToReceive().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW,
                isFinished() ? "" : "not ");
    }

    @Override
    public String display() {
        return String.format("%sYour task is to trade with a villager and receive any amount of the item %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getMaterialToReceive().name().toLowerCase().replace("_", " "));
    }
}
