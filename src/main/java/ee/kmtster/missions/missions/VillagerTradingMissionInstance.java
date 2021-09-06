package ee.kmtster.missions.missions;

import org.bukkit.ChatColor;

public class VillagerTradingMissionInstance implements MissionInstance<VillagerTradingMission> {
    private final VillagerTradingMission mission;
    private boolean finished;

    public VillagerTradingMissionInstance(VillagerTradingMission mission) {
        this.mission = mission;
        this.finished = false;
    }

    public VillagerTradingMission getMission() {
        return mission;
    }

    public void finish() {
        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public String progress() {
        return String.format("%sYour mission to receive any amount of the item %s%s %sin trade with a villager is %sfinished.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                mission.getMaterialToReceive().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW,
                isFinished() ? "" : "not ");
    }

    @Override
    public String display() {
        return String.format("%sYour mission is to trade with a villager and receive any amount of the item %s%s.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                mission.getMaterialToReceive().name().toLowerCase().replace("_", " "));
    }
}
