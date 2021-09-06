package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.CraftingMission;
import ee.kmtster.missions.missions.CraftingMissionInstance;
import ee.kmtster.missions.missions.MissionInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CraftingMissionRW implements MissionReader, MissionWriter<CraftingMissionInstance> {

    @Override
    public Optional<MissionInstance<?>> read(Player p, ConfigurationSection missionSection) {
        return ReadWriteUtils.loadMaterial(p, missionSection).flatMap(
                mat -> ReadWriteUtils.loadAmount(p, missionSection).map(
                        amount -> new CraftingMissionInstance(new CraftingMission(mat), amount)
                )
        );
    }

    @Override
    public void write(ConfigurationSection missionSection, CraftingMissionInstance missionInstance) {
        missionSection.set("type", "craft");
        missionSection.set("material", missionInstance.getMission().getItemToCraft().name().toLowerCase());
        missionSection.set("amount", missionInstance.getLeftToCraft());
    }
}
