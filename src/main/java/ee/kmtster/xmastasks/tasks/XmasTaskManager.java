package ee.kmtster.xmastasks.tasks;

import ee.kmtster.xmastasks.RandomCollection;
import ee.kmtster.xmastasks.SkullCreator;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import ee.kmtster.xmastasks.tasks.XmasTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class XmasTaskManager {
    private final Map<UUID, TaskInstance> playersTasks = new ConcurrentHashMap<>();
    private final RandomCollection<String> taskCategories = new RandomCollection<>();
    private final Map<String, RandomCollection<XmasTask>> tasks = new LinkedHashMap<>();
    private final RandomCollection<Supplier<ItemStack>> rewards = new RandomCollection<>();
    private final Random random = new Random();

    // Randomly generate a new task
    public TaskInstance createTask(Player player) {
        if (hasTask(player))
            return readTask(player);

        String category = taskCategories.next();
        XmasTask task = tasks.get(category).next();
        TaskInstance taskInstance = task.generate(random);

        playersTasks.put(player.getUniqueId(), task.generate(random));
        return taskInstance;
    }

    public TaskInstance readTask(Player player) {
        return playersTasks.get(player.getUniqueId());
    }

    public boolean hasTask(Player player) {
        return playersTasks.containsKey(player.getUniqueId());
    }

    public TaskInstance deleteTask(Player player) {
        return playersTasks.remove(player.getUniqueId());
    }

    public void putTask(Player player, TaskInstance t) {
        playersTasks.put(player.getUniqueId(), t);
    }

    public void addTaskCategory(String cat, int weight) {
        taskCategories.add(weight, cat);
    }

    public void addTask(String category, XmasTask task) {
        if (!tasks.containsKey(category))
            tasks.put(category, new RandomCollection<>());

        tasks.get(category).add(task.getWeight(), task);
    }

    public void addTasks(String category, List<XmasTask> added) {
        if (!tasks.containsKey(category))
            tasks.put(category, new RandomCollection<>());

        for (XmasTask task : added) {
            tasks.get(category).add(task.getWeight(), task);
        }
    }

    public static ItemStack present() {
        String url = "http://textures.minecraft.net/texture/2ebcd2159856d795c8915e8f59a8434c8e935a45a43fa71f0809789be75e3de2";
        ItemStack present = SkullCreator.itemFromUrl(url);

        SkullMeta presentMeta = (SkullMeta) present.getItemMeta();
        presentMeta.setDisplayName(ChatColor.RED + "Christmas Present");
        presentMeta.setLore(Collections.singletonList(String.format("%s%sFrom Santa. Right-click to open", ChatColor.GREEN, ChatColor.ITALIC)));

        present.setItemMeta(presentMeta);

        return present;
    }

    public void addReward(Supplier<ItemStack> rewardSupplier, int weight) {
        rewards.add(weight, rewardSupplier);
    }

    public ItemStack nextReward() {
        return rewards.next().get();
    }
}
