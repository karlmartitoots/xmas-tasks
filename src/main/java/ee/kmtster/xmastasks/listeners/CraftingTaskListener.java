package ee.kmtster.xmastasks.listeners;

import ee.kmtster.xmastasks.XmasTaskManager;
import ee.kmtster.xmastasks.XmasTasksPlugin;
import ee.kmtster.xmastasks.tasks.CraftingTaskInstance;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftingTaskListener implements Listener {

    private final XmasTasksPlugin plugin;
    private final XmasTaskManager taskManager;

    public CraftingTaskListener(XmasTasksPlugin plugin, XmasTaskManager taskManager) {
        this.plugin = plugin;
        this.taskManager = taskManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) // is player
            return;

        Player p = (Player) event.getWhoClicked();
        if (!taskManager.hasTask(p)) // has task
            return;

        TaskInstance taskInstance = taskManager.readTask(p);
        if (!(taskInstance instanceof CraftingTaskInstance))
            return;

        CraftingTaskInstance craftingTaskInstance = (CraftingTaskInstance) taskInstance;
        if (event.getCurrentItem() == null || craftingTaskInstance.getTask().getItemToCraft() != event.getCurrentItem().getType())
            return;

        if (craftingTaskInstance.isFinished()) { // already finished
            p.sendMessage(String.format("%sYou have already completed your Christmas Task. Claim your prize using /xmastasks reward.", ChatColor.YELLOW));
            return;
        }

        craftingTaskInstance.decrease(event.getCurrentItem().getAmount());


        if (craftingTaskInstance.isFinished()) { // already finished
            p.sendMessage(String.format("%sYou have completed your Christmas Task! Claim your prize using /xmastasks reward.", ChatColor.YELLOW));
        }
    }
}
