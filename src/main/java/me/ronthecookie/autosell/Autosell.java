package me.ronthecookie.autosell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.ronthecookie.autosell.commands.AutosellCommand;
import me.ronthecookie.autosell.commands.SellHandCommand;
import net.milkbowl.vault.economy.Economy;

public class Autosell extends JavaPlugin implements Listener {
	private HashMap<Material, Integer> prices = new HashMap<>();

	@Getter
	private static Autosell instance;
	@Getter
	private static Economy econ = null;
    @Override
    public void onEnable() {
		instance = this;
		registerEvents(this);
		this.saveDefaultConfig();
		if (!setupEconomy()) {
			getLogger().severe("Couldn't start because no Vault economy plugin was found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		Map<String, Object> oPrices = getConfig().getConfigurationSection("prices").getValues(false);
		oPrices.forEach((k, v) -> {
			prices.put(Material.valueOf(k), (Integer) v);
		});

		PaperCommandManager manager = new PaperCommandManager(this);
		manager.registerCommand(new AutosellCommand());
		manager.registerCommand(new SellHandCommand());

    }
    @Override
    public void onDisable() {
    }
	
	public void registerEvents(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		// Debug
		e.getPlayer().sendMessage(prices.toString());
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public double getWorth(ItemStack item, Player p) {
		if (!prices.containsKey(item.getType())) return 0;

		double[] multipliers = p.getEffectivePermissions().stream().filter(perm -> {
			return perm.getPermission().startsWith("autosell.multiplier");
		}).mapToDouble(perm -> {
			return Double.valueOf(perm.getPermission().replace("autosell.multiplier.", ""));
		}).sorted().toArray();

		return prices.get(item.getType()) * (multipliers.length == 0 ? 1 : multipliers[multipliers.length - 1]);
	}
}