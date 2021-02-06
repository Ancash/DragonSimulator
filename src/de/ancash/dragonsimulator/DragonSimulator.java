package de.ancash.dragonsimulator;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.ancash.dragonsimulator.EntityHider.Policy;

public class DragonSimulator extends JavaPlugin{

	private static DragonSimulator plugin;
	private static File config;
	private static FileConfiguration cfg;
	private static EntityHider entityHider;
	private static File itemFile;
	private static FileConfiguration itemConfig;
	
	public void onEnable() {
		plugin = this;
		
		
		//NMSDragonControllPhase.setDragonControllPhase(new Location(Bukkit.getWorld("world"), 10, 10, 10));
		
		entityHider = new EntityHider(plugin, Policy.BLACKLIST);
		
		config = new File("plugins/DragonSimulator/config.yml");
		if(!config.exists()) {
			/*try {
				Utils.loadFile(plugin.getResource("ressources/config.yml"), config);
			} catch (IOException | InvalidConfigurationException e) {
//				e.printStackTrace();
			}*/
		}
		cfg = YamlConfiguration.loadConfiguration(config);
		
		itemFile = new File("plugins/DragonSimulator/drops.yml");
		if(!itemFile.exists()) {
			try {
				Utils.loadFile(plugin.getResource("resources/drops.yml"), itemFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		itemConfig = YamlConfiguration.loadConfiguration(itemFile);
		
		getCommand("setaltar").setExecutor(new SetAltarCMD());
		getCommand("se").setExecutor(new SECMD());
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		pm.registerEvents(new Altar(), plugin);
		pm.registerEvents(entityHider, plugin);
		pm.registerEvents(new RightClickAltar(), plugin);
		
		if(!new File("plugins/DragonSimulator/logs").exists()) {
			new File("plugins/DragonSimulator/logs").mkdir();
		}
		
		Altar.loadAltars(cfg);
		CustomItems.loadInv();
	}
	
	public void onDisable() {
		entityHider.close();
		entityHider = null;
	}
	
	public static EntityHider getEntityHider() {
		return entityHider;
	}
	
	public static FileConfiguration getCfg() {
		return cfg;
	}
	public static File getCfgFile() {
		return config;
	}
	
	public static FileConfiguration getItemCfg() {
		return itemConfig;
	}
	public static File getItemFile() {
		return itemFile;
	}
	
	public static Plugin getInstance() {
		return plugin;
	}
}
