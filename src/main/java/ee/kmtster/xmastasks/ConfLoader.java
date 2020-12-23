package ee.kmtster.xmastasks;

import ee.kmtster.xmastasks.tasks.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Logger;

public class ConfLoader {

    private final Logger logger;
    private final FileConfiguration conf;

    private char PATH_SEPARATOR = '.';
    private final String wt = "weight";
    private final String TASKS_SECTION = "tasks";
    private final String REWARDS_SECTION = "rewards";

    private final String ACQUIRE_SUBSECTION = "acquire";
    private final String CRAFT_SUBSECTION = "craft";
    private final String FISH_SUBSECTION = "fish";
    private final String SLAY_SUBSECTION = "slay";
    private final String TRADE_SUBSECTION = "trade";

    private final String ACQUIRE_SECTION = TASKS_SECTION + PATH_SEPARATOR + ACQUIRE_SUBSECTION;
    private final String CRAFT_SECTION = TASKS_SECTION + PATH_SEPARATOR + CRAFT_SUBSECTION;
    private final String FISH_SECTION = TASKS_SECTION + PATH_SEPARATOR + FISH_SUBSECTION;
    private final String SLAY_SECTION = TASKS_SECTION + PATH_SEPARATOR + SLAY_SUBSECTION;
    private final String TRADE_SECTION = TASKS_SECTION + PATH_SEPARATOR + TRADE_SUBSECTION;

    private final List<String> categories = Arrays.asList("acquire","craft","fish","slay","trade");

    public ConfLoader(Plugin plugin, XmasTaskManager taskManager, FileConfiguration conf) {
        this.logger = plugin.getLogger();
        this.conf = conf;

        this.PATH_SEPARATOR = conf.options().pathSeparator();

        loadTasks(taskManager, conf);
    }

    private void info(String message) {
        this.logger.info(message);
    }

    private void info(String message, String... args) {
        this.logger.info(String.format(message, args));
    }

    private void invalidKey(ConfigurationSection section, String key) {
        info("Config.yml contains incorrect key '%s' under section %s. Skipping.", key, section.getCurrentPath());
    }

    private void loadTasks(XmasTaskManager taskManager, FileConfiguration conf) {
        if (!conf.contains(TASKS_SECTION)){
            info("Config.yml is missing key 'tasks'.");
            return;
        }

        if (!conf.contains(REWARDS_SECTION)){
            info("Config.yml is missing key 'tasks'.");
            return;
        }


        for (String category : this.categories) {
            if (!conf.getConfigurationSection(TASKS_SECTION).contains(category))
                continue;

            ConfigurationSection section = conf.getConfigurationSection(TASKS_SECTION + PATH_SEPARATOR + category);
            if (section.getKeys(false).size() < 2) // has to have more keys than just the 'weight' key
                continue;

            int weight = defaultIfAbsent(section, wt, 1);
            if (!positive(section, wt, weight)) continue;

            taskManager.addTaskCategory(category, weight);

            switch (category) {
                case CRAFT_SUBSECTION:
                    loadCraftingTasks(taskManager, conf);
                    continue;
                case SLAY_SUBSECTION:
                    loadSlayTasks(taskManager, conf);
                    continue;
                case ACQUIRE_SUBSECTION:
                    loadAcquireTasks(taskManager, conf);
                    continue;
                case FISH_SUBSECTION:
                    loadFishingTasks(taskManager, conf);
                    continue;
                case TRADE_SUBSECTION:
                    loadTradingTasks(taskManager, conf);
            }
        }

        loadRewards(taskManager, conf);
    }

