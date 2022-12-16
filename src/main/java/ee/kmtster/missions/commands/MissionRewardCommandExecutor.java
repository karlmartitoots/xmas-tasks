package ee.kmtster.missions.commands;

import ee.kmtster.missions.Leaderboard;
import ee.kmtster.missions.SendPlayerMessage;
import ee.kmtster.missions.missions.Mission;
import ee.kmtster.missions.missions.MissionInstance;
import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.missions.ObtainMissionInstance;
import ee.kmtster.missions.playerfiles.PlayerFilesManager;
import ee.kmtster.missions.rewards.Rewards;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MissionRewardCommandExecutor implements CommandExecutor {

    private final MissionManager missionManager;
    private final Leaderboard leaderboard;
    private final PlayerFilesManager filesManager;

    public MissionRewardCommandExecutor(MissionManager missionManager, Leaderboard leaderboard, PlayerFilesManager filesManager) {
        this.missionManager = missionManager;
        this.leaderboard = leaderboard;
        this.filesManager = filesManager;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] strings) {
        Player p = (Player) commandSender;

        if (!missionManager.hasMission(p)) {
            SendPlayerMessage.noMission(p);
            return true;
        }

        MissionInstance<? extends Mission> currentMission = missionManager.readMission(p);
        if (currentMission instanceof ObtainMissionInstance)
            missionManager.checkObtainMission(p, (ObtainMissionInstance) currentMission);

        if (!currentMission.isFinished()) {

            SendPlayerMessage.missionNotFinished(p);
            p.sendMessage(currentMission.progress());

            return true;
        }

        p.sendMessage(String.format("%sWell done! Have a reward for completing the mission.", ChatColor.YELLOW));

        p.getWorld().dropItem(p.getLocation(), Rewards.present());

        if (!leaderboard.has(p))
            leaderboard.add(p);
        leaderboard.increment(p);

        missionManager.deleteMission(p);
        filesManager.writeMission(p);

        return true;
    }
}
