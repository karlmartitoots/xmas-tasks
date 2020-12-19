package ee.kmtster.xmastasks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent e) {
        e.getCaught();
    }
}
