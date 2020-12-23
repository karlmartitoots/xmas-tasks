package ee.kmtster.xmastasks.tasks;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

public class EnchantedItemAcquireTaskInstance extends AcquireTaskInstance implements TaskInstance {
    private final AcquireTask task;
    private final Map<Enchantment, Integer> enchantments;
    private boolean finished;

    public EnchantedItemAcquireTaskInstance(AcquireTask task, Map<Enchantment, Integer> enchantments) {
        super(task, 1);
        this.task = task;
        this.enchantments = enchantments;
        this.finished = false;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public String progress() {
        return String.format("%sYour task to obtain a %s%s %swith enchantments %s%s %sis %sfinished.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getItemToAcquire().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW,
                ChatColor.GREEN,
                enchantments.keySet().stream()
                        .map(this::enchantmentToString)
                        .reduce((s1, s2) -> s1 + ", " + s2),
                ChatColor.YELLOW,
                isFinished() ? "" : "not ");
    }

    @Override
    public String display() {
        return String.format("%sYour task is to obtain a/an %s%s %swith enchantments %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                task.getItemToAcquire().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW,
                ChatColor.GREEN,
                enchantments.keySet().stream()
                        .map(this::enchantmentToString)
                        .reduce((s1, s2) -> s1 + ", " + s2));
    }

    private String enchantmentToString(Enchantment e) {
        return e.getMaxLevel() == 1 ? e.getName() : e.getName() + " " + roman(enchantments.get(e));
    }

    private String roman(int number) {
        switch (number){
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return "?";
        }
    }
}
