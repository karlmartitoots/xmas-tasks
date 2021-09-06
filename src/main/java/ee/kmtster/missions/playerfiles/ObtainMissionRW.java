package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ObtainMissionRW implements MissionReader, MissionWriter<ObtainMissionInstance> {

    @Override
    public Optional<MissionInstance<?>> read(Player p, ConfigurationSection missionSection) {
        return ReadWriteUtils.loadMaterial(p, missionSection).flatMap(
                mat -> {
                    if (missionSection.contains("enchantments"))
                        return ReadWriteUtils.loadEnchantments(p, missionSection)
                                .map(enchantments -> new EnchantedItemObtainMissionInstance(new ObtainMission(mat), enchantments));
                    else
                        return ReadWriteUtils.loadAmount(p, missionSection)
                                .map(amount -> new ObtainMissionInstance(new ObtainMission(mat), amount));
                }
        );
    }

    @Override
    public void write(ConfigurationSection missionSection, ObtainMissionInstance missionInstance) {
        missionSection.set("type", "obtain");
        missionSection.set("material", missionInstance.getMission().getItemToObtain().name().toLowerCase());
        missionSection.set("amount", missionInstance.getAmount());
    }
}
