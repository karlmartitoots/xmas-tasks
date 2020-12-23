package ee.kmtster.xmastasks;

import ee.kmtster.xmastasks.commands.XmasTaskCommandExecutor;
import ee.kmtster.xmastasks.listeners.*;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public class XmasTasksPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("Enabled Christmas Tasks plugin.");

        loadConfig();

// Make PlayerData Folder
//        PlayerStatsFilePreparation playerStatsFilePreparation = new PlayerStatsFilePreparation();
//        playerStatsFilePreparation.initializePlayerDataBase();

        XmasTaskManager taskManager = new XmasTaskManager();
        ConfLoader loader = new ConfLoader(this, taskManager, getConfig());

        new XmasTaskCommandExecutor(this, taskManager);

        new FishingTaskListener(this, taskManager, loader.get("fishing_progress_period", 3));
        new SlayTaskListener(this, taskManager, loader.get("slay_progress_period", 35));
        new TradingTaskListener(this, taskManager);
        new CraftingTaskListener(this, taskManager);

        new AnvilUnsafeEnchantmentListener(this);
        new RewardOpenListener(this, taskManager);

    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
