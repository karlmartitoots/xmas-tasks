package ee.kmtster.xmastasks.commands;

import ee.kmtster.xmastasks.XmasTaskManager;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ee.kmtster.xmastasks.XmasTaskManager.present;

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
        if (currentTask instanceof AcquireTaskInstance)
            checkAcquireTask(p, (AcquireTaskInstance) currentTask);

        if (!currentTask.isFinished()) {
            p.sendMessage(String.format("%sYour current Christmas Task is not finished yet.", ChatColor.YELLOW));
            p.sendMessage(currentTask.display());
        } else {
            p.sendMessage(String.format("%sWell done! Santa has given you a reward for completing the task.", ChatColor.YELLOW));

            p.getWorld().dropItem(p.getLocation(), present());

            deleteTask(p);
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
        if (currentTask instanceof AcquireTaskInstance)
            checkAcquireTask(p, (AcquireTaskInstance) currentTask);

        if (currentTask.isFinished())
            p.sendMessage(String.format("%sYour current Christmas Task is completed! Claim your reward with %s/xmastasks reward.", ChatColor.YELLOW, ChatColor.GREEN));
        else
            p.sendMessage(currentTask.display());

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
