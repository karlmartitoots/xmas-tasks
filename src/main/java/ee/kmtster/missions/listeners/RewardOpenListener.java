package ee.kmtster.missions.listeners;

import ee.kmtster.missions.MissionsPlugin;
import ee.kmtster.missions.rewards.Rewards;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RewardOpenListener implements Listener {

    private final MissionsPlugin plugin;
    private final Rewards rewards;
    private final ItemStack presentItem = Rewards.present();

    public RewardOpenListener(MissionsPlugin plugin, Rewards rewards) {
        this.plugin = plugin;
        this.rewards = rewards;

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
        p.getWorld().dropItem(p.getLocation(), rewards.getOne());

        p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
    }
}
