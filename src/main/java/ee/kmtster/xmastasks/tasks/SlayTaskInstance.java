package ee.kmtster.xmastasks.tasks;

import org.bukkit.ChatColor;

public class SlayTaskInstance implements TaskInstance {
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
        return String.format("%s(Christmas Task) %s%s %sof %s%s %sleft to slay.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToKill,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getMobToKill().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW);
    }

    @Override
    public String display() {
        return String.format("%sYour task is to slay %s%s %sof %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToKill,
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getMobToKill().name().toLowerCase().replace("_", " "));
    }
}
