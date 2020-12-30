package de.ancash.dragonsimulator;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonManager {

	static ArrayList<Item> hiddenForJoiner = new ArrayList<Item>();
	public static ArrayList<Item> pickupMessage = new ArrayList<Item>();
	
	public static void startDragonTeleporter(final ArrayList<Location> dragonLocs,Altar altar) {
		
		if(dragonLocs.size() == 0) dragonLocs.add(altar.getEnderDragon().getLocation());
		altar.log("Starting Dragon Movement Manager");
		
		if(dragonLocs.size() == 0) return;
		altar.setStaying(false);
		new BukkitRunnable() {
			
			int i = 0;
			boolean staying = false;
			int timesincestaying = 0;
			int cooldown = 6*20;
			int check = 0;
			String name = altar.getEnderDragon().getCustomName();
			EnderDragon ed = altar.getEnderDragon();
			
			@Override
			public void run() {
				
				if(checkDead(altar, name)) this.cancel();
				
				if(!staying && ed.getLocation().distance(altar.getTarget().getLocation()) < 8) {
					Location loc = dragonLocs.get(i);
					ArmorStand s = newArmorStand(altar.getTarget().getCustomName(), loc, altar);
					altar.getTarget().remove();
					altar.setTarget(s);
 					i++;
 					check = 0;
 					new EntityTargetEvent(ed, altar.getTarget(), TargetReason.CUSTOM);
				}
				if(i >= dragonLocs.size()) i = 0;
				if(staying) {
					timesincestaying++;
					try {ed.teleport(altar.getStayLoc());} catch(IllegalArgumentException ae) {}
				}
				if(Math.random() <= 0.002 && !staying) {
					staying = true;
					altar.setStayLoc(altar.getEnderDragon().getLocation());
					if(Math.random() <= 0.333) {
						cooldown = 70;
						createLightningEffects(altar.getEnderDragon().getLocation(), 60, altar);
					} else {
						cooldown = 6 * 20;
						createFireBalls(altar, dragonLocs.get(i));
					}
					altar.setStaying(true);
				}
				if(staying && timesincestaying >= cooldown) {
					staying = false;
					timesincestaying = 0;
					altar.setStaying(false);
				}
				if(check >= 200 && !staying) {
					ArmorStand s = newArmorStand(altar.getTarget().getCustomName(), altar.getTarget().getLocation(), altar);
					altar.getTarget().remove();
					altar.setTarget(s);
					new EntityTargetEvent(ed, altar.getTarget(), TargetReason.CUSTOM);
					check = 0;
				}
				check++;
				altar.getFollower().teleport(ed);
			}
		}.runTaskTimer(DragonSimulator.getInstance(), 0, 1);
		
	}
	
	protected static void createFireBalls(Altar altar, Location fallback) {
		altar.log("Creating Fireballs");
		Player toLookAr = altar.getTopDamagerInRange();
		if((toLookAr == null && fallback == null) || altar.getStayLoc() == null) return;
		altar.setStayLoc(altar.getStayLoc().setDirection(toLookAr != null ? altar.getStayLoc().clone().toVector().subtract(toLookAr.getLocation().toVector()).normalize() : altar.getStayLoc().toVector().subtract(fallback.toVector()).normalize()));
		new BukkitRunnable() {
			
			int count = 0;
			
			@Override
			public void run() {
				altar.getEnderDragon().getWorld().playEffect(altar.getEnderDragon().getLocation().clone().add(altar.getEnderDragon().getLocation().getDirection().clone().multiply(-7)).add(0,0.75,0), Effect.MOBSPAWNER_FLAMES, 5);
				altar.getEnderDragon().getWorld().spawnEntity(altar.getEnderDragon().getLocation().clone().add(altar.getEnderDragon().getLocation().getDirection().clone().multiply(-7)).add(0,0.75,0), EntityType.FIREBALL);
				altar.log("Spawned Fireball");
				if(count >= 6) this.cancel();
				count++;
			}
		}.runTaskTimer(DragonSimulator.getInstance(), 0, 15);
	}

	protected static boolean checkDead(Altar altar, String name) {
		altar.setLastLoc(altar.getEnderDragon().getLocation());
		if(!altar.getEnderDragon().isDead()) return false;
		ArrayList<ArmorStand> as = new ArrayList<ArmorStand>();
		altar.log("Creating 2D Sphere");
		for(Location loc : Utils.generateSphere(altar.getEnderDragon().getLocation(), 6, false)) {
			ArmorStand uff = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
			uff.setVisible(false);
			uff.setMarker(false);
			uff.setMaxHealth(1000);
			uff.setHealth(1000);
			uff.setGravity(true);
			as.add(uff);
		}
		for(Player player : altar.getDamager().keySet()) {
			int place = altar.getPlayerDamagePosition(player);
			if(place == 1) {
				altar.addWeight(player, 300);
			}
			if(place == 2) {
				altar.addWeight(player, 250);
			}
			if(place == 3) {
				altar.addWeight(player, 200);
			}
			if(place >= 4 && place <= 7) {
				altar.addWeight(player, 1250);
			}
			if(place >= 8 && place <= 15) {
				altar.addWeight(player, 100);
			}
			if(place >= 16) {
				altar.addWeight(player, 75);
			}
		}
		altar.getTarget().remove();
		altar.getFollower().remove();
		Bukkit.getScheduler().runTaskLater(DragonSimulator.getInstance(),new Runnable() {
			
			@Override
			public void run() {
				for(Player player : altar.getAltarMiddlePoint().getWorld().getPlayers()) {
					if(altar.getFinalHitPlayer() != null) {
						player.sendMessage("§a§l---------------------------------------------");
						player.sendMessage("                       §6§l" + (name.replace("§c", "").toUpperCase()) + " DOWN!");
						player.sendMessage(" ");
						player.sendMessage("§a§l                  " + altar.getFinalHitPlayer().getDisplayName() + " §7dealt the final blow.");
						player.sendMessage("  ");
						Player first = altar.getFirstDamager();
						Player second = altar.getSecondDamager();
						Player third = altar.getThirdDamager();
						player.sendMessage("                    §e§l1st Damager §7- " + (first != null ? first.getDisplayName() + " §7- §e" + (Utils.round(altar.getPlayerDamage(first), 0) + "").replace(".0", "") : "§cN/A"));
						player.sendMessage("                  §6§l2nd Damager §7- " + (second != null ? second.getDisplayName() + " §7- §e" + (Utils.round(altar.getPlayerDamage(second), 0) + "").replace(".0", "") : "§cN/A"));
						player.sendMessage("                     §c§l3rd Damager §7- " + (third != null ? third.getDisplayName() + " §7- §e" + (Utils.round(altar.getPlayerDamage(third), 0) + "").replace(".0", "") : "§cN/A"));
						player.sendMessage("   ");
						player.sendMessage("                 §eYour Damage: §a" + (Utils.round(altar.getPlayerDamage(player), 0) + "").replace(".0", "") + " §7(Position #" + altar.getPlayerDamagePosition(player) + ")");
						//player.sendMessage("    ");
						player.sendMessage("§a§l---------------------------------------------");
					}
				}
				Altar.PortalFrames(altar.getPortalFrames());
				Buildings.placeAltar(altar.getAltarMiddlePoint().clone());
				Buildings.setEgg(altar.getEggBlocks());
				altar.clearDamager();
			}
		}, 1);
		rewards(altar, altar.getType(), as);
		altar.getWeights().clear();
		strangeCircleStuff(as);
		altar.setCanPlace(true);
		return true;
	}
	
	private static void rewards(Altar altar, EnderDragonType type, ArrayList<ArmorStand> ass) {
		altar.log(altar.getEnderDragon().getCustomName() + " is dead");
		for(Player p : altar.getDamager().keySet()) {
			altar.log(p.getDisplayName() + " dealt " + altar.getDamager().get(p) + " damage, has " + altar.getWeight(p) + " and got " + altar.getPlayerDamagePosition(p));
		}
		DragonSet d = CustomItems.getDragonSet(type);
		HashMap<Integer, ItemStack> rewards = new HashMap<Integer, ItemStack>();
		if(!type.equals(EnderDragonType.SUPERIOR)) rewards.put(450, CustomItems.getAOTD());
		rewards.put(400, d.getChestplate());
		rewards.put(350, d.getLeggings());
		rewards.put(325, d.getHelmet());
		rewards.put(300, d.getBoots());
		for(Player player : altar.getWeightsOrdered()) {
			int weight = altar.getWeight(player);
			ItemStack specialReward = null;
			int rnd = new Random().nextInt(100) + 1;
			
			try {
				if(weight >= 450 && rnd <= (altar.getPlacedEyes().get(player) <= 4 ? altar.getPlacedEyes().get(player) * 5 : 20) && rewards.containsKey(450)) {
					specialReward = CustomItems.getAOTD().clone();
					rewards.remove(450);
					weight = weight - 450;
				}
				
				if(specialReward == null && weight >= 400 && rewards.containsKey(400)) {
					specialReward = d.getChestplate().clone();
					rewards.remove(400);
					weight = weight - 400;
			
				}
				if(specialReward == null && weight >= 350 && rewards.containsKey(350)) {
					specialReward = d.getLeggings().clone();
					rewards.remove(350);
					weight = weight - 350;
				
				}
				if(specialReward == null && weight >= 325 && rewards.containsKey(325)) {
					specialReward = d.getHelmet().clone();
					rewards.remove(325);
					weight = weight - 325;
				}
				if(specialReward == null && weight >= 300 && rewards.containsKey(300)) {
					specialReward = d.getBoots().clone();
					rewards.remove(300);
					weight = weight - 300;					
				}
				
				if(specialReward != null) {
					altar.log(player.getDisplayName() + " got " + specialReward.getItemMeta().getDisplayName());
					Item i = player.getWorld().dropItem(ass.get(5).getLocation(), specialReward);
					pickupMessage.add(i);
					toggleItemVisibility(i, player, altar);
				}
			} catch(NullPointerException e) {}
			if(weight >= 22) {
				int i = weight/22;
				altar.log(player.getDisplayName() + " got " + i + " fragments");
				ItemStack frag = d.getFragment().clone();
				frag.setAmount(i);
				Item fragItem = player.getWorld().dropItem(ass.get(10).getLocation(), frag);
				pickupMessage.add(fragItem);
				toggleItemVisibility(fragItem, player, altar);
			}
		}
		altar.log("Closing...");
		altar.closeLogger();
		altar.getPlacedEyes().clear();
	}

	private static void toggleItemVisibility(Item i, Player p, Altar altar) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!player.equals(p)) {
				altar.log("Toggling item visibility (" + i.getItemStack().getItemMeta().getDisplayName() + ") for " + player.getDisplayName());
				DragonSimulator.getEntityHider().toggleEntity(player, i);
			}
		}
	}
	
	private static void strangeCircleStuff(ArrayList<ArmorStand> as) {
		HashMap<Location, Material> reset = new HashMap<Location, Material>();
		HashMap<Location, Byte> resetData = new HashMap<Location, Byte>();
		new BukkitRunnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				try {
					for(ArmorStand armor : as) {
						if(armor.isOnGround()) {
							Block b = armor.getLocation().clone().add(0,-1,0).getBlock();
							reset.put(b.getLocation(), b.getType());
							resetData.put(b.getLocation(), b.getData());
							b.setType(Material.STAINED_CLAY);
							b.setData((byte) 6);
							armor.remove();
							as.remove(armor);
						}
					}
				} catch(ConcurrentModificationException e) {}
				if(as.isEmpty()) {
					startBlockResetter(reset, resetData);
					this.cancel();
				}
			}
		}.runTaskTimer(DragonSimulator.getInstance(), 1, 0);
	}

	protected static void startBlockResetter(HashMap<Location, Material> reset, HashMap<Location, Byte> resetData) {
		Bukkit.getScheduler().runTaskLater(DragonSimulator.getInstance(), new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				for(Location loc : reset.keySet()) {
					loc.getBlock().setType(reset.get(loc));
					loc.getBlock().setData(resetData.get(loc));
				}
				
			}
		}, 20 * 30);
	}

	public static void createLightningEffects(Location loc, int duration, Altar altar) {
		altar.log("Creating Lightnings");
		for(Player player : loc.getWorld().getPlayers()) {
			if(player.getLocation().distance(loc) <= 70) {
				altar.addPlayerToLightningStrike(player);
				player.getLocation().getWorld().strikeLightningEffect(player.getLocation());
				int d = new Random().nextInt(300) + 400;
				altar.log("Damaged " + player.getDisplayName() + " for " + d + " damage");
				player.damage(d, altar.getFollower());
			}
		}
		new BukkitRunnable() {
			
			int i = duration/5;
			
			@Override
			public void run() {
				
				loc.getWorld().strikeLightningEffect(loc);
				altar.getEnderDragon().teleport(loc);
				altar.log("Spawned Lightning Effect");
				
				if(i <= 0) this.cancel();
				i--;
			}
		}.runTaskTimer(DragonSimulator.getInstance(), 0, 5);
	}
	
	public static ArmorStand newArmorStand(String name, Location loc, Altar altar) {
		altar.log("Creating New Target");
		ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setVisible(false);
		as.setMarker(true);
		as.setCustomName(name);
		as.setGravity(false);
		return as;
	}
	
}
