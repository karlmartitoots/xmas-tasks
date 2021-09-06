package ee.kmtster.missions.listeners;

import ee.kmtster.missions.SendPlayerMessage;
import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.MissionsPlugin;
import ee.kmtster.missions.missions.VillagerTradingMissionInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.MerchantInventory;

public class VillagerTradingMissionListener implements Listener {

    private final MissionsPlugin plugin;
    private final MissionManager missionManager;

    public VillagerTradingMissionListener(MissionsPlugin plugin, MissionManager missionManager) {
        this.plugin = plugin;
        this.missionManager = missionManager;

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
        if (!missionManager.hasMission(p) || !(missionManager.readMission(p) instanceof VillagerTradingMissionInstance)) // player does not have the mission
            return;

        VillagerTradingMissionInstance missionInstance = (VillagerTradingMissionInstance) missionManager.readMission(p);
        if (event.getCurrentItem() == null || missionInstance.getMission().getMaterialToReceive() != event.getCurrentItem().getType()) // wrong item was traded
            return;

        if (missionInstance.isFinished()) { // already finished
            SendPlayerMessage.missionAlreadyCompleted(p);
            return;
        }

        missionInstance.finish();
        SendPlayerMessage.missionCompleted(p);
    }
}
