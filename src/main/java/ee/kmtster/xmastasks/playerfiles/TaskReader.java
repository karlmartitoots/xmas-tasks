package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface TaskReader {
    Optional<TaskInstance> read(Player p, ConfigurationSection taskSection);
}
