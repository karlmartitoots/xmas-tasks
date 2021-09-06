package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class FishingMissionRW implements MissionReader, MissionWriter<FishingMissionInstance> {

    @Override
    public Optional<MissionInstance<?>> read(Player p, ConfigurationSection missionSection) {
        return ReadWriteUtils.loadMaterial(p, missionSection).flatMap(
                mat -> ReadWriteUtils.loadAmount(p, missionSection).map(
                        amount -> new FishingMissionInstance(new FishingMission(mat), amount)
                )
        );
    }

    @Override
    public void write(ConfigurationSection missionSection, FishingMissionInstance missionInstance) {
        missionSection.set("type", "fish");
        missionSection.set("material", missionInstance.getMission().getFishToCatch().name().toLowerCase());
        missionSection.set("amount", missionInstance.getLeftToCatch());
    }
}
