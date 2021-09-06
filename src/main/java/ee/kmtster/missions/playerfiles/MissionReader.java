package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.MissionInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface MissionReader {
    Optional<MissionInstance<?>> read(Player p, ConfigurationSection missionSection);
}
