package ee.kmtster.missions.missions;

import ee.kmtster.missions.RandomCollection;
import ee.kmtster.missions.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class MissionManager {
    private final Map<UUID, MissionInstance<? extends Mission>> playersMissions = new ConcurrentHashMap<>();
    private final RandomCollection<String> missionCategories = new RandomCollection<>();
    private final Map<String, RandomCollection<Mission>> missions = new LinkedHashMap<>();
    private final Random random = new Random();

    // Randomly generate a new mission
    public MissionInstance<? extends Mission> createMission(Player player) {
        if (hasMission(player))
            return readMission(player);

        String category = missionCategories.next();
        Mission mission = missions.get(category).next();
        MissionInstance<? extends Mission> missionInstance = mission.generate(random);

        playersMissions.put(player.getUniqueId(), mission.generate(random));
        return missionInstance;
    }

    public MissionInstance<? extends Mission> readMission(Player player) {
        return playersMissions.get(player.getUniqueId());
    }

    public boolean hasMission(Player player) {
        return playersMissions.containsKey(player.getUniqueId());
    }

    public MissionInstance<? extends Mission> deleteMission(Player player) {
        return playersMissions.remove(player.getUniqueId());
    }

    public void putMission(Player player, MissionInstance t) {
        playersMissions.put(player.getUniqueId(), t);
    }

    public void addMissionCategory(String cat, int weight) {
        missionCategories.add(weight, cat);
    }

    public void addMission(String category, Mission mission) {
        if (!missions.containsKey(category))
            missions.put(category, new RandomCollection<>());

        missions.get(category).add(mission.getWeight(), mission);
    }

    public void addMissions(String category, List<Mission> added) {
        if (!missions.containsKey(category))
            missions.put(category, new RandomCollection<>());

        for (Mission mission : added) {
            missions.get(category).add(mission.getWeight(), mission);
        }
    }
}
