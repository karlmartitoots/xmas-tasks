package ee.kmtster.missions.missions;

import org.bukkit.Material;

import java.util.Random;

public class CraftingMission extends Mission {
    private final Material itemToCraft;
    private int min;
    private int max;

    public CraftingMission(int weight, Material itemToCraft, int min, int max) {
        super("craft", weight);
        this.itemToCraft = itemToCraft;
        this.min = min;
        this.max = max;
    }

    public CraftingMission(Material itemToCraft) {
        super("craft");
        this.itemToCraft = itemToCraft;
    }

    public Material getItemToCraft() {
        return itemToCraft;
    }

    @Override
    public MissionInstance<CraftingMission> generate(Random random) {
        return new CraftingMissionInstance(this,
                min == max ? min : min + random.nextInt(max - min));
    }
}
