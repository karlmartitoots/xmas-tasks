package ee.kmtster.xmastasks.listeners;

import ee.kmtster.xmastasks.tasks.XmasTaskManager;
import ee.kmtster.xmastasks.XmasTasksPlugin;
import ee.kmtster.xmastasks.tasks.FishingTaskInstance;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishingTaskListener implements Listener {

    private final XmasTasksPlugin plugin;
    private final XmasTaskManager taskManager;
    private final int progressPeriod;

    public FishingTaskListener(XmasTasksPlugin plugin, XmasTaskManager taskManager, int progressPeriod) {
        this.plugin = plugin;
        this.taskManager = taskManager;
        this.progressPeriod = progressPeriod;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCatchFishEvent(PlayerFishEvent e) {
        plugin.getLogger().info("state "+e.getState());
        if (e.getState() != PlayerFishEvent.State.CAUGHT_ENTITY && e.getState() != PlayerFishEvent.State.CAUGHT_FISH) // caught something
            return;

        plugin.getLogger().info("Caught1 "+e.getCaught());
        plugin.getLogger().info("caught type "+e.getCaught().getType());
        plugin.getLogger().info("itemstack "+((Item) e.getCaught()).getItemStack());
        plugin.getLogger().info("itemstack type "+((Item) e.getCaught()).getItemStack().getType());
        Player p = e.getPlayer();
        if (!taskManager.hasTask(p)) // has task
            return;

        TaskInstance taskInstance = taskManager.readTask(p);
        if (!(taskInstance instanceof FishingTaskInstance)) // is fishing task
            return;

        if (taskInstance.isFinished()) { // already finished
            p.sendMessage(String.format("%sYou have already completed your Christmas Task. Claim your prize using /xmastasks reward.", ChatColor.YELLOW));
            return;
        }

        FishingTaskInstance fishingTaskInstance = (FishingTaskInstance) taskInstance;
        if (e.getCaught() == null) // catch is not nothing
            return;

        plugin.getLogger().info("Caught2 "+e.getCaught());
        plugin.getLogger().info("itemstack "+((Item) e.getCaught()).getItemStack());
        plugin.getLogger().info("itemstack type "+((Item) e.getCaught()).getItemStack().getType());

        ItemStack caught = ((Item) e.getCaught()).getItemStack();
        if (fishingTaskInstance.getTask().getFishToCatch() == caught.getType()) // correct catch for the task
            fishingTaskInstance.decrease(caught.getAmount());

        if (fishingTaskInstance.isFinished()) // just finished
            p.sendMessage(String.format("%sYou have completed your Christmas Task! Claim your prize using /xmastasks reward.", ChatColor.YELLOW));
        else if (fishingTaskInstance.getLeftToCatch() % progressPeriod == 0)
            p.sendMessage(fishingTaskInstance.progress());
    }
}
