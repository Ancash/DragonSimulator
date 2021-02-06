package de.ancash.dragonsimulator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.tr7zw.nbtapi.NBTItem;

public class ItemStackUtil {

	private ItemStack item;
	private ItemMeta im;
	
	public ItemStackUtil(Material material, String name) {
		item = new ItemStack(material);
		this.im = this.item.getItemMeta();
		this.setDisplayName(name);
	}
	
	public ItemStackUtil(Material material, String name, int amount, byte data) {
		item = new ItemStack(material, amount, data);
		this.im = this.item.getItemMeta();
		this.setDisplayName(name);
		
	}
	
	public boolean isLeatherArmor() {
		if(this.item.getType().equals(Material.LEATHER_BOOTS) || this.item.getType().equals(Material.LEATHER_CHESTPLATE) || this.item.getType().equals(Material.LEATHER_HELMET) || this.item.getType().equals(Material.LEATHER_LEGGINGS)) {
			return true;
		}
		return false;
	}
	
	public ItemStackUtil setUnbreakable(boolean bol) {
		NBTItem nbt = new NBTItem(this.item);
		nbt.setBoolean("Unbreakablye", bol);
		this.item = nbt.getItem();
		return this;
	}
	
	public ItemStackUtil setString(String key, String value) {
		if(value == null) return this;
		NBTItem nbt = new NBTItem(getItem());
		nbt.setString(key, value);
		this.setItem(nbt.getItem());
		return this;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public boolean isSkull() {
		return this.item.getType().equals(Material.SKULL_ITEM);
	}
	
	public ItemStackUtil setAmount(int amount) {
		this.item.setAmount(amount);
		return this;
	}
	
	public ItemStackUtil setColor(Color c) {
		LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
		im.setColor(c);
		item.setItemMeta(im);
		return this;
	}
	
	public ItemStackUtil setLore(ArrayList<String> lore) {
		this.im = this.item.getItemMeta();
		this.im.setLore(lore);
		this.item.setItemMeta(this.im);
		return this;
	}
	
	public ItemStackUtil setDisplayName(String name) {
		this.im = item.getItemMeta();
		this.im.setDisplayName(name);
		this.item.setItemMeta(this.im);
		return this;
	}
	
	public ItemStackUtil setTexture(String texture) {
		SkullMeta hm = (SkullMeta) this.item.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", texture));
		try {
			Field field = hm.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			field.set(hm, profile);
		} catch(IllegalArgumentException  | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		this.item.setItemMeta(hm);
		return this;
	}

	public ItemStackUtil setItem(ItemStack item) {
		this.item = item;
		return this;
	}
}
