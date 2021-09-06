package ee.kmtster.missions.missions;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class EnchantedItemObtainMission extends ObtainMission {
    private final static List<Enchantment> allEnchantments = Arrays.asList(Enchantment.values());

    private final List<String> enchantments;

    public EnchantedItemObtainMission(int weight, Material enchantedItemToObtain, List<String> enchantments) {
        super(weight, enchantedItemToObtain);
        this.enchantments = enchantments;
    }

    public EnchantedItemObtainMission(int weight, List<String> enchantments) {
        super(weight, Material.ENCHANTED_BOOK);
        this.enchantments = enchantments;
    }

    public List<String> getEnchantments() {
        return enchantments;
    }

    @Override
    public MissionInstance<ObtainMission> generate(Random random) {
        List<Enchantment> choices = new ArrayList<>(allEnchantments);
        if (getItemToObtain() != Material.ENCHANTED_BOOK) choices.removeIf(choice -> !choice.getItemTarget().includes(getItemToObtain()));

        Map<Enchantment, Integer> enchantmentMap = new LinkedHashMap<>();
        for (String enchantment : enchantments) {
            if ("random".equalsIgnoreCase(enchantment)) {
                Enchantment e = choices.remove(random.nextInt(choices.size()));
                enchantmentMap.put(e, 1 + random.nextInt(e.getMaxLevel()));
            }
        }

        return new EnchantedItemObtainMissionInstance(this, enchantmentMap);
    }
}
