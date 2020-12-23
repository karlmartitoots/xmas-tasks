package ee.kmtster.xmastasks.tasks;

import org.bukkit.ChatColor;

public class SlayTaskInstance implements TaskInstance<SlayTask> {
    private final SlayTask task;
    private int leftToKill;

    public SlayTaskInstance(SlayTask task, int leftToKill) {
        this.task = task;
        this.leftToKill = leftToKill;
    }

    public SlayTask getTask() {
        return task;
    }

    public int getLeftToKill() {
        return leftToKill;
    }

    public void decrement() {
        this.leftToKill--;
    }

    @Override
    public boolean isFinished() {
        return leftToKill == 0;
    }

    @Override
    public String progress() {
        return String.format("%s(Christmas Task) %s%s %ss %sleft to slay.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToKill,
                task.getMobToKill().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW);
    }

    @Override
    public String display() {
        return String.format("%sYour task is to slay %s%s %ss.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToKill,
                task.getMobToKill().name().toLowerCase().replace("_", " "));
    }
}
