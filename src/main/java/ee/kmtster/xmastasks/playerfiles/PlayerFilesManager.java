package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.XmasTaskManager;
import ee.kmtster.xmastasks.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerFilesManager {
    private final static Map<UUID, File> playerFiles = new ConcurrentHashMap<>();

    private final Plugin plugin;
    private final XmasTaskManager taskManager;
    private final Map<String, TaskReader> readers = new ConcurrentHashMap<>();
    private final Map<Class<? extends TaskInstance>, TaskWriter> writers = new ConcurrentHashMap<>();

    public PlayerFilesManager(Plugin plugin, XmasTaskManager taskManager) {
        this.plugin = plugin;
        this.taskManager = taskManager;

        this.setupRW();
        File userdata = new File(plugin.getDataFolder(), File.separator + "PlayerDatabase");
        if (!userdata.exists())
            userdata.mkdir();

    }

    private void setupRW() {
        AcquireTaskRW acquire = new AcquireTaskRW();
        CraftingTaskRW craft = new CraftingTaskRW();
        FishingTaskRW fish = new FishingTaskRW();
        SlayTaskRW slay = new SlayTaskRW();
        TradingTaskRW trade = new TradingTaskRW();

        readers.put("acquire", acquire);
        readers.put("craft", craft);
        readers.put("fish", fish);
        readers.put("slay", slay);
        readers.put("trade", trade);

        writers.put(AcquireTaskInstance.class, acquire);
        writers.put(CraftingTaskInstance.class, craft);
        writers.put(FishingTaskInstance.class, fish);
        writers.put(SlayTaskInstance.class, slay);
        writers.put(TradingTaskInstance.class, trade);
        writers.put(EnchantedItemAcquireTaskInstance.class, new EnchantedItemAcquireW());
    }

    private void add(Player p, File file) {
        playerFiles.put(p.getUniqueId(), file);
    }
    private void add(UUID uuid, File file) {
        playerFiles.put(uuid, file);
    }

    public File load(Player p) {
        if (playerFiles.containsKey(p.getUniqueId()))
            return playerFiles.get(p.getUniqueId());

        return create(p);
    }
    public File load(UUID uuid, String playerName) {
        if (playerFiles.containsKey(uuid))
            return playerFiles.get(uuid);

        return create(uuid, playerName);
    }

    private File create(Player p) {
        return create(p.getUniqueId(), p.getName());
    }

    private File create(UUID uuid, String playerName) {
        File f = new File(plugin.getDataFolder(),
                String.format("%sPlayerDatabase%s%s.yml", File.separator, File.separator, uuid.toString()));
        add(uuid, f);
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(f);
        pfc.set("playerName", playerName);
        try {
            pfc.save(f);
        } catch (IOException e) {
            plugin.getLogger().warning(String.format("Failed to save player %s (uuid:%s) file under PlayerDatabase.", playerName, uuid));
        }

        return f;
    }

    public void delete(Player p) {
        playerFiles.remove(p.getUniqueId());
    }

    public void delete(UUID uuid) {
        playerFiles.remove(uuid);
    }

    public Optional<TaskInstance> readTask(Player p) {
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(load(p));
        if (!pfc.contains("task"))
            return Optional.empty();

        ConfigurationSection taskSection = pfc.getConfigurationSection("task");
        if (!taskSection.contains("type"))
            return Optional.empty();

        return readers.get(taskSection.get("type")).read(p, taskSection);
    }

    public void writeTask(Player p) {
        File playerFile = load(p);
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(playerFile);
        if (!taskManager.hasTask(p))
            return;

        if (!pfc.contains("task"))
            pfc.createSection("task");

        ConfigurationSection taskSection = pfc.getConfigurationSection("task");
        TaskInstance taskInstance = taskManager.readTask(p);

        writers.get(taskInstance.getClass()).write(taskSection, taskInstance);

        try {
            pfc.save(playerFile);
            plugin.getLogger().info(String.format("Successfully saved player %s (uuid:%s) file under PlayerDatabase.", p.getName(), p.getUniqueId()));
        } catch (IOException e) {
            plugin.getLogger().warning(String.format("Failed to save player %s (uuid:%s) file under PlayerDatabase.", p.getName(), p.getUniqueId()));
        }
    }
}