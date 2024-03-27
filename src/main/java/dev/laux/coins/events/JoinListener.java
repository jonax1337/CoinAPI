package dev.laux.coins.events;

import dev.laux.coins.Coins;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            // Hier wird der Benutzer in der Datenbank sichergestellt
            Coins.getInstance().getCoinAPI().ensurePlayerCoins(event.getPlayer().getUniqueId());
            Coins.getInstance().getCoinAPI().ensurePlayerRewards(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
