package ee.kmtster.xmastasks;

import org.bukkit.plugin.java.JavaPlugin;

public class XmasTasksPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        getServer().getConsoleSender().sendMessage("Enabled Christmas Tasks plugin.");
    }
}
