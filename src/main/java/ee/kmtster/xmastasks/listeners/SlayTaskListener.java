package ee.kmtster.xmastasks.listeners;

import ee.kmtster.xmastasks.XmasTaskManager;
import ee.kmtster.xmastasks.XmasTasksPlugin;
import ee.kmtster.xmastasks.tasks.SlayTaskInstance;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class SlayTaskListener implements Listener {

    private final XmasTasksPlugin plugin;
    private final XmasTaskManager taskManager;
    private final int progressPeriod;

    public SlayTaskListener(XmasTasksPlugin plugin, XmasTaskManager taskManager, int progressPeriod) {
        this.plugin = plugin;
        this.taskManager = taskManager;
        this.progressPeriod = progressPeriod;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        EntityType killed = e.getEntity().getType();

        if (killer == null)
            return;

        if (!taskManager.hasTask(killer))
            return;

        TaskInstance taskInstance = taskManager.readTask(killer);
        if (!(taskInstance instanceof SlayTaskInstance))
            return;

        if (taskInstance.isFinished()) { // already finished
            killer.sendMessage(String.format("%sYou have already completed your Christmas Task. Claim your prize using /xmastasks reward.", ChatColor.YELLOW));
            return;
        }

        SlayTaskInstance slayTaskInstance = (SlayTaskInstance) taskInstance;
        if (!killed.equals(slayTaskInstance.getTask().getMobToKill())) // correct mob
            return;

        slayTaskInstance.decrement();

        if (taskInstance.isFinished()) // just finished
            killer.sendMessage(String.format("%sYou have completed your Christmas Task! Claim your prize using /xmastasks reward.", ChatColor.YELLOW));
        else if (slayTaskInstance.getLeftToKill() % 5 == 0)
            killer.sendMessage(slayTaskInstance.progress());

    }
}
