package de.ancash.dragonsimulator;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.ancash.craftinginjector.IRecipe;

public class CustomItems {

	static Inventory inv;
	
	public static Inventory getInv() {
		return CustomItems.inv;
	}
	
	static ArrayList<ItemStackUtil> itemStackUtils = new ArrayList<ItemStackUtil>();
	public static ArrayList<DragonSet> dragonSets = new ArrayList<DragonSet>();
	static ItemStack aotd;
	
	public static DragonSet getDragonSet(EnderDragonType type) {
		for(DragonSet ds : dragonSets) {
			if(ds.getType().equals(type)) return ds;
		}
		return null;
	}
	
	public static void loadInv() {		
		inv = Bukkit.createInventory(null, 54, "Custom Items");
		inv.addItem(Altar.eyeOfEnder);
		try {

			ItemStackUtil isutil = new ItemStackUtil(Material.getMaterial(DragonSimulator.getItemCfg().getString("aotd.material")), DragonSimulator.getItemCfg().getString("aotd.name").replace("&", "§"));
			ArrayList<String> lore = new ArrayList<String>();
			DragonSimulator.getItemCfg().getStringList("aotd.lore").forEach(str -> lore.add(str.replace("&", "§")));
			isutil.setLore(lore);
			
			
			isutil.setString("ability", DragonSimulator.getItemCfg().getString("aotd..ability"));
			
			aotd = isutil.getItem().clone();
			
			loadDragonArmor();
			
		} catch(NullPointerException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadDragonArmor() {
		
		dragonSets.add(createDragonSet("Protector", EnderDragonType.PROTECTOR));
		dragonSets.add(createDragonSet("Old", EnderDragonType.OLD));
		dragonSets.add(createDragonSet("Unstable", EnderDragonType.UNSTABLE));
		dragonSets.add(createDragonSet("Wise", EnderDragonType.WISE));
		dragonSets.add(createDragonSet("Young", EnderDragonType.YOUNG));
		dragonSets.add(createDragonSet("Strong"	, EnderDragonType.STRONG));
		dragonSets.add(createDragonSet("Superior", EnderDragonType.SUPERIOR));
		
		for(DragonSet ds : dragonSets) {
			ItemStack frag = ds.getFragment().clone();
			frag.setAmount(10);
			HashMap<Integer, ItemStack> helmet = new HashMap<Integer, ItemStack>();
			helmet.put(1, frag.clone());
			helmet.put(2, frag.clone());
			helmet.put(3, frag.clone());
			helmet.put(4, frag.clone());
			helmet.put(6, frag.clone());
			new IRecipe(ds.getHelmet().clone(), helmet, ds.getHelmet().getItemMeta().getDisplayName());
			HashMap<Integer, ItemStack> chest = new HashMap<Integer, ItemStack>();
			chest.put(1, frag.clone());
			chest.put(3, frag.clone());
			chest.put(4, frag.clone());
			chest.put(5, frag.clone());
			chest.put(6, frag.clone());
			chest.put(7, frag.clone());
			chest.put(8, frag.clone());
			chest.put(9, frag.clone());
			new IRecipe(ds.getChestplate().clone(), chest, ds.getChestplate().getItemMeta().getDisplayName());
			HashMap<Integer, ItemStack> leggings = new HashMap<Integer, ItemStack>();
			leggings.put(1, frag.clone());
			leggings.put(2, frag.clone());
			leggings.put(3, frag.clone());
			leggings.put(4, frag.clone());
			leggings.put(6, frag.clone());
			leggings.put(7, frag.clone());
			leggings.put(9, frag.clone());
			new IRecipe(ds.getLeggings().clone(), leggings, ds.getLeggings().getItemMeta().getDisplayName());
			HashMap<Integer, ItemStack> boots = new HashMap<Integer, ItemStack>();
			boots.put(1, frag.clone());
			boots.put(3, frag.clone());
			boots.put(4, frag.clone());
			boots.put(6, frag.clone());
			new IRecipe(ds.getBoots().clone(), boots, ds.getBoots().getItemMeta().getDisplayName());
		}
	}
	
	private static DragonSet createDragonSet(String type, EnderDragonType edtype) {
		
		ItemStackUtil helmet = new ItemStackUtil(Material.SKULL_ITEM, "§6" + type + " Dragon Helmet", 1, (byte) 3)
				.setUnbreakable(true)
				.setLore((ArrayList<String>) DragonSimulator.getItemCfg().getStringList("set." + type.toLowerCase() + ".helmet.lore"))
				.setTexture(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".helmet.texture"));
		ItemStackUtil chestplate = new ItemStackUtil(Material.LEATHER_CHESTPLATE, "§6" + type + " Dragon Chestplate")
				.setUnbreakable(true)
				.setLore((ArrayList<String>) DragonSimulator.getItemCfg().getStringList("set." + type.toLowerCase() + ".chestplate.lore"))
				.setColor(getColorByString(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".chestplate.color")) != null ? getColorByString(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".chestplate.color")) : getColorByRGB(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".chestplate.color")));
		ItemStackUtil leggings = new ItemStackUtil(Material.LEATHER_LEGGINGS, "§6" + type + " Dragon Leggings")
				.setUnbreakable(true)
				.setLore((ArrayList<String>) DragonSimulator.getItemCfg().getStringList("set." + type.toLowerCase() + ".leggings.lore"))
				.setColor(getColorByString(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".leggings.color")) != null ? getColorByString(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".leggings.color")) : getColorByRGB(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".leggings.color")));
		ItemStackUtil boots = new ItemStackUtil(Material.LEATHER_BOOTS, "§6" + type + " Dragon Boots")
				.setUnbreakable(true)
				.setLore((ArrayList<String>) DragonSimulator.getItemCfg().getStringList("set." + type.toLowerCase() + ".boots.lore"))
				.setColor(getColorByString(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".boots.color")) != null ? getColorByString(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".boots.color")) : getColorByRGB(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".boots.color")));
		ItemStackUtil fragment = new ItemStackUtil(Material.SKULL_ITEM, "§5" + type + " Dragon Fragment", 1, (byte) 3)
				.setUnbreakable(true)
				.setLore((ArrayList<String>) DragonSimulator.getItemCfg().getStringList("set." + type.toLowerCase() + ".fragment.lore"))
				.setTexture(DragonSimulator.getItemCfg().getString("set." + type.toLowerCase() + ".fragment.texture"));
		
		itemStackUtils.add(helmet);
		itemStackUtils.add(chestplate);
		itemStackUtils.add(leggings);
		itemStackUtils.add(boots);
		itemStackUtils.add(fragment);
		DragonSet ds = new DragonSet(helmet.getItem(), chestplate.getItem(), leggings.getItem(), boots.getItem(), fragment.getItem(), edtype);
		return ds;
	}

	private static Color getColorByRGB(String string) {
		return Color.fromRGB(Integer.valueOf(string.split(",")[0]), Integer.valueOf(string.split(",")[1]), Integer.valueOf(string.split(",")[2]));
	}

	private static Color getColorByString(String string) {
		switch (string.toLowerCase()) {
		case "aqua":
			return Color.AQUA;
		case "black":
			return Color.BLACK;
		case "blue":
			return Color.BLUE;
		case "fuchsia":
			return Color.FUCHSIA;
		case "gray":
			return Color.GRAY;
		case "green":
			return Color.GREEN;
		case "lime":
			return Color.LIME;
		case "maroon":
			return Color.MAROON;
		case "navy":
			return Color.NAVY;
		case "olive":
			return Color.OLIVE;
		case "orange":
			return Color.ORANGE;
		case "purple":
			return Color.PURPLE;
		case "red":
			return Color.RED;
		case "silver":
			return Color.SILVER;
		case "teal":
			return Color.TEAL;
		case "white":
			return Color.WHITE;
		case "yellow":
			return Color.YELLOW;
		default:
			break;
		}
		return null;
	}

	public static ItemStack getAOTD() {
		return aotd;
	}
}
