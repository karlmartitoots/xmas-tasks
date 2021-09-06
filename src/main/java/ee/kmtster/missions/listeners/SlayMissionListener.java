package ee.kmtster.missions.listeners;

import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.MissionsPlugin;
import ee.kmtster.missions.missions.SlayMissionInstance;
import ee.kmtster.missions.missions.MissionInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class SlayMissionListener implements Listener {

    private final MissionsPlugin plugin;
    private final MissionManager missionManager;
    private final int progressPeriod;

    public SlayMissionListener(MissionsPlugin plugin, MissionManager missionManager, int progressPeriod) {
        this.plugin = plugin;
        this.missionManager = missionManager;
        this.progressPeriod = progressPeriod;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        EntityType killed = e.getEntity().getType();

        if (killer == null)
            return;

        if (!missionManager.hasMission(killer))
            return;

        MissionInstance missionInstance = missionManager.readMission(killer);
        if (!(missionInstance instanceof SlayMissionInstance))
            return;

        if (missionInstance.isFinished()) { // already finished
            killer.sendMessage(String.format("%sYou have already completed your Mission. Claim your prize using /missions reward.", ChatColor.YELLOW));
            return;
        }

        SlayMissionInstance slayMissionInstance = (SlayMissionInstance) missionInstance;
        if (!killed.equals(slayMissionInstance.getMission().getMobToKill())) // correct mob
            return;

        slayMissionInstance.decrement();

        if (missionInstance.isFinished()) // just finished
            killer.sendMessage(String.format("%sYou have completed your Missions! Claim your prize using /missions reward.", ChatColor.YELLOW));
        else if (slayMissionInstance.getLeftToKill() % 5 == 0)
            killer.sendMessage(slayMissionInstance.progress());

    }
}
