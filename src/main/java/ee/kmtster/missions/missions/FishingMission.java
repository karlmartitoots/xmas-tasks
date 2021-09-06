package ee.kmtster.missions.missions;

import org.bukkit.Material;

import java.util.Random;

public class FishingMission extends Mission {
    private final Material fishToCatch;
    private int min;
    private int max;

    public FishingMission(int weight, Material fishToCatch, int min, int max) {
        super("fish", weight);
        this.fishToCatch = fishToCatch;
        this.min = min;
        this.max = max;
    }

    public FishingMission(Material fishToCatch) {
        super("fish");
        this.fishToCatch = fishToCatch;
    }

    public Material getFishToCatch() {
        return fishToCatch;
    }

    @Override
    public MissionInstance<FishingMission> generate(Random random) {
        return new FishingMissionInstance(this,
                min == max ? min : min + random.nextInt(max - min));
    }
}
