package ee.kmtster.missions.listeners;

import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.MissionsPlugin;
import ee.kmtster.missions.missions.CraftingMissionInstance;
import ee.kmtster.missions.missions.MissionInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

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

        MissionInstance missionInstance = missionManager.readMission(p);
        if (!(missionInstance instanceof CraftingMissionInstance))
            return;

        CraftingMissionInstance craftingMissionInstance = (CraftingMissionInstance) missionInstance;
        if (event.getCurrentItem() == null || craftingMissionInstance.getMission().getItemToCraft() != event.getCurrentItem().getType())
            return;

        if (craftingMissionInstance.isFinished()) { // already finished
            p.sendMessage(String.format("%sYou have already completed your Missions. Claim your prize using /xmasmissions reward.", ChatColor.YELLOW));
            return;
        }

        craftingMissionInstance.decrease(event.getCurrentItem().getAmount());

        if (craftingMissionInstance.isFinished()) { // just finished
            p.sendMessage(String.format("%sYou have completed your Missions! Claim your prize using /xmasmissions reward.", ChatColor.YELLOW));
        }
    }
}
