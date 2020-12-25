package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.XmasTasksPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReadWriteUtils {
    private static Plugin plugin = XmasTasksPlugin.getPlugin(XmasTasksPlugin.class);

    public static Optional<Material> loadMaterial(Player p, ConfigurationSection section) {
        Optional<Material> material = Optional.empty();

        if (!section.contains("material")) {
            logMissingKey(p, section, "material");
            return material;
        }

        try {
            material = Optional.ofNullable(Material.matchMaterial(section.getString("material")));
        } catch (IllegalArgumentException garbage) {
            logInvalidValue(p, section, "material", "a valid material");
            return material;
        }

        return material;
    }

    public static Optional<EntityType> loadMob(Player p, ConfigurationSection section) {
        Optional<EntityType> entityType = Optional.empty();

        if (!section.contains("mob")) {
            logMissingKey(p, section, "mob");
            return entityType;
        }

        try {
            entityType = Optional.of(EntityType.valueOf(section.getString("mob")));
        } catch (IllegalArgumentException garbage) {
            logInvalidValue(p, section, "mob");
            return entityType;
        }

        return entityType;
    }

    public static Optional<Integer> loadAmount(Player p, ConfigurationSection section) {
        Optional<Integer> amount = Optional.empty();

        if (!section.contains("amount")) {
            logMissingKey(p, section, "amount");
            return amount;
        }

        if (!section.isInt("amount")) {
            logInvalidValue(p, section, "amount");
            return amount;
        }

        return Optional.of(section.getInt("amount"));
    }

    public static Optional<Map<Enchantment, Integer>> loadEnchantments(Player p, ConfigurationSection section) {
        Optional<Map<Enchantment, Integer>> enchantments = Optional.empty();

        if (!section.contains("enchantments")) {
            logMissingKey(p, section, "enchantments");
            return enchantments;
        }

        if (!section.isList("enchantments")) {
            logInvalidValue(p, section, "enchantments", "a list");
            return enchantments;
        }

        List<String> enchantmentStrs = section.getStringList("enchantments");
        enchantments = Optional.of(new LinkedHashMap<>());
        for (String str : enchantmentStrs) {

            if (!str.contains("|"))  // format example: power|6
                continue;

            String[] parts = str.split("\\|");
            if (parts.length > 2) {
                logInvalidValue(p, section, "enchantments", "a string list in format '<enchantment>|<level>' for example: power|5.");
                continue;
            }

            try {

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts[0]));
                int level = Integer.parseInt(parts[1]);

                if (enchantment == null) {
                    logInvalidValue(p, section, "enchantments", "a string list in format '<enchantment>|<level>' for example: power|5.");
                    continue;
                }

                enchantments.get().put(enchantment, level); // passed all checks

            } catch (NumberFormatException garbage){
                logInvalidValue(p, section, "enchantments", "a string list in format '<enchantment>|<level>' for example: power|5.");
            }

        }


        return enchantments;
    }

    private static void logInvalidValue(Player p, ConfigurationSection taskSection, String key) {
        logInvalidValue(p, taskSection, key, "an integer");
    }

    private static void logInvalidValue(Player p, ConfigurationSection taskSection, String key, String valueShouldBe) {
        plugin.getLogger().warning(String.format("Player %s (uuid:%s) data file section %s %s value should be %s.",
                p.getName(), p.getUniqueId(), taskSection.getCurrentPath(), key, valueShouldBe));
    }

    private static void logMissingKey(Player p, ConfigurationSection taskSection, String key) {
        plugin.getLogger().warning(String.format("Player %s (uuid:%s) data file section %s is missing key %s.",
                p.getName(), p.getUniqueId(), taskSection.getCurrentPath(), key));
    }
}
