package ee.kmtster.missions.missions;

import org.bukkit.Material;

import java.util.Random;

public class VillagerTradingMission extends Mission {
    private final Material materialToReceive;

    public VillagerTradingMission(int weight, Material materialToReceive) {
        super("trade", weight);
        this.materialToReceive = materialToReceive;
    }

    public VillagerTradingMission(Material materialToReceive) {
        super("trade");
        this.materialToReceive = materialToReceive;
    }

    public Material getMaterialToReceive() {
        return materialToReceive;
    }

    @Override
    public MissionInstance<VillagerTradingMission> generate(Random random) {
        return new VillagerTradingMissionInstance(this);
    }
}
