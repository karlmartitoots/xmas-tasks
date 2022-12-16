package ee.kmtster.missions.listeners;

import ee.kmtster.missions.missions.Mission;
import ee.kmtster.missions.missions.MissionInstance;
import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.MissionsPlugin;
import ee.kmtster.missions.missions.CraftingMissionInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingMissionListener implements Listener {

    private final MissionsPlugin plugin;
    private final MissionManager missionManager;

    public CraftingMissionListener(MissionsPlugin plugin, MissionManager missionManager) {
        this.plugin = plugin;
        this.missionManager = missionManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) // is player
            return;

        Player p = (Player) event.getWhoClicked();
        if (!missionManager.hasMission(p)) // has mission
            return;

        MissionInstance<? extends Mission> missionInstance = missionManager.readMission(p);
        if (!(missionInstance instanceof CraftingMissionInstance))
            return;

        CraftingMissionInstance craftingMissionInstance = (CraftingMissionInstance) missionInstance;
        if (event.getCurrentItem() == null || craftingMissionInstance.getMission().getItemToCraft() != event.getCurrentItem().getType())
            return;

        if (craftingMissionInstance.isFinished()) { // already finished
            p.sendMessage(String.format("%sYou have already completed your mission. Claim your prize using /missions reward.", ChatColor.YELLOW));
            return;
        }

        int countBeforeCraft = itemCountOnPlayer(p, craftingMissionInstance.getMission().getItemToCraft());
        plugin.getServer().getScheduler().runTaskLater(plugin, new CraftTaskUpdate(countBeforeCraft, p, craftingMissionInstance), 1);
    }

    private static int itemCountOnPlayer(Player p, Material targetItem) {
        ItemStack itemOnCursor = p.getItemOnCursor();
        return p.getInventory().all(targetItem).values().stream()
                .map(ItemStack::getAmount)
                .reduce(Integer::sum)
                .orElse(0) + itemOnCursor.getAmount();
    }

    private static class CraftTaskUpdate implements Runnable {
        final Player player;
        final int countBefore;
        final CraftingMissionInstance missionInstance;

        CraftTaskUpdate(int amountBefore, Player player, CraftingMissionInstance missionInstance) {
            this.countBefore = amountBefore;
            this.player = player;
            this.missionInstance = missionInstance;
        }

        // player can probably exploit this if he manages to pickup additional items on the same tick when clicking to craft the item :(
        public void run() {
            int countAfterCraft = itemCountOnPlayer(player, missionInstance.getMission().getItemToCraft());
            int craftCount = countAfterCraft - countBefore;

            missionInstance.decrease(craftCount);
            player.sendMessage(missionInstance.progress());

            if (missionInstance.isFinished()) { // just finished
                player.sendMessage(String.format("%sYou have completed your mission! Claim your prize using /missions reward.", ChatColor.YELLOW));
            }
        }
    }
}
