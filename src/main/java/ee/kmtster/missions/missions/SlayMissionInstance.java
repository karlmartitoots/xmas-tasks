package ee.kmtster.missions.missions;

import org.bukkit.ChatColor;

public class SlayMissionInstance implements MissionInstance<SlayMission> {
    private final SlayMission slayMission;
    private int leftToKill;

    public SlayMissionInstance(SlayMission slayMission, int leftToKill) {
        this.slayMission = slayMission;
        this.leftToKill = leftToKill;
    }

    public SlayMission getMission() {
        return slayMission;
    }

    public int getLeftToKill() {
        return leftToKill;
    }

    public void decrement() {
        this.leftToKill--;
    }

    @Override
    public boolean isFinished() {
        return leftToKill == 0;
    }

    @Override
    public String progress() {
        return String.format("%s(Mission) %s%s %ss %sleft to slay.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToKill,
                slayMission.getMobToKill().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW);
    }

    @Override
    public String display() {
        return String.format("%sYour mission is to slay %s%s %ss.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                leftToKill,
                slayMission.getMobToKill().name().toLowerCase().replace("_", " "));
    }
}
