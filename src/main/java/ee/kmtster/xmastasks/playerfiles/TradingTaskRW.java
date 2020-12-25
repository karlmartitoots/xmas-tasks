package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.TaskInstance;
import ee.kmtster.xmastasks.tasks.TradingTask;
import ee.kmtster.xmastasks.tasks.TradingTaskInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TradingTaskRW implements TaskReader, TaskWriter<TradingTaskInstance> {

    @Override
    public Optional<TaskInstance> read(Player p, ConfigurationSection taskSection) {
        return ReadWriteUtils.loadMaterial(p, taskSection).map(
                material -> new TradingTaskInstance(new TradingTask(material))
        );
    }

    @Override
    public void write(ConfigurationSection taskSection, TradingTaskInstance taskInstance) {
        taskSection.set("type", "trade");
        taskSection.set("material", taskInstance.getTask().getMaterialToReceive().name().toLowerCase());
    }
}
