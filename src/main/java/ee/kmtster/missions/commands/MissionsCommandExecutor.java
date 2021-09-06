package ee.kmtster.missions.commands;

import ee.kmtster.missions.Leaderboard;
import ee.kmtster.missions.SendPlayerMessage;
import ee.kmtster.missions.missions.*;
import ee.kmtster.missions.playerfiles.PlayerFilesManager;
import ee.kmtster.missions.MissionsPlugin;
import ee.kmtster.missions.rewards.Rewards;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.*;

public class MissionsCommandExecutor implements TabExecutor {
    private final static Map<UUID, Long> cooldowns = new LinkedHashMap<>();

    private final Plugin plugin;
    private final Leaderboard leaderboard;
    private final MissionManager missionManager;
    private final PlayerFilesManager filesManager;
    private final List<String> options = Arrays.asList("help", "new", "current", "delete", "reward", "leaderboard");

    public MissionsCommandExecutor(MissionsPlugin plugin, MissionManager missionManager, PlayerFilesManager filesManager, Leaderboard leaderboard) {
        this.plugin = plugin;
        PluginCommand cmd = plugin.getCommand("missions");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
        this.missionManager = missionManager;
        this.filesManager = filesManager;
        this.leaderboard = leaderboard;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only available to players");
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0 || "help".equalsIgnoreCase(args[0]))
            return help(p);

        switch (args[0].toLowerCase()) {
            case "new":
                return newMission(p);
            case "current":
                return currentMission(p);
            case "delete":
                return deleteMission(p);
            case "reward":
                return missionReward(p);
            case "leaderboard":
                return leaderboard(p);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return options;
        }

        return null;
    }

    private boolean leaderboard(Player p) {
        p.sendMessage(leaderboard.display());

        return true;
    }

    private boolean missionReward(Player p) {
        if (!missionManager.hasMission(p)) {
            SendPlayerMessage.noMission(p);
            return true;
        }

        MissionInstance<? extends Mission> currentMission = missionManager.readMission(p);
        if (currentMission instanceof ObtainMissionInstance)
            checkObtainMission(p, (ObtainMissionInstance) currentMission);

        if (!currentMission.isFinished()) {

            SendPlayerMessage.missionNotFinished(p);
            p.sendMessage(currentMission.progress());

        } else {

            p.sendMessage(String.format("%sWell done! Have a reward for completing the mission.", ChatColor.YELLOW));

            p.getWorld().dropItem(p.getLocation(), Rewards.present());

            if (!leaderboard.has(p))
                leaderboard.add(p);
            leaderboard.increment(p);

            missionManager.deleteMission(p);
            filesManager.writeMission(p);

        }

        return true;
    }

    private boolean deleteMission(Player p) {
        if (!missionManager.hasMission(p)) {
            SendPlayerMessage.noMission(p);
            return true;
        }

        if (cooldowns.containsKey(p.getUniqueId())) {
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

    private String displayTimeLeft(long timeLeft) {
        return String.format("%s%s%s %sseconds",
                timeLeft > 60 ? String.format("%s%s %sminutes ", ChatColor.GREEN, timeLeft / 60, ChatColor.YELLOW) : "", // minutes
                ChatColor.GREEN,
                timeLeft % 60,  // seconds
                ChatColor.YELLOW);
    }

    private boolean currentMission(Player p) {
        if (!missionManager.hasMission(p)) {
            SendPlayerMessage.noMission(p);
            return true;
        }

        MissionInstance<? extends Mission> currentMission = missionManager.readMission(p);
        if (currentMission instanceof ObtainMissionInstance)
            checkObtainMission(p, (ObtainMissionInstance) currentMission);

        if (currentMission.isFinished())
            SendPlayerMessage.missionCompleted(p);
        else
            p.sendMessage(currentMission.progress());

        return true;
    }

    private boolean newMission(Player p) {
        if (missionManager.hasMission(p)) {
            SendPlayerMessage.alreadyHaveMission(p);
            p.sendMessage(missionManager.readMission(p).display());
            return true;
        }

        missionManager.createMission(p);

        MissionInstance<? extends Mission> currentMission = missionManager.readMission(p);

        p.sendMessage(currentMission.display());
        if (currentMission instanceof ObtainMissionInstance)
            checkObtainMission(p, (ObtainMissionInstance) currentMission);

        if (currentMission.isFinished())
            SendPlayerMessage.missionCompleted(p);

        return true;
    }

    private boolean help(Player p) {
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

    private void checkObtainMission(Player p, ObtainMissionInstance missionInstance) {
        Inventory inv = p.getInventory();
        if (!inv.contains(missionInstance.getMission().getItemToObtain()))
            return;

        Map<Integer, ItemStack> itemsBySlot = (Map<Integer, ItemStack>) inv.all(missionInstance.getMission().getItemToObtain());
        missionInstance.check(itemsBySlot);
    }
}
