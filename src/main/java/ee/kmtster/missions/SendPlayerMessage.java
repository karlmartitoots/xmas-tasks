package ee.kmtster.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SendPlayerMessage {
    public static void noMission(Player p) {
        p.sendMessage(String.format("%sYou currently do not have a Mission.", ChatColor.YELLOW));
    }

    public static void missionNotFinished(Player p) {
        p.sendMessage(String.format("%sYour current Mission is not finished yet.", ChatColor.YELLOW));
    }

    public static void alreadyHaveMission(Player p) {
        p.sendMessage(String.format("%sYou currently already have a Mission.", ChatColor.YELLOW));
    }

    public static void missionAlreadyCompleted(Player p){
        p.sendMessage(String.format("%sYou have already completed your Mission. Claim your prize using /missions reward.", ChatColor.YELLOW));
    }

    public static void missionCompleted(Player p) {
        p.sendMessage(String.format("%sYour current Mission is completed! Claim your reward with %s/missions reward.", ChatColor.YELLOW, ChatColor.GREEN));
    }
}
