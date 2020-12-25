package ee.kmtster.xmastasks;

import ee.kmtster.xmastasks.tasks.XmasTaskManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class DefaultRewards {
    private Random random = new Random();

    public DefaultRewards(Plugin plugin, XmasTaskManager taskManager) {
        taskManager.addReward(this::falsePresents, 10);
        taskManager.addReward(this::cake, 10);
        taskManager.addReward(this::sponges, 10);
        taskManager.addReward(this::twopresents, 10);

        taskManager.addReward(this::potions15min, 10);
        taskManager.addReward(this::potions30min, 5);

        taskManager.addReward(this::shulker_boxes, 5);
        taskManager.addReward(this::parrotEgg, 5);
        taskManager.addReward(this::pandaEgg, 5);
        taskManager.addReward(this::diamond_ores, 5);
        taskManager.addReward(this::nether_gold_ore, 5);
        taskManager.addReward(this::emerald_ores, 5);
        taskManager.addReward(this::upTo4presents, 5);

        taskManager.addReward(this::dragonEgg, 2);
        taskManager.addReward(this::netherite_pickaxe, 2);
        taskManager.addReward(this::netherite_shovel, 2);
        taskManager.addReward(this::netherite_hoe, 2);
        taskManager.addReward(this::netherite_axe, 2);
        taskManager.addReward(this::netherite_sword, 2);
        taskManager.addReward(this::netherite_ingots, 2);
        taskManager.addReward(this::netherite_scraps, 2);


        taskManager.addReward(this::nether_star, 1);
        taskManager.addReward(this::santas_hoe, 1);
        taskManager.addReward(this::santas_shovel, 1);
        taskManager.addReward(this::enchanted_golden_apple, 1);
        taskManager.addReward(this::upTo8presents, 1);
    }

    private ItemStack falsePresents() {
        String url = "http://textures.minecraft.net/texture/2ebcd2159856d795c8915e8f59a8434c8e935a45a43fa71f0809789be75e3de2";
        ItemStack present = SkullCreator.itemFromUrl(url);

        SkullMeta presentMeta = (SkullMeta) present.getItemMeta();
        presentMeta.setDisplayName(ChatColor.RED + "Decorative Christmas Present");

        present.setItemMeta(presentMeta);

        present.setAmount(random.nextInt(8) + 1);

        return present;
    }

    private ItemStack twopresents() {
        ItemStack present = XmasTaskManager.present();
        present.setAmount(2);

        return present;
    }

    private ItemStack upTo4presents() {
        ItemStack present = XmasTaskManager.present();
        present.setAmount(random.nextInt(3) + 2);

        return present;
    }

    private ItemStack upTo8presents() {
        ItemStack present = XmasTaskManager.present();
        present.setAmount(random.nextInt(6) + 3);

        return present;
    }

    private ItemStack parrotEgg() {
        return new ItemStack(Material.PARROT_SPAWN_EGG, 1);
    }

    private ItemStack pandaEgg() {
        return new ItemStack(Material.PANDA_SPAWN_EGG, 1);
    }

    private ItemStack dragonEgg() {
        return new ItemStack(Material.DRAGON_EGG, 1);
    }

    private ItemStack cake() {
        return new ItemStack(Material.CAKE, random.nextInt(16) + 8);
    }

    private ItemStack shulker_boxes() {
        return new ItemStack(Material.SHULKER_BOX, random.nextInt(3) + 1);
    }

    private ItemStack nether_star() {
        return new ItemStack(Material.NETHER_STAR, 1);
    }

    private ItemStack sponges() {
        return new ItemStack(Material.SPONGE, random.nextInt(8) + 8);
    }

    private ItemStack netherite_pickaxe() {
        return new ItemStack(Material.NETHERITE_PICKAXE, 1);
    }

    private ItemStack netherite_shovel() {
        return new ItemStack(Material.NETHERITE_SHOVEL, 1);
    }

    private ItemStack santas_shovel() {
        ItemStack reward = new ItemStack(Material.NETHERITE_SHOVEL, 1);
        reward.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 10);

        ItemMeta rewardMeta = reward.getItemMeta();
        rewardMeta.setDisplayName(String.format("Santa's shovel", ChatColor.YELLOW));

        reward.setItemMeta(rewardMeta);

        return reward;
    }

    private ItemStack santas_hoe() {
        ItemStack reward = new ItemStack(Material.DIAMOND_HOE, 1);
        reward.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 10);

        ItemMeta rewardMeta = reward.getItemMeta();
        rewardMeta.setDisplayName(String.format("Santa's hoe", ChatColor.YELLOW));

        reward.setItemMeta(rewardMeta);

        return reward;
    }

    private ItemStack netherite_hoe() {
        return new ItemStack(Material.NETHERITE_HOE, 1);
    }

    private ItemStack netherite_axe() {
        return new ItemStack(Material.NETHERITE_AXE, 1);
    }

    private ItemStack netherite_sword() {
        return new ItemStack(Material.NETHERITE_SWORD, 1);
    }

    private ItemStack netherite_ingots() {
        return new ItemStack(Material.NETHERITE_INGOT, random.nextInt(3) + 1);
    }

    private ItemStack netherite_scraps() {
        return new ItemStack(Material.NETHERITE_SCRAP, random.nextInt(8) + 4);
    }

    private ItemStack diamond_ores() {
        return new ItemStack(Material.DIAMOND_ORE, random.nextInt(8) + 4);
    }

    private ItemStack emerald_ores() {
        return new ItemStack(Material.DIAMOND_ORE, random.nextInt(8) + 4);
    }

    private ItemStack nether_gold_ore() {
        return new ItemStack(Material.NETHER_GOLD_ORE, random.nextInt(16) + 4);
    }

    private ItemStack enchanted_golden_apple() {
        return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
    }

    private ItemStack potions30min() {
        ItemStack reward = new ItemStack(Material.SPLASH_POTION, 1);

        PotionMeta rewardMeta = (PotionMeta) reward.getItemMeta();
        rewardMeta.setDisplayName(String.format("Santa's random cocktail I", ChatColor.RED));

        PotionEffectType[] allEffects = PotionEffectType.values();
        PotionEffect effect = new PotionEffect(allEffects[random.nextInt(allEffects.length)], 20 * 60 * 30, 1);
        rewardMeta.addCustomEffect(effect, true);

        reward.setItemMeta(rewardMeta);
        reward.setAmount(random.nextInt(3) + 1);

        return reward;
    }

    private ItemStack potions15min() {
        ItemStack reward = new ItemStack(Material.SPLASH_POTION, 1);

        PotionMeta rewardMeta = (PotionMeta) reward.getItemMeta();
        rewardMeta.setDisplayName(String.format("Santa's random cocktail II", ChatColor.RED));

        PotionEffectType[] allEffects = PotionEffectType.values();
        PotionEffect effect = new PotionEffect(allEffects[random.nextInt(allEffects.length)], 20 * 60 * 15, 1);
        rewardMeta.addCustomEffect(effect, true);

        reward.setItemMeta(rewardMeta);
        reward.setAmount(random.nextInt(3) + 1);

        return reward;
    }
}
