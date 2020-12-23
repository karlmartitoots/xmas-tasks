package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.configuration.ConfigurationSection;

public interface TaskWriter<T extends TaskInstance> {
    void write(ConfigurationSection taskSection, T taskInstance);
}
