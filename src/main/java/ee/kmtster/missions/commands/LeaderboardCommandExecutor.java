package ee.kmtster.missions.commands;

import ee.kmtster.missions.Leaderboard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderboardCommandExecutor implements CommandExecutor {

    private final Leaderboard leaderboard;

    public LeaderboardCommandExecutor(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args) {
        Player p = (Player) commandSender;

        if (args.length == 2 && args[1].equalsIgnoreCase("reset") && p.isOp()) {
            leaderboard.reset();
            p.sendMessage("Deleted all entries on the leaderboard");
            return true;
        }

        p.sendMessage(leaderboard.display());

        return true;
    }
}
