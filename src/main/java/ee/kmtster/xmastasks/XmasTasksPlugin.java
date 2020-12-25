package ee.kmtster.xmastasks;

import ee.kmtster.xmastasks.commands.XmasTaskCommandExecutor;
import ee.kmtster.xmastasks.listeners.*;
import ee.kmtster.xmastasks.playerfiles.PlayerFilesManager;
import ee.kmtster.xmastasks.tasks.XmasTaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class XmasTasksPlugin extends JavaPlugin {
    private Leaderboard leaderboard;
    private PlayerFilesManager filesManager;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("Enabled Christmas Tasks plugin.");

        loadConfig();
        leaderboard = new Leaderboard(this);
        XmasTaskManager taskManager = new XmasTaskManager();
        filesManager = new PlayerFilesManager(this, taskManager);
        ConfLoader loader = new ConfLoader(this, taskManager, getConfig());

        // commands listener
        new XmasTaskCommandExecutor(this, taskManager, filesManager, leaderboard);

        // task progress listeners
        new FishingTaskListener(this, taskManager, loader.get("fishing_progress_period", 3));
        new SlayTaskListener(this, taskManager, loader.get("slay_progress_period", 5));
        new TradingTaskListener(this, taskManager);
        new CraftingTaskListener(this, taskManager);

        // other listeners
        new AnvilUnsafeEnchantmentListener(this);
        new RewardOpenListener(this, taskManager);
        new PlayerJoinLeaveListener(this, filesManager, leaderboard);
        new DefaultRewards(this, taskManager);

        this.readPlayersTasks();
    }

    @Override
    public void onDisable() {
        this.writePlayersTasks();
        leaderboard.save();
    }

    private void readPlayersTasks() {
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        players.forEach(filesManager::readTask);
    }

    private void writePlayersTasks() {
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        players.forEach(filesManager::writeTask);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
