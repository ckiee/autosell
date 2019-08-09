package me.ronthecookie.autosell.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.ronthecookie.autosell.Autosell;

@CommandPermission("autosell.sellhand")
@CommandAlias("sellhand")
public class SellHandCommand extends BaseCommand {
	private Autosell autosell = Autosell.getInstance();
    @CatchUnknown
    @Default
    public void onDefault(CommandSender sender, String[] message) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command");
            return;
        }
		Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();
		double worth = autosell.getWorth(item, player);;
		autosell.getEcon().depositPlayer(player, worth);
		player.getInventory().setItemInMainHand(null);
		player.sendMessage(ChatColor.YELLOW + "Sold hand for $" + String.valueOf(worth) + ".");
    }
}