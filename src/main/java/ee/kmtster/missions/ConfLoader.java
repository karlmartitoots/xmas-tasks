package ee.kmtster.missions;

import ee.kmtster.missions.missions.*;
import ee.kmtster.missions.rewards.Rewards;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Logger;

public class ConfLoader {

    private final Logger logger;
    private final FileConfiguration conf;
    private final Rewards rewards;

    private char PATH_SEPARATOR = '.';
    private final String wt = "weight";
    private final String MISSIONS_SECTION = "missions";
    private final String REWARDS_SECTION = "rewards";

    private final String OBTAIN_SUBSECTION = "obtain";
    private final String CRAFT_SUBSECTION = "craft";
    private final String FISH_SUBSECTION = "fish";
    private final String SLAY_SUBSECTION = "slay";
    private final String TRADE_SUBSECTION = "trade";

    private final String ACQUIRE_SECTION = MISSIONS_SECTION + PATH_SEPARATOR + OBTAIN_SUBSECTION;
    private final String CRAFT_SECTION = MISSIONS_SECTION + PATH_SEPARATOR + CRAFT_SUBSECTION;
    private final String FISH_SECTION = MISSIONS_SECTION + PATH_SEPARATOR + FISH_SUBSECTION;
    private final String SLAY_SECTION = MISSIONS_SECTION + PATH_SEPARATOR + SLAY_SUBSECTION;
    private final String TRADE_SECTION = MISSIONS_SECTION + PATH_SEPARATOR + TRADE_SUBSECTION;

    private final List<String> categories = Arrays.asList("obtain","craft","fish","slay","trade");

    public ConfLoader(Plugin plugin, MissionManager missionManager, FileConfiguration conf, Rewards rewards) {
        this.logger = plugin.getLogger();
        this.conf = conf;
        this.rewards = rewards;

        this.PATH_SEPARATOR = conf.options().pathSeparator();

        loadMissions(missionManager, conf);
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

    private void loadMissions(MissionManager missionManager, FileConfiguration conf) {
        if (!conf.contains(MISSIONS_SECTION)){
            info("Config.yml is missing key 'missions'.");
            return;
        }

        if (!conf.contains(REWARDS_SECTION)){
            info("Config.yml is missing key 'missions'.");
            return;
        }


        for (String category : this.categories) {
            if (!conf.getConfigurationSection(MISSIONS_SECTION).contains(category))
                continue;

            ConfigurationSection section = conf.getConfigurationSection(MISSIONS_SECTION + PATH_SEPARATOR + category);
            if (section.getKeys(false).size() < 2) // has to have more keys than just the 'weight' key
                continue;

            int weight = defaultIfAbsent(section, wt, 1);
            if (!positive(section, wt, weight)) continue;

            missionManager.addMissionCategory(category, weight);

            switch (category) {
                case CRAFT_SUBSECTION:
                    loadCraftingMissions(missionManager, conf);
                    continue;
                case SLAY_SUBSECTION:
                    loadSlayMissions(missionManager, conf);
                    continue;
                case OBTAIN_SUBSECTION:
                    loadObtainMissions(missionManager, conf);
                    continue;
                case FISH_SUBSECTION:
                    loadFishingMissions(missionManager, conf);
                    continue;
                case TRADE_SUBSECTION:
                    loadVillagerTradingMissions(missionManager, conf);
            }
        }

        loadRewards(missionManager, conf);
    }

    private void loadRewards(MissionManager missionManager, FileConfiguration conf) {

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

                rewards.add(()->{
                    ItemStack reward = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) reward.getItemMeta();
                    for (Enchantment ench : enchantments.keySet()) {
                        meta.addStoredEnchant( ench, enchantments.get(ench), true );
                    }
                    reward.setItemMeta( meta );

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

    private void loadVillagerTradingMissions(MissionManager missionManager, FileConfiguration conf) {

        Set<String> keys = conf.getConfigurationSection(TRADE_SECTION).getKeys(false);
        keys.remove(wt);
        for (String key : keys) {

            ConfigurationSection section = conf.getConfigurationSection(TRADE_SECTION + PATH_SEPARATOR + key);
            try {
                Material materialToReceiveInTrade = Material.valueOf(key.toUpperCase());

                int weight = defaultIfAbsent(section, wt, 1);
                if (!positive(section, wt, weight)) continue;

                missionManager.addMission(TRADE_SUBSECTION,
                        new VillagerTradingMission(weight,
                                materialToReceiveInTrade));
            } catch (IllegalArgumentException e) {
                invalidKey(section, key);
            }
        }
    }

    private void loadCraftingMissions(MissionManager missionManager, FileConfiguration conf) {

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

                missionManager.addMission(CRAFT_SUBSECTION,
                        new CraftingMission(weight,
                                materialToCraft,
                                min,
                                max));
            } catch (IllegalArgumentException e) {
                invalidKey(section, key);
            }
        }
    }

    private void loadFishingMissions(MissionManager missionManager, FileConfiguration conf) {

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

                missionManager.addMission(FISH_SUBSECTION,
                        new FishingMission(weight,
                                materialToCatch,
                                min,
                                max));
            } catch (IllegalArgumentException e) {
                invalidKey(section, key);
            }
        }
    }

    private void loadObtainMissions(MissionManager missionManger, FileConfiguration conf) {

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

                    missionManger.addMission(OBTAIN_SUBSECTION,
                            new EnchantedItemObtainMission(weight,
                                    enchantments));

                } else {

                    try {
                        Material materialToObtain = Material.valueOf(key.toUpperCase());
                        List<String> enchantments = validateEnchantments(section, section.getStringList("enchantments"), materialToObtain);

                        int weight = defaultIfAbsent(section, wt, 1);
                        if (!positive(section, wt, weight)) continue;

                        missionManger.addMission(OBTAIN_SUBSECTION,
                                new EnchantedItemObtainMission(weight,
                                        materialToObtain,
                                        enchantments));

                    } catch (IllegalArgumentException e) {
                        invalidKey(section, key);
                    }

                }
            } else {

                try {
                    Material materialToObtain = Material.valueOf(key.toUpperCase());

                    int weight = defaultIfAbsent(section, "weight", 1);
                    if (!positive(section, "weight", weight)) continue;

                    int min = defaultIfAbsent(section, "min", 1);
                    if (!positive(section, "min", min)) continue;

                    int max = defaultIfAbsent(section, "max", 1);
                    if (!positive(section, "max", max)) continue;

                    missionManger.addMission(OBTAIN_SUBSECTION,
                            new ObtainMission(weight,
                                    materialToObtain,
                                    min,
                                    max));
                } catch (IllegalArgumentException e) {
                    invalidKey(section, key);
                }

            }
        }
    }

    private void loadSlayMissions(MissionManager missionManager, FileConfiguration conf) {

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

            missionManager.addMission(SLAY_SUBSECTION,
                    new SlayMission(weight,
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

    private List<String> validateEnchantments(ConfigurationSection section, List<String> enchantments, Material materialToObtain) {
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
            if (e != null && e.canEnchantItem(new ItemStack(materialToObtain)))
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
