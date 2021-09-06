package ee.kmtster.missions.missions;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class ObtainMissionInstance implements MissionInstance<ObtainMission> {
    private final ObtainMission mission;
    private final int amount;
    private boolean finished;

    public ObtainMissionInstance(ObtainMission mission, int amount) {
        this.mission = mission;
        this.amount = amount;
        this.finished = false;
    }

    public ObtainMission getMission() {
        return mission;
    }

    public int getAmount() {
        return amount;
    }

    public void finish() {
        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    public void check(Map<Integer, ItemStack> itemsBySlot) {
        Optional<Integer> amount = itemsBySlot.keySet().stream()
                .map(slot -> itemsBySlot.get(slot).getAmount())
                .reduce(Integer::sum);
        if (amount.isPresent() && getAmount() <= amount.get()) {
            finish();
        }
    }

    @Override
    public String progress() {
        return String.format("%sYour mission to obtain %s%s %s %sis %sfinished. Have them in your inventory to finish.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                amount,
                mission.getItemToObtain().name().toLowerCase().replace("_", " "),
                ChatColor.YELLOW,
                isFinished() ? "" : "not ");
    }

    @Override
    public String display() {
        return String.format("%sYour mission is to obtain %s%s %ss.",
                ChatColor.YELLOW,
                ChatColor.GREEN,
                amount,
                mission.getItemToObtain().name().toLowerCase().replace("_", " "));
    }
}
