package ee.kmtster.xmastasks.players;

import ee.kmtster.xmastasks.XmasTasksPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class PlayerStatsFilePreparation {
    FileConfiguration playerData;

    public void initializePlayerDataBase() {
        Plugin plugin = XmasTasksPlugin.getPlugin(XmasTasksPlugin.class);
        File userdata = new File(plugin.getDataFolder(), File.separator + "PlayerDatabase");
        if(!userdata.exists()){
            userdata.mkdir();
        }
    }

    public void playJoinConditions(Player p) {
        String pName = p.getName();
        UUID pUUID = p.getUniqueId();
        preparePlayerFile(pName, pUUID);
    }

    public void preparePlayerFile(String pName, UUID pUUID) {

        PlayerFilesManager playerFilesManager = new PlayerFilesManager();
        File f = playerFilesManager.getPlayerFile(pUUID);
        playerData = YamlConfiguration.loadConfiguration(f);

        if  (!f.exists()) {

            playerData.set("playername", pName);

            // Current task information
            playerData.createSection("currenttask");
            playerData.set("currenttask.type", "slay"); // mine / craft / slay / fetch / fish / trade
            playerData.set("currenttask.objective", "zombie");
            playerData.set("currenttask.amount", "2");
            try {
                playerData.save(f);
            } catch (IOException e) {
                XmasTasksPlugin.getPlugin(XmasTasksPlugin.class).getLogger().warning("Failed to save data for player: " + pName);
            }
        } else {
            try {
                playerData.save(f);
            } catch (IOException e) {
                XmasTasksPlugin.getPlugin(XmasTasksPlugin.class).getLogger().warning("Failed to save data for player: " + pName);
            }
            playerFilesManager.addPlayerFile(pUUID,f);

        }

    }

    public void addIfMissing(String key, Object value) {
        if (!playerData.contains(key)) {
            playerData.set(key, value);
        }
    }
}
