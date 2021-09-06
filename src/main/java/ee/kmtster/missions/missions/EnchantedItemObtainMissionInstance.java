package ee.kmtster.missions.missions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;
import java.util.stream.Collectors;

public class EnchantedItemObtainMissionInstance extends ObtainMissionInstance {
    private final ObtainMission mission;
    private final Map<Enchantment, Integer> enchantments;

    public EnchantedItemObtainMissionInstance(ObtainMission mission, Map<Enchantment, Integer> enchantments) {
        super(mission, 1);
        this.mission = mission;
        this.enchantments = enchantments;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public void check(Map<Integer, ItemStack> itemsBySlot) {
        if (getMission().getItemToObtain() == Material.ENCHANTED_BOOK) {

            for (Integer slot : itemsBySlot.keySet()) {

                ItemStack item = itemsBySlot.get(slot);
                if (!item.hasItemMeta()) continue;

                EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) item.getItemMeta();

                Map<Enchantment, Integer> requireds = getEnchantments();
                if (requireds.keySet().stream().allMatch(req -> bookMeta.getEnchantLevel(req) == requireds.get(req) || bookMeta.getStoredEnchantLevel(req) == requireds.get(req))) {
                    finish();
                    return;
                }

            }

        } else {

            for (Integer slot : itemsBySlot.keySet()) {

                ItemStack enchantedItem = itemsBySlot.get(slot);
                if (allRequiredEnchantmentsMatch(enchantedItem, getEnchantments())) {
                    finish();
                    return;
                }

            }

        }
    }

    private boolean allRequiredEnchantmentsMatch(ItemStack item, Map<Enchantment, Integer> requireds) {
        return requireds.keySet().stream().allMatch(req -> item.getEnchantmentLevel(req) == requireds.get(req));
    }

    @Override
    public String progress() {
        return String.format("%sYour mission to obtain %s%s %swith enchantment%s %s%s %sis %sfinished.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                mission.getItemToObtain().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW,
                ChatColor.GREEN,
                enchantments.size() == 1 ? "" : "s",
                enchantments.keySet().stream()
                        .map(this::enchantmentToString)
                        .collect(Collectors.joining(",")),
                ChatColor.YELLOW,
                isFinished() ? "" : "not ");
    }

    @Override
    public String display() {
        return String.format("%sYour mission is to obtain %s%s %swith enchantment%s %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                mission.getItemToObtain().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW,
                ChatColor.GREEN,
                enchantments.size() == 1 ? "" : "s",
                enchantments.keySet().stream()
                        .map(this::enchantmentToString)
                        .collect(Collectors.joining(","))
        );
    }

    private String enchantmentToString(Enchantment e) {
        return e.getMaxLevel() == 1 ?
                e.getKey().getKey().toLowerCase().replace("_", " ") :
                e.getKey().getKey().toLowerCase().replace("_", " ") + " " + roman(enchantments.get(e));
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
