package ee.kmtster.missions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class Leaderboard {
    private Map<String, Integer> missionCounts = new LinkedHashMap<>(); // playerName -> missions completed
    private final Plugin plugin;

    private final File leaderBoardsFile;

    public Leaderboard(Plugin plugin) {
        this.plugin = plugin;
        this.leaderBoardsFile = load();
    }

    private File load() {
        File f = new File(plugin.getDataFolder(), "leaderboard.yml");
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(f);

        Set<String> entries = pfc.getKeys(false);
        for (String entry : entries) {
            missionCounts.put(entry, pfc.getInt(entry));
        }

        return f;
    }

    public void save() {
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(leaderBoardsFile);
        for (String playerName : missionCounts.keySet()) {
            pfc.set(playerName, missionCounts.get(playerName));
        }

        try {
            pfc.save(leaderBoardsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save leaderboards.");
        }
    }

    public void save(Player p) {
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(leaderBoardsFile);
        pfc.set(p.getName(), missionCounts.get(p.getName()));

        try {
            pfc.save(leaderBoardsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save leaderboards.");
        }
    }

    public boolean has(Player p) {
        return missionCounts.containsKey(p.getName());
    }

    public void add(Player p) {
        missionCounts.put(p.getName(), 0);
    }

    public void increment(Player p) {
        if (missionCounts.containsKey(p.getName()))
            missionCounts.put(p.getName(), missionCounts.get(p.getName()) + 1);
    }

    public void reset() {
        missionCounts = new LinkedHashMap<>();
    }

    public String display() {
        if (missionCounts.isEmpty())
            return String.format("%sThere is nobody on the leaderboard yet.", ChatColor.YELLOW);

        List<String> topTen =
                missionCounts.entrySet().stream()
                        .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .map(entry -> String.format("%s (%s%s mission%s completed%s)", entry.getKey(), ChatColor.GREEN, entry.getValue(), entry.getValue() == 1 ? "" : "s", ChatColor.YELLOW) )
                        .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s===== %sMissions Leaderboards %s=====\n", ChatColor.YELLOW, ChatColor.BLUE, ChatColor.YELLOW));
        int place = 1;
        for (String playerEntry : topTen) {
            builder.append(String.format("%s%s. ", ChatColor.GREEN, place));
            builder.append(String.format("%s%s ", ChatColor.YELLOW, playerEntry));
            builder.append("\n");
            place++;
        }

        return builder.toString();
    }
}
