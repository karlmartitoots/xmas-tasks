package ee.kmtster.missions.commands;

import ee.kmtster.missions.Leaderboard;
import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.playerfiles.PlayerFilesManager;
import ee.kmtster.missions.MissionsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MissionsCommandExecutor implements TabExecutor {
    private final List<String> options = PlayerCommand.listStrings();
    private final Map<String, CommandExecutor> commandExecutors;

    enum PlayerCommand {
        HELP, NEW, CURRENT, DELETE, REWARD,
        LEADERBOARD,
        ;

        private static List<String> listStrings() {
            return Arrays.stream(values())
                    .map((v) -> v.toString().toLowerCase())
                    .collect(Collectors.toList());
        }
    }

    enum OpCommand {
        LEADERBOARD_RESET,
        DELETE_OVERRIDE_COOLDOWN,
        ;
    }

    public MissionsCommandExecutor(MissionsPlugin plugin, MissionManager missionManager, PlayerFilesManager filesManager, Leaderboard leaderboard) {
        PluginCommand cmd = plugin.getCommand("missions");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }

        // setup command executors
        commandExecutors = new LinkedHashMap<>();
        commandExecutors.put("help", new HelpCommandExecutor());
        commandExecutors.put("new", new NewMissionCommandExecutor(missionManager));
        commandExecutors.put("current", new CurrentMissionCommandExecutor(missionManager));
        commandExecutors.put("delete", new DeleteMissionCommandExecutor(missionManager, filesManager));
        commandExecutors.put("reward", new MissionRewardCommandExecutor(missionManager, leaderboard, filesManager));
        commandExecutors.put("leaderboard", new LeaderboardCommandExecutor(leaderboard));
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only available to players");
            return true;
        }

        CommandExecutor help = commandExecutors.get("help");

        if (args.length == 0)
            return help.onCommand(sender, command, s, args);

        CommandExecutor commandExecutor = commandExecutors.get(args[0].toLowerCase());
        if (commandExecutor == null) {
            sender.sendMessage("Invalid command");
            return help.onCommand(sender, command, s, args);
        }

        return commandExecutor.onCommand(sender, command, s, args);
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args) {
        if (args.length == 1) {
            return options;
        }

        return null;
    }
}
