package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.SlayTask;
import ee.kmtster.xmastasks.tasks.SlayTaskInstance;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SlayTaskRW implements TaskReader, TaskWriter<SlayTaskInstance> {
    @Override
    public Optional<TaskInstance> read(Player p, ConfigurationSection taskSection) {
        return ReadWriteUtils.loadMob(p, taskSection).flatMap(
                mob -> ReadWriteUtils.loadAmount(p, taskSection).map(
                        amount -> new SlayTaskInstance(new SlayTask(mob), amount)
                )
        );
    }

    @Override
    public void write(ConfigurationSection taskSection, SlayTaskInstance taskInstance) {
        taskSection.set("type", "acquire");
        taskSection.set("mob", taskInstance.getTask().getMobToKill().name().toLowerCase());
        taskSection.set("amount", taskInstance.getLeftToKill());
    }
}