    private void loadRewards(XmasTaskManager taskManager, FileConfiguration conf) {

        Set<String> keys = conf.getConfigurationSection(REWARDS_SECTION).getKeys(false);
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection(REWARDS_SECTION + PATH_SEPARATOR + key);
            if (key.startsWith("enchanted_book")) {

                if (!section.contains("enchantments")) {
                    info("Config.yml is missing 'enchantments' value under section %s. 'enchantments' has to be a string list. Skipping. ",
                            section.getCurrentPath());
                    continue;
                }

                if (!section.isList("enchantments")) {
                    info("Config.yml contains incorrect 'enchantments' value under section %s. 'enchantments' has to be a string list. Skipping. ",
                            section.getCurrentPath());
                    continue;
                }

                int weight = defaultIfAbsent(section, wt, 1);
                if (!positive(section, wt, weight)) continue;

                List<String> enchantmentStrs = validateEnchantments(section, section.getStringList("enchantments"));

                Map<Enchantment, Integer> enchantments = convertToEnchantments(section, enchantmentStrs);

                taskManager.addReward(()->{
                    ItemStack reward = new ItemStack(Material.ENCHANTED_BOOK);
                    for (Enchantment ench : enchantments.keySet()) {
                        reward.addUnsafeEnchantment(ench, enchantments.get(ench));
                    }

                    return reward;
                }, weight);
            }
        }
    }

    private Map<Enchantment, Integer> convertToEnchantments(ConfigurationSection section, List<String> enchantmentStrs) {
        Map<Enchantment, Integer> target = new LinkedHashMap<>();
        for (String enchStr : enchantmentStrs) {

            if (enchStr.contains("|")) {
                String[] parts = enchStr.split("\\|");
                if (parts.length > 2) {
                    info("Config.yml has invalid 'enchantments' value %s under section %s. 'enchantments' has to be a string list. " +
                                    "Can not contain more than one '|'. For example: power|6 is valid. Skipping. ",
                            enchStr, section.getCurrentPath());
                    continue;
                }

                try {

                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts[0]));
                    int level = Integer.parseInt(parts[1]);

                    if (enchantment == null) {
                        info("Config.yml has invalid 'enchantments' value %s under section %s. Before the '|' has to be a valid enchantment. " +
                                        "For example: power|6 is valid. Skipping. ",
                                enchStr, section.getCurrentPath());
                        continue;
                    }

                    target.put(enchantment, level); // passed all checks

                } catch (NumberFormatException garbage){
                    info("Config.yml has invalid 'enchantments' value format %s under section %s. After the '|' has to be an integer. " +
                                    "For example: power|6 is valid. Skipping. ",
                            enchStr, section.getCurrentPath());
                }
            }
        }

        return target;
    }

    private void loadTradingTasks(XmasTaskManager taskManager, FileConfiguration conf) {

        Set<String> keys = conf.getConfigurationSection(TRADE_SECTION).getKeys(false);
        keys.remove(wt);
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection(TRADE_SECTION + PATH_SEPARATOR + key);
            try {
                Material materialToReceiveInTrade = Material.valueOf(key.toUpperCase());

                int weight = defaultIfAbsent(section, wt, 1);
                if (!positive(section, wt, weight)) continue;

                taskManager.addTask(TRADE_SUBSECTION,
                        new TradingTask(weight,
                                materialToReceiveInTrade));
            } catch (IllegalArgumentException e) {
                invalidKey(section, key);
            }
        }
    }

    private void loadCraftingTasks(XmasTaskManager taskManager, FileConfiguration conf) {

        Set<String> keys = conf.getConfigurationSection(CRAFT_SECTION).getKeys(false);
        keys.remove(wt);
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection(CRAFT_SECTION + PATH_SEPARATOR + key);
            try {
                Material materialToCraft = Material.valueOf(key.toUpperCase());

                int weight = defaultIfAbsent(section, wt, 1);
                if (!positive(section, wt, weight)) continue;

                int min = defaultIfAbsent(section, "min", 1);
                if (!positive(section, "min", min)) continue;

                int max = defaultIfAbsent(section, "max", 1);
                if (!positive(section, "max", max)) continue;

                taskManager.addTask(CRAFT_SUBSECTION,
                        new CraftingTask(weight,
                                materialToCraft,
                                min,
                                max));
            } catch (IllegalArgumentException e) {
                invalidKey(section, key);
            }
        }
    }

    private void loadFishingTasks(XmasTaskManager taskManager, FileConfiguration conf) {

        Set<String> keys = conf.getConfigurationSection(FISH_SECTION).getKeys(false);
        keys.remove(wt);
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection(FISH_SECTION + PATH_SEPARATOR + key);
            try {
                Material materialToCatch = Material.valueOf(key.toUpperCase());

                int weight = defaultIfAbsent(section, wt, 1);
                if (!positive(section, wt, weight)) continue;

                int min = defaultIfAbsent(section, "min", 1);
                if (!positive(section, "min", min)) continue;

                int max = defaultIfAbsent(section, "max", 1);
                if (!positive(section, "max", max)) continue;

                taskManager.addTask(FISH_SUBSECTION,
                        new FishingTask(weight,
                                materialToCatch,
                                min,
                                max));
            } catch (IllegalArgumentException e) {
                invalidKey(section, key);
            }
        }
    }

    private void loadAcquireTasks(XmasTaskManager taskManager, FileConfiguration conf) {

        Set<String> keys = conf.getConfigurationSection(ACQUIRE_SECTION).getKeys(false);
        keys.remove(wt);
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection(ACQUIRE_SECTION + PATH_SEPARATOR + key);
            if (section.contains("enchantments")) {
                if (!section.isList("enchantments")) {
                    info("Config.yml contains incorrect 'enchantments' value under section %s. '%s' has to be a string list. Skipping. ",
                            key, section.getCurrentPath());
                    continue;
                }

                if (key.startsWith("enchanted_book")) {
                    List<String> enchantments = validateEnchantments(section, section.getStringList("enchantments"));

                    int weight = defaultIfAbsent(section, wt, 1);
                    if (!positive(section, wt, weight)) continue;

                    taskManager.addTask(ACQUIRE_SUBSECTION,
                            new EnchantedItemAcquireTask(weight,
                                    enchantments));

                } else {

                    try {
                        Material materialToAcquire = Material.valueOf(key.toUpperCase());
                        List<String> enchantments = validateEnchantments(section, section.getStringList("enchantments"), materialToAcquire);

                        int weight = defaultIfAbsent(section, wt, 1);
                        if (!positive(section, wt, weight)) continue;

                        taskManager.addTask(ACQUIRE_SUBSECTION,
                                new EnchantedItemAcquireTask(weight,
                                        materialToAcquire,
                                        enchantments));

                    } catch (IllegalArgumentException e) {
                        invalidKey(section, key);
                    }

                }
            } else {

                try {
                    Material materialToAcquire = Material.valueOf(key.toUpperCase());

                    int weight = defaultIfAbsent(section, "weight", 1);
                    if (!positive(section, "weight", weight)) continue;

                    int min = defaultIfAbsent(section, "min", 1);
                    if (!positive(section, "min", min)) continue;

                    int max = defaultIfAbsent(section, "max", 1);
                    if (!positive(section, "max", max)) continue;

                    taskManager.addTask(ACQUIRE_SUBSECTION,
                            new AcquireTask(weight,
                                    materialToAcquire,
                                    min,
                                    max));
                } catch (IllegalArgumentException e) {
                    invalidKey(section, key);
                }

            }
        }
    }

    private void loadSlayTasks(XmasTaskManager taskManager, FileConfiguration conf) {

        Set<String> keys = conf.getConfigurationSection(SLAY_SECTION).getKeys(false);
        keys.remove(wt);
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection(SLAY_SECTION + PATH_SEPARATOR + key);
            EntityType mobToKill = EntityType.fromName(key);
            if (mobToKill == null) {
                invalidKey(section, key);
                continue;
            }

            int weight = defaultIfAbsent(section, wt, 1);
            if (!positive(section, wt, weight)) continue;

            int min = defaultIfAbsent(section, "min", 1);
            if (!positive(section, "min", min)) continue;

            int max = defaultIfAbsent(section, "max", 1);
            if (!positive(section, "max", max)) continue;

            taskManager.addTask(SLAY_SUBSECTION,
                    new SlayTask(weight,
                            mobToKill,
                            min,
                            max));
        }

    }

    private List<String> validateEnchantments(ConfigurationSection section, List<String> enchantments) {
        if (enchantments.size() > 5) {
            info("Config.yml contains too many values under 'enchantments' in section %s. Cutting to size. Max is currently 5.",
                    section.getCurrentPath());
            enchantments = enchantments.subList(0, 5);
        }
        
        List<String> faultyEnchantments = new ArrayList<>();
        for (String ench : enchantments) {
            if ("random".equalsIgnoreCase(ench) || ench.contains("|") || Enchantment.getByKey(NamespacedKey.minecraft(ench)) != null)
                continue;

            info("Config.yml contains incorrect value '%s' under 'enchantments' in section %s. Enchantments can be 'random' or a specific enchantment. Skipping the value.",
                    ench, section.getCurrentPath());
            faultyEnchantments.add(ench);
        }
        enchantments.removeAll(faultyEnchantments);
        return enchantments;
    }

    private List<String> validateEnchantments(ConfigurationSection section, List<String> enchantments, Material materialToAcquire) {
        if (enchantments.size() > 5) {
            info("Config.yml contains too many values under 'enchantments' in section %s. Cutting to size. Max is currently 5.",
                    section.getCurrentPath());
            enchantments = enchantments.subList(0, 5);
        }

        List<String> faultyEnchantments = new ArrayList<>();
        for (String ench : enchantments) {
            if ("random".equalsIgnoreCase(ench) || ench.contains("|"))
                continue;

            Enchantment e =  Enchantment.getByKey(NamespacedKey.minecraft(ench));
            if (e != null && e.canEnchantItem(new ItemStack(materialToAcquire)))
                continue;

            info("Config.yml contains incorrect value '%s' under 'enchantments' in section %s. Enchantments can be 'random' or a specific enchantment. Skipping the value.",
                    ench, section.getCurrentPath());
            faultyEnchantments.add(ench);
        }
        enchantments.removeAll(faultyEnchantments);

        return enchantments;
    }

    private boolean positive(ConfigurationSection section, String key, int value) {
        if (value < 1) {
            info("Config.yml contains incorrect '%s' value '%s' under section %s. '%s' has to have a positive value. ",
                    key, String.valueOf(value), section.getCurrentPath(), key);
            return false;
        }

        return true;
    }

    private int defaultIfAbsent(ConfigurationSection section, String key, int defaultValue) {
        if (!section.contains(key)) {
            info("Config.yml is missing key '%s' under section %s. Defaulting to %s.", key, section.getCurrentPath(), String.valueOf(defaultValue));
            return defaultValue;
        }

        return section.getInt(key);
    }

    public int get(String key, int defaultValue) {
        if (!conf.contains(key) || !conf.isInt(key)){
            info("Config.yml is missing key '%s'.", key);
            return defaultValue;
        }

        return conf.getInt(key);
    }
}
