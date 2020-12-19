package ee.kmtster.xmastasks;

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

    public SlayTaskListener(XmasTasksPlugin plugin, XmasTaskManager taskManager) {
        this.plugin = plugin;
        this.taskManager = taskManager;

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

        SlayTaskInstance slayTaskInstance = (SlayTaskInstance) taskInstance;
        if (!killed.equals(slayTaskInstance.getTask().getMobToKill()))
            return;

        if (!slayTaskInstance.isFinished())
            slayTaskInstance.decrement();
        else
            killer.sendMessage(String.format("%sYou have completed your Christmas Task!", ChatColor.YELLOW));
    }
}
