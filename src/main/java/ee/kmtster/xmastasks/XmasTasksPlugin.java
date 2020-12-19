package ee.kmtster.xmastasks;

import ee.kmtster.xmastasks.commands.XmasTaskCommandExecutor;
import ee.kmtster.xmastasks.tasks.AcquireTask;
import ee.kmtster.xmastasks.tasks.FishingTask;
import ee.kmtster.xmastasks.tasks.SlayTask;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class XmasTasksPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("Enabled Christmas Tasks plugin.");

        loadConfig();

        // Make PlayerData Folder
//        PlayerStatsFilePreparation playerStatsFilePreparation = new PlayerStatsFilePreparation();
//        playerStatsFilePreparation.initializePlayerDataBase();

        XmasTaskManager taskManager = new XmasTaskManager();
        loadTasks(taskManager, getConfig());

        new XmasTaskCommandExecutor(this, taskManager);

        new SlayTaskListener(this, taskManager);
        new AnvilUnsafeEnchantmentListener(this);

    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void loadTasks(XmasTaskManager taskManager, FileConfiguration conf) {
        if (!conf.contains("tasks")){
            getServer().getConsoleSender().sendMessage("Config.yml is missing key 'tasks'.");
            return;
        }

        ConfigurationOptions options = conf.options();
        for (String category : taskManager.getDefaultCategories()) {
            if (conf.getConfigurationSection("tasks").contains(category)
                    && conf.getConfigurationSection("tasks" + options.pathSeparator() + category).getKeys(false).size() > 1) { // has more keys than just the 'weight' key

                ConfigurationSection section = conf.getConfigurationSection("tasks" + options.pathSeparator() + category);
                if (!section.contains("weight")) {
                    getLogger().info(String.format("Config.yml is missing key 'weight' under section %s. Defaulting to 1.", section.getName()));
                    taskManager.addTaskCategory(category, 1);
                } else {
                    taskManager.addTaskCategory(category, section.getInt("weight"));
                }

                switch (category) {
//                    case "mine":
//                        loadMiningTasks(taskManager, conf);
//                        continue;
//                    case "craft":
//                        loadCraftingTasks(taskManager, conf);
//                        continue;
                    case "slay":
                        loadSlayTasks(taskManager, conf, options);
                        continue;
                    case "acquire":
                        loadAcquireTasks(taskManager, conf, options);
                        continue;
                    case "fish":
                        loadFishingTasks(taskManager, conf, options);
                        continue;
//                    case "trade":
//                        loadTradingTasks(taskManager, conf);
//                        continue;
                }
            }
        }
    }

    private void loadFishingTasks(XmasTaskManager taskManager, FileConfiguration conf, ConfigurationOptions options) {

        Set<String> keys = conf.getConfigurationSection("tasks.fish").getKeys(false);
        keys.remove("weight");
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection("tasks.fish" + options.pathSeparator() + key);
            try {
                Material materialToCatch = Material.valueOf(key.toUpperCase());

                int weight = defaultIfAbsent(section, "weight", 1);
                int min = defaultIfAbsent(section, "min", 1);
                int max = defaultIfAbsent(section, "max", 1);

                taskManager.addTask("fish",
                        new FishingTask(weight,
                                materialToCatch,
                                min,
                                max));
            } catch (IllegalArgumentException e) {
                getLogger().info(String.format("Config.yml contains incorrect key '%s' under section %s. Skipping.", key, section.getName()));
            }
        }
    }

    private void loadAcquireTasks(XmasTaskManager taskManager, FileConfiguration conf, ConfigurationOptions options) {

        Set<String> keys = conf.getConfigurationSection("tasks.acquire").getKeys(false);
        keys.remove("weight");
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection("tasks.acquire" + options.pathSeparator() + key);
            try {
                Material materialToAcquire = Material.valueOf(key.toUpperCase());

                int weight = defaultIfAbsent(section, "weight", 1);
                int min = defaultIfAbsent(section, "min", 1);
                int max = defaultIfAbsent(section, "max", 1);

                taskManager.addTask("acquire",
                        new AcquireTask(weight,
                                materialToAcquire,
                                min,
                                max));
            } catch (IllegalArgumentException e) {
                getLogger().info(String.format("Config.yml contains incorrect key '%s' under section %s. Skipping.", key, section.getName()));
            }
        }
    }

    private void loadSlayTasks(XmasTaskManager taskManager, FileConfiguration conf, ConfigurationOptions options) {

        Set<String> keys = conf.getConfigurationSection("tasks.slay").getKeys(false);
        keys.remove("weight");
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection("tasks.slay" + options.pathSeparator() + key);
            EntityType mobToKill = EntityType.fromName(key);
            if (mobToKill == null) {
                getLogger().info(String.format("Config.yml contains incorrect key '%s' under section %s. Skipping.", key, section.getName()));
                continue;
            }

            int weight = defaultIfAbsent(section, "weight", 1);
            int min = defaultIfAbsent(section, "min", 1);
            int max = defaultIfAbsent(section, "max", 1);

            taskManager.addTask("slay",
                    new SlayTask(weight,
                            mobToKill,
                            min,
                            max));
        }

    }

    private int defaultIfAbsent(ConfigurationSection section, String key, int defaultValue) {
        if (!section.contains(key)) {
            getLogger().info(String.format("Config.yml is missing key '%s' under section %s. Defaulting to %s.", key, section.getName(), defaultValue));
            return defaultValue;
        }

        return section.getInt(key);
    }
}
