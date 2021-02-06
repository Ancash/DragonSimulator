package de.ancash.dragonsimulator;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RightClickAltar implements Listener {

	static ArrayList<Player> cooldown = new ArrayList<Player>();
	
	@EventHandler(ignoreCancelled = false)
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof EnderDragon && e.getDamager() instanceof Player) {
			e.setCancelled(true);
			Bukkit.getScheduler().runTaskLater(DragonSimulator.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					CustomDragonDamageEvent dde = new CustomDragonDamageEvent((Player) e.getDamager(),(EnderDragon) e.getEntity(), e.getDamage());
					Bukkit.getServer().getPluginManager().callEvent(dde);
				}
			}, 0);
			return;
		} 
		if(e.getEntity() instanceof EnderDragon && e.getDamager() instanceof Arrow && ((Arrow)e.getDamager()).getShooter() instanceof Player) {
			e.setCancelled(true);
			Bukkit.getScheduler().runTaskLater(DragonSimulator.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					CustomDragonDamageEvent dde = new CustomDragonDamageEvent((Player) ((Arrow)e.getDamager()).getShooter(),(EnderDragon) e.getEntity(), e.getDamage());
					Bukkit.getServer().getPluginManager().callEvent(dde);
				}
			}, 0);
			return;
		} 
		if(e.getDamager() instanceof ArmorStand) {
			if(e.getDamager().getCustomName() != null && e.getDamager().getCustomName().equals("follower")) {
				Altar altar = Altar.getAltar((ArmorStand)e.getDamager());
				if(altar == null) return;
				Player player = (Player) e.getEntity();
				if(altar.hasLightningStrike(player)) {
					player.sendMessage("§c" + (altar.getEnderDragon().getCustomName() == null ? "Ender Dragon" : altar.getEnderDragon().getCustomName()) + " §5used §eLightning Strike §5on you for §c" + Utils.round(e.getDamage(), 1) + " damage.");
					altar.removePlayerFromLightningStrike(player);
					return;
				}
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDragonDamage(CustomDragonDamageEvent e) {
		if(e.isValid()) {
			Player p = e.getDamager();
			Altar altar = null;
			for(Entity entity : e.getDragon().getNearbyEntities(2, 2, 2)) {
				if(entity.getCustomName() != null) {
					if(entity.getCustomName().equals("follower")) {
						Altar alt = Altar.getAltar((ArmorStand) entity);
						if(alt == null) continue;
						altar = alt;
						break;
					}
				}
			}
			if(altar != null) {
				setHealthDragon(e.getDragon(), e.getDamage(), p);
				altar.addPlayerDamage(e.getDamage(), p);
				altar.log("-" + Utils.round(e.getDamage(), 1) + " by " + p.getDisplayName() + ". Now " + Utils.round(altar.getEnderDragon().getHealth(), 1)+ "/" + altar.getEnderDragon().getMaxHealth());
				altar.setFinalHitPlayer(p);
			}
		}
	}
	
	private void setHealthDragon(EnderDragon ed, double damage, Player killer) {
		if(ed.getHealth() - damage <= 0) ed.setHealth(0);
		if(ed.getHealth() - damage >= 0) ed.setHealth(ed.getHealth() - damage);
	}
	
	@SuppressWarnings({ "deprecation" })
	@EventHandler
	public void onRighClickEndFrame(PlayerInteractEvent e) {
		
		if(e.getClickedBlock() == null) return;
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		if(p.getItemInHand() != null && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().getDisplayName().equals("§5Summoning Eye")) e.setCancelled(true);
		if(!b.getType().equals(Material.ENDER_PORTAL_FRAME)) return;
		
		ItemStack is = p.getItemInHand();
		Altar a = Altar.getAltar(b.getLocation());
		if(a == null) {
			return;
		} else {
			e.setCancelled(true);
		}
		if((b.getData() + "").equals("6")) {
			removeEye(p, a, b);
			return;
		}
		if(is == null) return;
		if(!is.hasItemMeta()) return;
		if(!is.getItemMeta().getDisplayName().equals("§5Summoning Eye")) return;
		if((b.getData() + "").equals("2") && !cooldown.contains(p) && a.canPlace()) {
			b.setData((byte) 6);
			if(p.getItemInHand().getAmount() == 1) {
				p.setItemInHand(null);
			} else {
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
			}
			a.addPlacedEyes(p);
			int eyes = 0;
			for(Location loc : a.getPortalFrames()) {
				if((loc.getBlock().getData() + "").equals("6")) {
					eyes++;
				}
			}
			for(Player all : p.getWorld().getPlayers()) {
				all.sendMessage("§5»§r " + p.getDisplayName() + " §dplaced a summoning eye §7(§e" + eyes + "§7/§a8§7)");

				try {
					all.playSound(all.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 2);
				} catch(IllegalArgumentException | NoSuchFieldError exc) {
					all.playSound(all.getLocation(), Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"), 1, 2);
				}
				
			}
			Bukkit.getScheduler().runTaskLater(DragonSimulator.getInstance(), new Runnable() {
				
				@Override
				public void run() {

					cooldown.remove(p);
					
				}
			}, 50);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void removeEye(Player p, Altar a, Block b) {
		if(a.getPlacedEyes().containsKey(p)) {
			for(Player all : p.getWorld().getPlayers()) {
				all.sendMessage("§5»§r " + p.getDisplayName() + " §dtook his eye");
			}
			b.setData((byte) 2);
			a.removePlacedEyes(p);
			p.getInventory().addItem(Altar.eyeOfEnder.clone());
		}
	}
}
