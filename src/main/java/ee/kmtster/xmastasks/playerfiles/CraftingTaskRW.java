package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.CraftingTask;
import ee.kmtster.xmastasks.tasks.CraftingTaskInstance;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CraftingTaskRW implements TaskReader, TaskWriter<CraftingTaskInstance> {

    @Override
    public Optional<TaskInstance> read(Player p, ConfigurationSection taskSection) {
        return ReadWriteUtils.loadMaterial(p, taskSection).flatMap(
                mat -> ReadWriteUtils.loadAmount(p, taskSection).map(
                        amount -> new CraftingTaskInstance(new CraftingTask(mat), amount)
                )
        );
    }

    @Override
    public void write(ConfigurationSection taskSection, CraftingTaskInstance taskInstance) {
        taskSection.set("type", "craft");
        taskSection.set("material", taskInstance.getTask().getItemToCraft().name().toLowerCase());
        taskSection.set("amount", taskInstance.getLeftToCraft());
    }
}
