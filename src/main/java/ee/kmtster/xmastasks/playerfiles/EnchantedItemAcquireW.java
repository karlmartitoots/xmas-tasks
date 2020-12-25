package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.EnchantedItemAcquireTaskInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantedItemAcquireW implements TaskWriter<EnchantedItemAcquireTaskInstance> {

    @Override
    public void write(ConfigurationSection taskSection, EnchantedItemAcquireTaskInstance taskInstance) {
        taskSection.set("type", "acquire");
        taskSection.set("material", taskInstance.getTask().getItemToAcquire().name().toLowerCase());

        Map<Enchantment, Integer> enchantments = taskInstance.getEnchantments();
        List<String> enchStrs = new ArrayList<>();
        for (Enchantment enchantment : enchantments.keySet()) {
            enchStrs.add(String.format("%s|%s", enchantment.getKey().getKey(), enchantments.get(enchantment)));
        }
        taskSection.set("enchantments", enchStrs);
    }
}
