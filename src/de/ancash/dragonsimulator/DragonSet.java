package de.ancash.dragonsimulator;

import org.bukkit.inventory.ItemStack;

public class DragonSet {

	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack fragment;
	private EnderDragonType type;
	
	public DragonSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack fragment, EnderDragonType type) {
		this.helmet = helmet;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		this.fragment = fragment;
		this.type = type;
	}
	
	public EnderDragonType getType() {
		return this.type;
	}
	public ItemStack getHelmet() {
		return this.helmet;
	}
	public ItemStack getChestplate() {
		return this.chestplate;
	}
	public ItemStack getLeggings() {
		return this.leggings;
	}
	public ItemStack getBoots() {
		return this.boots;
	}
	public ItemStack getFragment() {
		return this.fragment;
	}
	
	
	
}
