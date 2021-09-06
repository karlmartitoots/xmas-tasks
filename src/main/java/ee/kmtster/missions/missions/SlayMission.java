package ee.kmtster.missions.missions;

import org.bukkit.entity.EntityType;

import java.util.Random;

public class SlayMission extends Mission {
    private EntityType mobToKill;
    private int min;
    private int max;

    public SlayMission(int weight, EntityType mobToKill, int min, int max) {
        super("slay", weight);
        this.mobToKill = mobToKill;
        this.min = min;
        this.max = max;
    }

    public SlayMission(EntityType mobToKill) {
        super("slay");
        this.mobToKill = mobToKill;
    }

    public EntityType getMobToKill() {
        return mobToKill;
    }

    @Override
    public MissionInstance generate(Random random) {
        return new SlayMissionInstance(this,
                min == max ? min : min + random.nextInt(max - min));
    }
}
