package ee.kmtster.xmastasks.listeners;

import ee.kmtster.xmastasks.XmasTaskManager;
import ee.kmtster.xmastasks.XmasTasksPlugin;
import ee.kmtster.xmastasks.tasks.TradingTaskInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.MerchantInventory;

public class TradingTaskListener implements Listener {

    private final XmasTasksPlugin plugin;
    private final XmasTaskManager taskManager;

    public TradingTaskListener(XmasTasksPlugin plugin, XmasTaskManager taskManager) {
        this.plugin = plugin;
        this.taskManager = taskManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onTradeVillagerEvent(InventoryClickEvent event){
        if (!(event.getClickedInventory() instanceof MerchantInventory)) // villager inventory
            return;

        if (!(event.getSlotType() == InventoryType.SlotType.RESULT)) // click on result slot
            return;

        if (event.getAction() == InventoryAction.NOTHING) // successful trade check
            return;

        if (!(event.getWhoClicked() instanceof Player)) // null check if not player
            return;

        Player p = (Player) event.getWhoClicked();
        if (!taskManager.hasTask(p) || !(taskManager.readTask(p) instanceof TradingTaskInstance)) // player does not have the task
            return;

        TradingTaskInstance taskInstance = (TradingTaskInstance) taskManager.readTask(p);
        if (event.getCurrentItem() == null || taskInstance.getTask().getMaterialToReceive() != event.getCurrentItem().getType()) // wrong item was traded
            return;

        if (taskInstance.isFinished()) { // already finished
            p.sendMessage(String.format("%sYou have already completed your Christmas Task. Claim your prize using /xmastasks reward.", ChatColor.YELLOW));
            return;
        }

        taskInstance.finish();
        p.sendMessage(String.format("%sYou have completed your Christmas Task! Claim your prize using /xmastasks reward.", ChatColor.YELLOW));
    }
}
