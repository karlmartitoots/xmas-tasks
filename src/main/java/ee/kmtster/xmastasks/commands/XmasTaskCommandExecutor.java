package ee.kmtster.xmastasks.commands;

import ee.kmtster.xmastasks.DefaultRewards;
import ee.kmtster.xmastasks.Leaderboard;
import ee.kmtster.xmastasks.playerfiles.PlayerFilesManager;
import ee.kmtster.xmastasks.tasks.XmasTaskManager;
import ee.kmtster.xmastasks.XmasTasksPlugin;
import ee.kmtster.xmastasks.tasks.AcquireTaskInstance;
import ee.kmtster.xmastasks.tasks.EnchantedItemAcquireTaskInstance;
import ee.kmtster.xmastasks.tasks.TaskInstance;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static ee.kmtster.xmastasks.tasks.XmasTaskManager.present;

public class XmasTaskCommandExecutor implements TabExecutor {
    private final static Map<UUID, Long> cooldowns = new LinkedHashMap<>();

    private final Plugin plugin;
    private final Leaderboard leaderboard;
    private final XmasTaskManager taskManager;
    private final PlayerFilesManager filesManager;
    private final List<String> options = Arrays.asList("help", "new", "current", "delete", "reward", "leaderboard");

    public XmasTaskCommandExecutor(XmasTasksPlugin plugin, XmasTaskManager taskManager, PlayerFilesManager filesManager, Leaderboard leaderboard) {
        this.plugin = plugin;
        PluginCommand cmd = plugin.getCommand("xmastasks");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
        this.taskManager = taskManager;
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
        p.sendMessage(leaderboard.display());

        return true;
    }

    private boolean taskReward(Player p) {
        if (!taskManager.hasTask(p)) {
            p.sendMessage(String.format("%sYou currently do not have a Christmas Task.", ChatColor.YELLOW));
            return true;
        }

        TaskInstance currentTask = taskManager.readTask(p);
        if (currentTask instanceof AcquireTaskInstance)
            checkAcquireTask(p, (AcquireTaskInstance) currentTask);

        if (!currentTask.isFinished()) {

            p.sendMessage(String.format("%sYour current Christmas Task is not finished yet.", ChatColor.YELLOW));
            p.sendMessage(currentTask.progress());

        } else {

            p.sendMessage(String.format("%sWell done! Santa has given you a reward for completing the task.", ChatColor.YELLOW));

            p.getWorld().dropItem(p.getLocation(), present());

            if (!leaderboard.has(p))
                leaderboard.add(p);
            leaderboard.increment(p);

            taskManager.deleteTask(p);
            filesManager.writeTask(p);

        }

        return true;
    }

