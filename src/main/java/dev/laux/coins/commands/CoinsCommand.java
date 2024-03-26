package dev.laux.coins.commands;

import dev.laux.coins.Coins;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            try {
                // Hole den aktuellen Coin-Stand des Spielers und zeige ihn an
                int coins = Coins.getInstance().getCoinAPI().getCoins(player.getUniqueId());
                player.sendMessage("§7Du besitzt aktuell §a" + coins + " §7Coins.");
            } catch (Exception e) {
                player.sendMessage("Es gab einen Fehler beim Abrufen deiner Coins. Bitte versuche es später erneut.");
                e.printStackTrace();
            }
        } else {
            sender.sendMessage("Nur Spieler können diesen Befehl ausführen.");
        }
        return true;
    }

}
