package ee.kmtster.missions.commands;

import ee.kmtster.missions.SendPlayerMessage;
import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.playerfiles.PlayerFilesManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class DeleteMissionCommandExecutor implements CommandExecutor {

    private final static Map<UUID, Long> cooldowns = new LinkedHashMap<>();
    private final MissionManager missionManager;
    private final PlayerFilesManager filesManager;

    public DeleteMissionCommandExecutor(MissionManager missionManager, PlayerFilesManager filesManager) {
        this.missionManager = missionManager;
        this.filesManager = filesManager;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args) {
        Player p = (Player) commandSender;

        if (!missionManager.hasMission(p)) {
            SendPlayerMessage.noMission(p);
            return true;
        }

        if (cooldowns.containsKey(p.getUniqueId()) && !isOverride(p, args)) {
            long timeLeft = cooldowns.get(p.getUniqueId()) - System.currentTimeMillis();
            if (timeLeft > 0) {
                p.sendMessage(String.format("%sYou can not delete a Mission for another %s.",
                        ChatColor.YELLOW,
                        displayTimeLeft(timeLeft/1000)));
                return true;
            }
        }

        missionManager.deleteMission(p);
        filesManager.writeMission(p);

        p.sendMessage(String.format("%sYour current Mission has been removed.", ChatColor.YELLOW));

        cooldowns.put(p.getUniqueId(), System.currentTimeMillis() + Duration.ofMinutes(5).toMillis());

        return true;
    }

    private boolean isOverride(Player p, String[] args) {
        if (args.length == 2 && args[1].equalsIgnoreCase("override") && p.isOp()) {
            p.sendMessage("Skipping cooldown");
            return true;
        }

        return false;
    }

    private String displayTimeLeft(long timeLeft) {
        return String.format("%s%s%s %sseconds",
                timeLeft > 60 ? String.format("%s%s %sminutes ", ChatColor.GREEN, timeLeft / 60, ChatColor.YELLOW) : "", // minutes
                ChatColor.GREEN,
                timeLeft % 60,  // seconds
                ChatColor.YELLOW);
    }
}
