package de.ancash.dragonsimulator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Utils {

	public static ItemStack setTexture(ItemStack pet, String texture) {
		SkullMeta hm = (SkullMeta) pet.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", texture));
		try {
			Field field = hm.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			field.set(hm, profile);
		} catch(IllegalArgumentException  | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		pet.setItemMeta(hm);
		return pet;
	}
	
	public static double round(final double value, final int frac) {
        return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac);
    }
	
	public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {
	     
        List<Location> circleBlocks = new ArrayList<Location>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();
     
        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                 
                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));
                 
                    if(distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                     
                        Location l = new Location(centerBlock.getWorld(), x, centerBlock.getY(), z);
                     
                        if(!circleBlocks.contains(l)) circleBlocks.add(l);
                    }
                 
                }
            }
        }
     
        return circleBlocks;
    }
	
	public static void loadFile(InputStream paramInputStream, File paramFile) throws IOException, InvalidConfigurationException {

		if(!paramFile.exists()) {
			FileUtils.copyInputStreamToFile(paramInputStream, paramFile);
		}
		((FileConfiguration)YamlConfiguration.loadConfiguration(paramFile)).load(paramFile);
	}
	
	public static Class<?> getNMSClass(String name) {
		try {
			return Class.forName("net.minecraft.server." + getVersion() + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Class<?> getCraftBukkitClass(String name) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String getVersion() {
    	return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
}
