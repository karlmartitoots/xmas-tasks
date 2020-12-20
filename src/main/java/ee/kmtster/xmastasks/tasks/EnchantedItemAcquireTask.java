package ee.kmtster.xmastasks.tasks;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class EnchantedItemAcquireTask extends AcquireTask {
    private final Enchantment[] enchantments;

    public EnchantedItemAcquireTask(int weight, Material itemToAcquire, int min, int max, Enchantment[] enchantments) {
        super(weight, itemToAcquire, min, max);
        this.enchantments = enchantments;
    }

    public Enchantment[] getEnchantments() {
        return enchantments;
    }
}
