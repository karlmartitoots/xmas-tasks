package ee.kmtster.missions;

import ee.kmtster.missions.commands.MissionsCommandExecutor;
import ee.kmtster.missions.listeners.*;
import ee.kmtster.missions.playerfiles.PlayerFilesManager;
import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.rewards.Rewards;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MissionsPlugin extends JavaPlugin {
    private Leaderboard leaderboard;
    private PlayerFilesManager filesManager;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("Enabled Missions plugin.");

        loadConfig();
        leaderboard = new Leaderboard(this);
        MissionManager missionManager = new MissionManager();
        Rewards rewards = new Rewards();
        filesManager = new PlayerFilesManager(this, missionManager);
        ConfLoader loader = new ConfLoader(this, missionManager, getConfig(), rewards);

        // commands listener
        new MissionsCommandExecutor(this, missionManager, filesManager, leaderboard);

        // mission progress listeners
        new FishingMissionListener(this, missionManager, loader.get("fishing_progress_period", 3));
        new SlayMissionListener(this, missionManager, loader.get("slay_progress_period", 5));
        new VillagerTradingMissionListener(this, missionManager);
        new CraftingMissionListener(this, missionManager);

        // other listeners
        new RewardOpenListener(this, rewards);
        new PlayerJoinLeaveListener(this, filesManager, leaderboard);

        this.readPlayersMissions();
    }

    @Override
    public void onDisable() {
        this.writePlayersMissions();
        leaderboard.save();
    }

    private void readPlayersMissions() {
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        players.forEach(filesManager::readMission);
    }

    private void writePlayersMissions() {
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        players.forEach(filesManager::writeMission);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
