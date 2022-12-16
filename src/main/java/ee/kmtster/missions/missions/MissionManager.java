package ee.kmtster.missions.missions;

import ee.kmtster.missions.RandomCollection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

        playersMissions.put(player.getUniqueId(), missionInstance);
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

    public void checkObtainMission(Player p, ObtainMissionInstance missionInstance) {
        Inventory inv = p.getInventory();
        if (!inv.contains(missionInstance.getMission().getItemToObtain()))
            return;

        Map<Integer, ItemStack> itemsBySlot = (Map<Integer, ItemStack>) inv.all(missionInstance.getMission().getItemToObtain());
        missionInstance.check(itemsBySlot);
    }
}
