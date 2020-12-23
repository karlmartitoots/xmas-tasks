package ee.kmtster.xmastasks.playerfiles;

import ee.kmtster.xmastasks.tasks.AcquireTask;
import ee.kmtster.xmastasks.tasks.AcquireTaskInstance;
import ee.kmtster.xmastasks.tasks.EnchantedItemAcquireTaskInstance;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class AcquireTaskRW implements TaskReader, TaskWriter<AcquireTaskInstance> {

    @Override
    public Optional<TaskInstance> read(Player p, ConfigurationSection section) {
        return ReadWriteUtils.loadMaterial(p, section).flatMap(
                mat -> {
                    if (section.contains("enchantments"))
                        return ReadWriteUtils.loadEnchantments(p, section)
                                .map(enchantments -> new EnchantedItemAcquireTaskInstance(new AcquireTask(mat), enchantments));
                    else
                        return ReadWriteUtils.loadAmount(p, section)
                                .map(amount -> new AcquireTaskInstance(new AcquireTask(mat), amount));
                }
        );
    }

    @Override
    public void write(ConfigurationSection taskSection, AcquireTaskInstance taskInstance) {
        taskSection.set("type", "acquire");
        taskSection.set("material", taskInstance.getTask().getItemToAcquire().name().toLowerCase());
        taskSection.set("amount", taskInstance.getAmount());
    }
}
