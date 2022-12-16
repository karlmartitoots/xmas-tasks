package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.SlayMission;
import ee.kmtster.missions.missions.SlayMissionInstance;
import ee.kmtster.missions.missions.MissionInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SlayMissionRW implements MissionReader, MissionWriter<SlayMissionInstance> {
    @Override
    public Optional<MissionInstance<?>> read(Player p, ConfigurationSection missionSection) {
        return ReadWriteUtils.loadMob(p, missionSection).flatMap(
                mob -> ReadWriteUtils.loadAmount(p, missionSection).map(
                        amount -> new SlayMissionInstance(new SlayMission(mob), amount)
                )
        );
    }

    @Override
    public void write(ConfigurationSection missionSection, SlayMissionInstance missionInstance) {
        missionSection.set("type", "slay");
        missionSection.set("mob", missionInstance.getMission().getMobToKill().name().toLowerCase());
        missionSection.set("amount", missionInstance.getLeftToKill());
    }
}
