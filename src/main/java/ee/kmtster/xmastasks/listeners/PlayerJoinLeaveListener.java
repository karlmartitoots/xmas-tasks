package ee.kmtster.xmastasks.listeners;

import ee.kmtster.xmastasks.Leaderboard;
import ee.kmtster.xmastasks.XmasTasksPlugin;
import ee.kmtster.xmastasks.playerfiles.PlayerFilesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {

    private final XmasTasksPlugin plugin;
    private final PlayerFilesManager filesManager;
    private final Leaderboard leaderboard;

    public PlayerJoinLeaveListener(XmasTasksPlugin plugin, PlayerFilesManager filesManager, Leaderboard leaderboard) {
        this.plugin = plugin;
        this.filesManager = filesManager;
        this.leaderboard = leaderboard;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        filesManager.readTask(p).ifPresent(
                taskInstance -> {
                    if (taskInstance.isFinished()) {
                        p.sendMessage(String.format("%sYou still have a finished christmas task.", ChatColor.YELLOW));
                    } else {
                        p.sendMessage(String.format("%sYou still have an unfinished christmas task.", ChatColor.YELLOW));
                        p.sendMessage(taskInstance.display());
                    }
                }
        );

        if (!leaderboard.has(p))
            leaderboard.add(p);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        filesManager.writeTask(p);
        leaderboard.save(p);
    }
}

