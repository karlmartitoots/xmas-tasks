package ee.kmtster.xmastasks.tasks;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class EnchantedItemAcquireTask extends AcquireTask {
    private final static List<Enchantment> allEnchantments = Arrays.asList(Enchantment.values());

    private final List<String> enchantments;

    public EnchantedItemAcquireTask(int weight, Material enchantedItemToAcquire, List<String> enchantments) {
        super(weight, enchantedItemToAcquire);
        this.enchantments = enchantments;
    }
    public EnchantedItemAcquireTask(int weight, List<String> enchantments) {
        super(weight, Material.ENCHANTED_BOOK);
        this.enchantments = enchantments;
    }

    public List<String> getEnchantments() {
        return enchantments;
    }

    @Override
    public TaskInstance generate(Random random) {
        List<Enchantment> choices = new ArrayList<>(allEnchantments);
        choices.removeIf(choice -> !choice.getItemTarget().includes(getItemToAcquire()));

        Map<Enchantment, Integer> enchList = new LinkedHashMap<>();
        for (String enchantment : enchantments) {
            if ("random".equalsIgnoreCase(enchantment)) {
                Enchantment e = choices.remove(random.nextInt(choices.size()));
                enchList.put(e, random.nextInt(1 + e.getMaxLevel()));
            }
        }

        return new EnchantedItemAcquireTaskInstance(this, enchList);
    }
}
