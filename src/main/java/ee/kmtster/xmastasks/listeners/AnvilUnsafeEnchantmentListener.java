package ee.kmtster.xmastasks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

/**
 * Adds support for adding 'unsafe' level enchanted books to items.
 * For example Unbreaking IV is an 'unsafe' enchantment.
 */
public class AnvilUnsafeEnchantmentListener implements Listener {

    private Plugin plugin;

    public AnvilUnsafeEnchantmentListener(Plugin plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (e.getView().getType() != InventoryType.ANVIL)
            return;

        if (!(e.getWhoClicked() instanceof Player)) {
            e.getWhoClicked().sendMessage("Only available to players.");
            return;
        }

        AnvilInventory inventory = (AnvilInventory) e.getInventory();
        ItemStack input0 = inventory.getItem(0);
        ItemStack input1 = inventory.getItem(1);

        boolean isInput1Book = input1 != null && input1.getType() == Material.ENCHANTED_BOOK;

        if (e.getSlotType() == InventoryType.SlotType.RESULT
                && input0 != null
                && isInput1Book && input1.hasItemMeta()) { // changes the item when output is clicked

            ItemStack result = createResult(input0, input1);
            if (!result.equals(input0)) {
                e.setCursor(result);
                inventory.setItem(0, null);
                inventory.setItem(1, null);
            }

        } else {
            scheduleChangeOutput(inventory); // checks any other clicks and changes output display
        }
    }

    /**
     * Creates the resulting item.
     * Adds enchantments from the enchanted book to the item.
     */
    private ItemStack createResult(ItemStack input, ItemStack enchantedBook) {
        ItemStack result = new ItemStack(input);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

        if (bookMeta == null)
            return result;

        if (input.getType() == Material.ENCHANTED_BOOK) {
            combineBooksEnchants(result, bookMeta.getStoredEnchants());
            combineBooksEnchants(result, bookMeta.getEnchants());
        } else {
            addUnsafeEnchants(result, bookMeta.getStoredEnchants());
            addUnsafeEnchants(result, bookMeta.getEnchants());
        }

        return result;
    }

    private void scheduleChangeOutput(AnvilInventory inventory) {
        new BukkitRunnable() {
            @Override
            public void run() {
                changeOutput(inventory);
            }
        }.runTaskLater(plugin, 1);
    }

    private void addUnsafeEnchants(ItemStack result, Map<Enchantment, Integer> enchants) {
        for (Enchantment ench : enchants.keySet()) {
            if (ench.canEnchantItem(result)
                    && !enchantmentsConflict(result, ench))
                result.addUnsafeEnchantment(ench, enchants.get(ench));
        }
    }

    private boolean enchantmentsConflict(ItemStack result, Enchantment ench) {
        return result.getEnchantments().keySet().stream()
                .anyMatch(existingEnch -> existingEnch.conflictsWith(ench));
    }

    private void combineBooksEnchants(ItemStack result, Map<Enchantment, Integer> enchants) {
        for (Enchantment ench : enchants.keySet()) {
            result.addUnsafeEnchantment(ench, enchants.get(ench));
        }
    }

    public void changeOutput(AnvilInventory inventory) {
        if (inventory == null || inventory.getViewers().isEmpty())
            return;

        ItemStack input0 = inventory.getItem(0);
        ItemStack input1 = inventory.getItem(1);

        if (input0 == null || input1 == null)
            return;

        if (input1.getType() != Material.ENCHANTED_BOOK)
            return;

        ItemStack result = createResult(input0, input1);
        // Set output if the input changed
        inventory.setItem(2, result.equals(input0) ? null : result);
    }
}
