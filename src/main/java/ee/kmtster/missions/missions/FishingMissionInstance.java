package ee.kmtster.missions.missions;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class FishingMissionInstance implements MissionInstance<FishingMission> {
    private final FishingMission mission;
    private int leftToCatch;

    public FishingMissionInstance(FishingMission mission, int leftToCatch) {
        this.mission = mission;
        this.leftToCatch = leftToCatch;
    }

    public FishingMission getMission() {
        return mission;
    }

    public int getLeftToCatch() {
        return leftToCatch;
    }

    public void decrease(int by, Plugin plugin) {
        this.leftToCatch = Math.max(0, this.leftToCatch - by);
        plugin.getLogger().info("Decreasing by amoount "+by);
        plugin.getLogger().info("lefttocatch "+this.leftToCatch);
    }

    @Override
    public boolean isFinished() {
        return leftToCatch == 0;
    }

    public String progress() {
        return String.format("%s(Missions) %s%s %s%s %sleft to catch.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCatch,
                mission.getFishToCatch().name().toLowerCase().replace("_", " "),
                leftToCatch == 1 ? "" : "s",
                ChatColor.YELLOW);
    }

    @Override
    public String display() {
        return String.format("%sYour mission is to catch %s%s %s%s %sby fishing.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCatch,
                mission.getFishToCatch().name().toLowerCase().replace("_", " "),
                leftToCatch == 1 ? "" : "s",
                ChatColor.YELLOW);
    }
}