    private boolean deleteTask(Player p) {
        if (!taskManager.hasTask(p)) {
            p.sendMessage(String.format("%sYou currently do not have a Christmas Task.", ChatColor.YELLOW));
            return true;
        }

        if (cooldowns.containsKey(p.getUniqueId())) {
            long timeLeft = cooldowns.get(p.getUniqueId()) - System.currentTimeMillis();
            if (timeLeft > 0) {
                p.sendMessage(String.format("%sYou can not delete a Christmas Task for another %s.",
                        ChatColor.YELLOW,
                        displayTimeLeft(timeLeft/1000)));
                return true;
            }
        }

        taskManager.deleteTask(p);
        filesManager.writeTask(p);

        p.sendMessage(String.format("%sYour current Christmas Task has been removed.", ChatColor.YELLOW));

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

    private boolean currentTask(Player p) {
        if (!taskManager.hasTask(p)) {
            p.sendMessage(String.format("%sYou currently do not have a Christmas Task.", ChatColor.YELLOW));
            return true;
        }

        TaskInstance currentTask = taskManager.readTask(p);
        if (currentTask instanceof AcquireTaskInstance)
            checkAcquireTask(p, (AcquireTaskInstance) currentTask);

        if (currentTask.isFinished())
            p.sendMessage(String.format("%sYour current Christmas Task is completed! Claim your reward with %s/xmastasks reward.", ChatColor.YELLOW, ChatColor.GREEN));
        else
            p.sendMessage(currentTask.progress());

        return true;
    }

    private boolean newTask(Player p) {
        if (taskManager.hasTask(p)) {
            p.sendMessage(String.format("%sYou currently already have a Christmas Task.", ChatColor.YELLOW));
            p.sendMessage(taskManager.readTask(p).display());
            return true;
        }

        taskManager.createTask(p);

        TaskInstance currentTask = taskManager.readTask(p);

        p.sendMessage(currentTask.display());
        if (currentTask instanceof AcquireTaskInstance)
            checkAcquireTask(p, (AcquireTaskInstance) currentTask);

        if (currentTask.isFinished())
            p.sendMessage(String.format("%sYour current Christmas Task is completed! Claim your reward with %s/xmastasks reward.", ChatColor.YELLOW, ChatColor.GREEN));

        return true;
    }

    private boolean help(Player p) {
        p.sendMessage(String.format("%s===============       %sCh%sri%sst%smas %sTa%ssk%ss      %s===============",ChatColor.WHITE,
                ChatColor.RED, ChatColor.GREEN,
                ChatColor.RED, ChatColor.GREEN,
                ChatColor.RED, ChatColor.GREEN,
                ChatColor.RED,ChatColor.WHITE));

        p.sendMessage(String.format("  %s%sCommands:", ChatColor.DARK_BLUE, ChatColor.BOLD));
        p.sendMessage(String.format("  %s/xmastasks help %s.. %s%sThis information page", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/xmastasks new %s.. %s%sAssign a new task", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/xmastasks current %s.. %s%sDisplay current christmas task", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/xmastasks delete %s.. %s%sRemove current task (5-minute cooldown)", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/xmastasks reward %s.. %s%sClaim reward after completing the task", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));
        p.sendMessage(String.format("  %s/xmastasks leaderboard %s.. %s%sSee player leaderboards", ChatColor.YELLOW, ChatColor.WHITE, ChatColor.ITALIC, ChatColor.BLUE));

        p.sendMessage(String.format("%s===============                                 ===============",ChatColor.WHITE));
        return true;
    }

    private void checkAcquireTask(Player p, AcquireTaskInstance taskInstance) {
        Inventory inv = p.getInventory();
        if (!inv.contains(taskInstance.getTask().getItemToAcquire()))
            return;

        Map<Integer, ItemStack> itemsBySlot = (Map<Integer, ItemStack>) inv.all(taskInstance.getTask().getItemToAcquire());
        if (taskInstance instanceof EnchantedItemAcquireTaskInstance) {

            checkEnchantedItemAcquireTask(itemsBySlot, (EnchantedItemAcquireTaskInstance) taskInstance);

        } else {

            Optional<Integer> amount = itemsBySlot.keySet().stream().map(slot -> itemsBySlot.get(slot).getAmount()).reduce(Integer::sum);
            if (amount.isPresent() && taskInstance.getAmount() <= amount.get()) {
                taskInstance.finish();
            }

        }
    }

    private void checkEnchantedItemAcquireTask(Map<Integer, ItemStack> itemsBySlot, EnchantedItemAcquireTaskInstance enchantedItemAcquireTaskInstance) {
        if (enchantedItemAcquireTaskInstance.getTask().getItemToAcquire() == Material.ENCHANTED_BOOK) {

            for (Integer slot : itemsBySlot.keySet()) {

                ItemStack item = itemsBySlot.get(slot);
                if (!item.hasItemMeta()) continue;

                EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) item.getItemMeta();

                Map<Enchantment, Integer> requireds = enchantedItemAcquireTaskInstance.getEnchantments();
                if (requireds.keySet().stream().allMatch(req -> bookMeta.getEnchantLevel(req) == requireds.get(req) || bookMeta.getStoredEnchantLevel(req) == requireds.get(req))) {
                    enchantedItemAcquireTaskInstance.finish();
                    return;
                }

            }

        } else {

            for (Integer slot : itemsBySlot.keySet()) {

                ItemStack enchantedItem = itemsBySlot.get(slot);
                if (allRequiredEnchantmentsMatch(enchantedItem, enchantedItemAcquireTaskInstance.getEnchantments())) {
                    enchantedItemAcquireTaskInstance.finish();
                    return;
                }

            }

        }
    }

    private boolean allRequiredEnchantmentsMatch(ItemStack item, Map<Enchantment, Integer> requireds) {
        return requireds.keySet().stream().allMatch(req -> item.getEnchantmentLevel(req) == requireds.get(req));
    }
}
