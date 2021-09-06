package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.MissionInstance;
import org.bukkit.configuration.ConfigurationSection;

public interface MissionWriter<T extends MissionInstance> {
    void write(ConfigurationSection missionSection, T missionInstance);
}
