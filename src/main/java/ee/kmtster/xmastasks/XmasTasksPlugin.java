package ee.kmtster.xmastasks;

import ee.kmtster.xmastasks.commands.XmasTaskCommandExecutor;
import ee.kmtster.xmastasks.listeners.*;
import ee.kmtster.xmastasks.playerfiles.PlayerFilesManager;
import ee.kmtster.xmastasks.tasks.XmasTaskManager;
import org.bukkit.plugin.java.JavaPlugin;

public class XmasTasksPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("Enabled Christmas Tasks plugin.");

        loadConfig();

        XmasTaskManager taskManager = new XmasTaskManager();
        PlayerFilesManager filesManager = new PlayerFilesManager(this, taskManager);
        ConfLoader loader = new ConfLoader(this, taskManager, getConfig());

        new XmasTaskCommandExecutor(this, taskManager);

        new FishingTaskListener(this, taskManager, loader.get("fishing_progress_period", 3));
        new SlayTaskListener(this, taskManager, loader.get("slay_progress_period", 35));
        new TradingTaskListener(this, taskManager);
        new CraftingTaskListener(this, taskManager);

        new AnvilUnsafeEnchantmentListener(this);
        new RewardOpenListener(this, taskManager);
        new PlayerJoinLeaveListener(this, filesManager);

    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
