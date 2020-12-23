package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.FishingTask;
import ee.kmtster.xmastasks.tasks.FishingTaskInstance;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class FishingTaskRW implements TaskReader, TaskWriter<FishingTaskInstance> {

    @Override
    public Optional<TaskInstance> read(Player p, ConfigurationSection taskSection) {
        return ReadWriteUtils.loadMaterial(p, taskSection).flatMap(
                mat -> ReadWriteUtils.loadAmount(p, taskSection).map(
                        amount -> new FishingTaskInstance(new FishingTask(mat), amount)
                )
        );
    }

    @Override
    public void write(ConfigurationSection taskSection, FishingTaskInstance taskInstance) {
        taskSection.set("type", "acquire");
        taskSection.set("material", taskInstance.getTask().getFishToCatch().name().toLowerCase());
        taskSection.set("amount", taskInstance.getLeftToCatch());
    }
}
