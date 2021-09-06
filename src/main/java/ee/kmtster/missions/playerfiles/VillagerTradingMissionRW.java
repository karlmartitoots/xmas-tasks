package ee.kmtster.missions.playerfiles;

import ee.kmtster.missions.missions.CraftingMission;
import ee.kmtster.missions.missions.MissionInstance;
import ee.kmtster.missions.missions.VillagerTradingMission;
import ee.kmtster.missions.missions.VillagerTradingMissionInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class VillagerTradingMissionRW implements MissionReader, MissionWriter<VillagerTradingMissionInstance> {

    @Override
    public Optional<MissionInstance<?>> read(Player p, ConfigurationSection missionSection) {
        return ReadWriteUtils.loadMaterial(p, missionSection).map(
                material -> new VillagerTradingMissionInstance(new VillagerTradingMission(material))
        );
    }

    @Override
    public void write(ConfigurationSection missionSection, VillagerTradingMissionInstance missionInstance) {
        missionSection.set("type", "trade");
        missionSection.set("material", missionInstance.getMission().getMaterialToReceive().name().toLowerCase());
    }
}
