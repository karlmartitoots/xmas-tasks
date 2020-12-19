package ee.kmtster.xmastasks;

import ee.kmtster.xmastasks.tasks.TaskInstance;
import ee.kmtster.xmastasks.tasks.XmasTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class XmasTaskManager {
    private final Map<UUID, TaskInstance> playersTasks = new ConcurrentHashMap<>();
    private final List<String> defaultCategories = Arrays.asList("mine", "craft", "slay", "acquire", "fish", "trade");
    private final RandomCollection<String> categories = new RandomCollection<>();
    private final Map<String, RandomCollection<XmasTask>> tasks = new LinkedHashMap<>();
    private final Random random = new Random();

    // Randomly generate a new task
    public TaskInstance createTask(Player player) {
        if (hasTask(player))
            return readTask(player);

        String category = categories.next();
        XmasTask task = tasks.get(category).next();
        TaskInstance taskInstance = task.generate(random);

        playersTasks.put(player.getUniqueId(), task.generate(random));
        return taskInstance;
    }

    public TaskInstance readTask(Player player) {
        Bukkit.getLogger().info("read PlayersTasks: ");
        for (UUID p : playersTasks.keySet()) {
            Bukkit.getLogger().info(String.format("%s: %s", p, playersTasks.get(p) ));
        }
        return playersTasks.get(player.getUniqueId());
    }

    public boolean hasTask(Player player) {
        return playersTasks.containsKey(player.getUniqueId());
    }

    public TaskInstance deleteTask(Player player) {
        return playersTasks.remove(player.getUniqueId());
    }

    void addTaskCategory(String cat, int weight) {
        categories.add(weight, cat);
    }

    void addTask(String category, XmasTask task) {
        if (!tasks.containsKey(category))
            tasks.put(category, new RandomCollection<>());

        tasks.get(category).add(task.getWeight(), task);
    }

    void addTasks(String category, List<XmasTask> added) {
        if (!tasks.containsKey(category))
            tasks.put(category, new RandomCollection<>());

        for (XmasTask task : added) {
            tasks.get(category).add(task.getWeight(), task);
        }
    }

    public List<String> getDefaultCategories() {
        return defaultCategories;
    }
}
