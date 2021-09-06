package ee.kmtster.missions.listeners;

import ee.kmtster.missions.SendPlayerMessage;
import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.MissionsPlugin;
import ee.kmtster.missions.missions.FishingMissionInstance;
import ee.kmtster.missions.missions.MissionInstance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishingMissionListener implements Listener {

    private final MissionsPlugin plugin;
    private final MissionManager missionManager;
    private final int progressPeriod;

    public FishingMissionListener(MissionsPlugin plugin, MissionManager missionManager, int progressPeriod) {
        this.plugin = plugin;
        this.missionManager = missionManager;
        this.progressPeriod = progressPeriod;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCatchFishEvent(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) // caught something
            return;

        Player p = e.getPlayer();
        if (!missionManager.hasMission(p)) // has mission
            return;

        MissionInstance missionInstance = missionManager.readMission(p);
        if (!(missionInstance instanceof FishingMissionInstance)) // is fishing mission
            return;

        if (missionInstance.isFinished()) { // already finished
            SendPlayerMessage.missionAlreadyCompleted(p);
            return;
        }

        FishingMissionInstance fishingMissionInstance = (FishingMissionInstance) missionInstance;
        if (e.getCaught() == null) // catch is not nothing
            return;

        if (fishingMissionInstance.getMission().getFishToCatch() == ((Item) e.getCaught()).getItemStack().getType()) // correct catch for the mission
            fishingMissionInstance.decrease(((Item) e.getCaught()).getItemStack().getAmount(), plugin);

        if (fishingMissionInstance.isFinished()) // just finished
            SendPlayerMessage.missionCompleted(p);
        else if (fishingMissionInstance.getLeftToCatch() % progressPeriod == 0)
            p.sendMessage(fishingMissionInstance.progress());
    }
}
