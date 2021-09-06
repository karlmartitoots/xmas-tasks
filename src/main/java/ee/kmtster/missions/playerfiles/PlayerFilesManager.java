package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.missions.*;
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
    private final MissionManager missionManager;
    private final Map<String, MissionReader> readers = new ConcurrentHashMap<>();
    private final Map<Class<? extends MissionInstance>, MissionWriter> writers = new ConcurrentHashMap<>();

    public PlayerFilesManager(Plugin plugin, MissionManager missionManager) {
        this.plugin = plugin;
        this.missionManager = missionManager;

        this.setupRW();
        File userdata = new File(plugin.getDataFolder(), File.separator + "PlayerDatabase");
        if (!userdata.exists())
            userdata.mkdir();

    }

    private void setupRW() {
        ObtainMissionRW obtain = new ObtainMissionRW();
        CraftingMissionRW craft = new CraftingMissionRW();
        FishingMissionRW fish = new FishingMissionRW();
        SlayMissionRW slay = new SlayMissionRW();
        VillagerTradingMissionRW trade = new VillagerTradingMissionRW();

        readers.put("obtain", obtain);
        readers.put("craft", craft);
        readers.put("fish", fish);
        readers.put("slay", slay);
        readers.put("trade", trade);

        writers.put(ObtainMissionInstance.class, obtain);
        writers.put(CraftingMissionInstance.class, craft);
        writers.put(FishingMissionInstance.class, fish);
        writers.put(SlayMissionInstance.class, slay);
        writers.put(VillagerTradingMissionInstance.class, trade);
        writers.put(EnchantedItemObtainMissionInstance.class, new EnchantedItemObtainW());
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
        File f = new File(plugin.getDataFolder(), String.format("%sPlayerDatabase%s%s.yml", File.separator, File.separator, uuid.toString()));
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

    public Optional<MissionInstance<?>> readMission(Player p) {
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(load(p));
        if (!pfc.contains("mission"))
            return Optional.empty();

        ConfigurationSection missionSection = pfc.getConfigurationSection("mission");
        if (!missionSection.contains("type"))
            return Optional.empty();

        Optional<MissionInstance<?>> missionInstance = readers.get(missionSection.get("type")).read(p, missionSection);

        missionInstance.ifPresent(
                i -> missionManager.putMission(p, i)
        );

        return missionInstance;
    }

    public void writeMission(Player p) {
        File playerFile = load(p);
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(playerFile);
        if (!missionManager.hasMission(p)) {
            pfc.set("mission", null);
        } else {

            if (!pfc.contains("mission"))
                pfc.createSection("mission");

            ConfigurationSection missionSection = pfc.getConfigurationSection("mission");
            MissionInstance<? extends Mission> missionInstance = missionManager.readMission(p);

            writers.get(missionInstance.getClass()).write(missionSection, missionInstance);
        }

        try {
            pfc.save(playerFile);
            plugin.getLogger().info(String.format("[Missions] Successfully saved player %s (uuid:%s) file under PlayerDatabase.", p.getName(), p.getUniqueId()));
        } catch (IOException e) {
            plugin.getLogger().warning(String.format("Failed to save player %s (uuid:%s) file under PlayerDatabase.", p.getName(), p.getUniqueId()));
        }
    }
}