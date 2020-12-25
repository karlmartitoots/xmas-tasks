package ee.kmtster.xmastasks;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Leaderboard {
    private final Map<String, Integer> taskCounts = new LinkedHashMap<>(); // playerName -> tasks completed
    private final Plugin plugin;

    private static File leaderBoardsFile;

    public Leaderboard(Plugin plugin) {
        this.plugin = plugin;
        this.leaderBoardsFile = load();
    }

    private File load() {
        File f = new File(plugin.getDataFolder(), "leaderboard.yml");
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(f);

        Set<String> entries = pfc.getKeys(false);
        for (String entry : entries) {
            taskCounts.put(entry, pfc.getInt(entry));
        }

        return f;
    }

    public void save() {
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(leaderBoardsFile);
        for (String playerName : taskCounts.keySet()) {
            pfc.set(playerName, taskCounts.get(playerName));
        }

        try {
            pfc.save(leaderBoardsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save leaderboards.");
        }
    }

    public void save(Player p) {
        FileConfiguration pfc = YamlConfiguration.loadConfiguration(leaderBoardsFile);
        pfc.set(p.getName(), taskCounts.get(p.getName()));

        try {
            pfc.save(leaderBoardsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save leaderboards.");
        }
    }

    public boolean has(Player p) {
        return taskCounts.containsKey(p.getName());
    }

    public void add(Player p) {
        taskCounts.put(p.getName(), 0);
    }

    public void increment(Player p) {
        if (taskCounts.containsKey(p.getName()))
            taskCounts.put(p.getName(), taskCounts.get(p.getName()) + 1);
    }

    public String display() {
        if (taskCounts.isEmpty())
            return String.format("%sThere is nobody on the leaderboard yet.", ChatColor.YELLOW);

        List<String> topTen =
                taskCounts.entrySet().stream()
                        .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .map(entry -> String.format("%s (%s%s task%s completed%s)", entry.getKey(), ChatColor.GREEN, entry.getValue(), entry.getValue() == 1 ? "" : "s", ChatColor.YELLOW) )
                        .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s===== %sChristmas Task Leaderboards %s=====\n", ChatColor.YELLOW, ChatColor.BLUE, ChatColor.YELLOW));
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
