package ee.kmtster.missions.listeners;

import ee.kmtster.missions.Leaderboard;
import ee.kmtster.missions.MissionsPlugin;
import ee.kmtster.missions.playerfiles.PlayerFilesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {

    private final MissionsPlugin plugin;
    private final PlayerFilesManager filesManager;
    private final Leaderboard leaderboard;

    public PlayerJoinLeaveListener(MissionsPlugin plugin, PlayerFilesManager filesManager, Leaderboard leaderboard) {
        this.plugin = plugin;
        this.filesManager = filesManager;
        this.leaderboard = leaderboard;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        filesManager.readMission(p).ifPresent(
                missionInstance -> {
                    if (missionInstance.isFinished()) {
                        p.sendMessage(String.format("%sYou still have an unclaimed Mission reward.", ChatColor.YELLOW));
                    } else {
                        p.sendMessage(String.format("%sYou still have an unfinished Mission.", ChatColor.YELLOW));
                        p.sendMessage(missionInstance.display());
                    }
                }
        );

        if (!leaderboard.has(p))
            leaderboard.add(p);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        filesManager.writeMission(p);
        leaderboard.save(p);
    }
}

