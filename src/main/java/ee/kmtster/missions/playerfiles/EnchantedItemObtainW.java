package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.EnchantedItemObtainMissionInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantedItemObtainW implements MissionWriter<EnchantedItemObtainMissionInstance> {

    @Override
    public void write(ConfigurationSection missionSection, EnchantedItemObtainMissionInstance missionInstance) {
        missionSection.set("type", "obtain");
        missionSection.set("material", missionInstance.getMission().getItemToObtain().name().toLowerCase());

        Map<Enchantment, Integer> enchantments = missionInstance.getEnchantments();
        List<String> enchStrs = new ArrayList<>();
        for (Enchantment enchantment : enchantments.keySet()) {
            enchStrs.add(String.format("%s|%s", enchantment.getKey().getKey(), enchantments.get(enchantment)));
        }
        missionSection.set("enchantments", enchStrs);
    }
}
