package ee.kmtster.missions.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] strings) {
        Player p = (Player) commandSender;

        p.sendMessage(String.format("%s===============       %sMissions      %s===============", ChatColor.WHITE, ChatColor.RED, ChatColor.WHITE));

        p.sendMessage(String.format("  %s%sCommands:", ChatColor.DARK_BLUE, ChatColor.BOLD));
        p.sendMessage(String.format("  %s/missions help %s.. %s%sThis information page", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/missions new %s.. %s%sAssign a new mission", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/missions current %s.. %s%sDisplay current Mission", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/missions delete %s.. %s%sRemove current Mission (5-min cooldown)", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/missions reward %s.. %s%sClaim reward after completing the Mission", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/missions leaderboard %s.. %s%sSee player leaderboards", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));

        p.sendMessage(String.format("%s===============                                 ===============",ChatColor.WHITE));
        return true;
    }
}
