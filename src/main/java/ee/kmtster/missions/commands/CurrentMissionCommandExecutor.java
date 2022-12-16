package ee.kmtster.missions.commands;

import ee.kmtster.missions.SendPlayerMessage;
import ee.kmtster.missions.missions.Mission;
import ee.kmtster.missions.missions.MissionInstance;
import ee.kmtster.missions.missions.MissionManager;
import ee.kmtster.missions.missions.ObtainMissionInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CurrentMissionCommandExecutor  implements CommandExecutor {

    private final MissionManager missionManager;

    public CurrentMissionCommandExecutor(MissionManager missionManager) {
        this.missionManager = missionManager;
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

        if (currentMission.isFinished())
            SendPlayerMessage.missionCompleted(p);
        else
            p.sendMessage(currentMission.progress());

        return true;
    }
}
