package ee.kmtster.xmastasks.commands;

import ee.kmtster.xmastasks.XmasTaskManager;
import ee.kmtster.xmastasks.XmasTasksPlugin;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class XmasTaskCommandExecutor implements TabExecutor {
    private final Plugin plugin;
    private final XmasTaskManager taskManager;
    private final List<String> options = Arrays.asList("help", "new", "current", "delete", "reward", "leaderboard");

    public XmasTaskCommandExecutor(XmasTasksPlugin plugin, XmasTaskManager taskManager) {
        this.plugin = plugin;
        PluginCommand cmd = plugin.getCommand("xmastasks");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
        this.taskManager = taskManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only usable by players");
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0 || "help".equalsIgnoreCase(args[0]))
            return help(p);

        switch (args[0].toLowerCase()) {
            case "new":
                return newTask(p);
            case "current":
                return currentTask(p);
            case "delete":
                return deleteTask(p);
            case "reward":
                return taskReward(p);
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
        return false;
    }

    private boolean taskReward(Player p) {
        if (!taskManager.hasTask(p)) {
            p.sendMessage(String.format("%sYou currently do not have a Christmas Task.", ChatColor.YELLOW));
            return true;
        }

        TaskInstance currentTask = taskManager.readTask(p);
        if (!currentTask.isFinished()) {
            p.sendMessage(String.format("%sYour current Christmas Task is not finished yet.", ChatColor.YELLOW));
            p.sendMessage(currentTask.toString());
        } else {
            p.sendMessage(String.format("%sWell done! Santa has given you a reward for completing the task.", ChatColor.YELLOW));

        }

        return true;
    }

    private boolean deleteTask(Player p) {
        if (!taskManager.hasTask(p)) {
            p.sendMessage(String.format("%sYou currently do not have a Christmas Task.", ChatColor.YELLOW));
            return true;
        }

        taskManager.deleteTask(p);
        p.sendMessage(String.format("%sYour current Christmas Task has been removed.", ChatColor.YELLOW));

        return true;
    }

    private boolean currentTask(Player p) {
        if (!taskManager.hasTask(p)) {
            p.sendMessage(String.format("%sYou currently do not have a Christmas Task.", ChatColor.YELLOW));
            return true;
        }

        TaskInstance currentTask = taskManager.readTask(p);
        if (currentTask.isFinished())
            p.sendMessage(String.format("%sYour current Christmas Task is completed! Claim your reward with %s/xmastasks reward.", ChatColor.YELLOW, ChatColor.GREEN));
        else
            p.sendMessage(currentTask.toString());

        return true;
    }

    private boolean newTask(Player p) {
        if (taskManager.hasTask(p)) {
            p.sendMessage(String.format("%sYou currently already have a Christmas Task.", ChatColor.YELLOW));
            p.sendMessage(taskManager.readTask(p).toString());
            return true;
        }

        taskManager.createTask(p);
        return currentTask(p);
    }

    private boolean help(Player p) {
        p.sendMessage("Help here");
        return true;
    }
}
