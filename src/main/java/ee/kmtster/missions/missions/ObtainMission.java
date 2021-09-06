package ee.kmtster.missions.missions;

import org.bukkit.Material;

import java.util.Random;

public class ObtainMission extends Mission {
    private final Material itemToObtain;
    private int min;
    private int max;

    public ObtainMission(int weight, Material itemToObtain, int min, int max) {
        super("obtain", weight);
        this.itemToObtain = itemToObtain;
        this.min = min;
        this.max = max;
    }

    public ObtainMission(int weight, Material itemToObtain) { // default 1
        super("obtain", weight);
        this.itemToObtain = itemToObtain;
        this.min = 1;
        this.max = 1;
    }

    public ObtainMission(Material itemToObtain) {
        super("obtain");
        this.itemToObtain = itemToObtain;
    }

    public Material getItemToObtain() {
        return itemToObtain;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public MissionInstance<ObtainMission> generate(Random random) {
        return new ObtainMissionInstance(this,
                min == max ? min : min + random.nextInt(max - min));
    }

    public boolean check() {
        return true;
    }
}
