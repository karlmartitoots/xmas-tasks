package ee.kmtster.xmastasks.listeners;

import ee.kmtster.xmastasks.XmasTaskManager;
import ee.kmtster.xmastasks.XmasTasksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static ee.kmtster.xmastasks.XmasTaskManager.present;
import static org.bukkit.Particle.SNOW_SHOVEL;

public class RewardOpenListener implements Listener {

    private final XmasTasksPlugin plugin;
    private final XmasTaskManager taskManager;
    private final ItemStack presentItem = present();

    public RewardOpenListener(XmasTasksPlugin plugin, XmasTaskManager taskManager) {
        this.plugin = plugin;
        this.taskManager = taskManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRightClickEvent(PlayerInteractEvent event) {
        if (event.getItem() == null) // has an item in hand
            return;

        ItemStack item = new ItemStack(event.getItem());
        item.setAmount(1);

        if (!presentItem.equals(item)) // is the present item
            return;

        event.setCancelled(true);

        Player p = event.getPlayer();
        p.getInventory().removeItem(presentItem);
        p.getWorld().dropItem(p.getLocation(), taskManager.nextReward());
        p.getWorld().spawnParticle(SNOW_SHOVEL, p.getLocation(), 5);
        p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
    }
}
