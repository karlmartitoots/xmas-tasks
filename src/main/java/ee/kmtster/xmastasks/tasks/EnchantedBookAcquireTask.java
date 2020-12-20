package ee.kmtster.xmastasks.tasks;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class EnchantedBookAcquireTask extends AcquireTask{
    private final Enchantment[] enchantments;

    public EnchantedBookAcquireTask(int weight, int min, int max, Enchantment[] enchantments) {
        super(weight, Material.ENCHANTED_BOOK, min, max);
        this.enchantments = enchantments;
    }

    public Enchantment[] getEnchantments() {
        return enchantments;
    }
}
