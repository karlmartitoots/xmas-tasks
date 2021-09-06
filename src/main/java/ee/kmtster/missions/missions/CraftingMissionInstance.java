package ee.kmtster.missions.missions;

import org.bukkit.ChatColor;

public class CraftingMissionInstance implements MissionInstance<CraftingMission> {
    private final CraftingMission mission;
    private int leftToCraft;

    public CraftingMissionInstance(CraftingMission mission, int leftToCraft) {
        this.mission = mission;
        this.leftToCraft = leftToCraft;
    }

    public CraftingMission getMission() {
        return mission;
    }

    public int getLeftToCraft() {
        return leftToCraft;
    }

    public void decrease(int by) {
        this.leftToCraft = Math.max(0, this.leftToCraft - by);
    }

    @Override
    public boolean isFinished() {
        return this.leftToCraft == 0;
    }

    @Override
    public String progress() {
        return String.format("%s(Mission) %s%s %ss %sleft to craft.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCraft,
                mission.getItemToCraft().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW);
    }

    @Override
    public String display() {
        return String.format("%sYour mission is to craft %s%s %ss.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToCraft,
                mission.getItemToCraft().name().toLowerCase().replace("_", " "));
    }
}
