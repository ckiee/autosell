package me.ronthecookie.autosell.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.ronthecookie.autosell.Autosell;
import net.milkbowl.vault.economy.EconomyResponse;

@CommandPermission("autosell.autosell")
@CommandAlias("autosell")
public class AutosellCommand extends BaseCommand implements Listener {
	private Set<UUID> autoSellEnabled = new HashSet();
	private Autosell autosell = Autosell.getInstance();
	{
		Autosell.getInstance().registerEvents(this);
	}
    @CatchUnknown
    @Default
    public void onDefault(CommandSender sender, String[] message) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command");
            return;
        }
		Player player = (Player) sender;
		if (autoSellEnabled.contains(player.getUniqueId())) {
			autoSellEnabled.remove(player.getUniqueId());
			player.sendMessage(ChatColor.YELLOW + "Autosell disabled.");
		} else {
			autoSellEnabled.add(player.getUniqueId());
			player.sendMessage(ChatColor.YELLOW + "Autosell enabled.");
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (e.isDropItems()) {
			e.getBlock().getDrops().forEach(item -> {
				autosell.getEcon().depositPlayer(p, autosell.getWorth(item, p));
			});
			e.setDropItems(false);
		}
	}
}