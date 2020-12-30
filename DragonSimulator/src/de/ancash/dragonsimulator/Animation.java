package de.ancash.dragonsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Animation {
	
	@SuppressWarnings("unchecked")
	public static void startAnimation(Altar altar) {
		altar.log("Initializing animation...");
		startEyeAnimation((ArrayList<Location>) altar.getPortalFrames().clone(), altar);
	}
	
	@SuppressWarnings("deprecation")
	public static void startEyeAnimation(ArrayList<Location> portalFramesLocs, Altar altar) {

		altar.log("Initializing Summoning Eye animation...");
		
		ArrayList<ArmorStand> eyes = new ArrayList<ArmorStand>();
		for(Location loc : portalFramesLocs) {
			 
			
			ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0.5, -1, 0.5), EntityType.ARMOR_STAND);
			as.setVisible(false);
			as.setMarker(true);
			as.getEyeLocation().setYaw(as.getEyeLocation().getYaw() - 90);;
			as.setGravity(false);
			as.setHelmet(Altar.rotatedEyeOfEnder.clone());
			eyes.add(as);
			loc.getBlock().setData((byte) 2);
		}
		new BukkitRunnable() {
			
			double y = 0;
			
			@Override
			public void run() {
				for(ArmorStand as : eyes) {
					as.teleport(as.getLocation().add(0,0.05,0));
				}
				
				if(y >= 4) {
					for(ArmorStand as : eyes) {
						as.remove();
					}
					try {
						altar.getAltarMiddlePoint().getWorld().playSound(altar.getAltarMiddlePoint(), Sound.ENDERDRAGON_DEATH, 1, 2);
					} catch(IllegalArgumentException | NoSuchFieldError exc) {
						
						altar.getAltarMiddlePoint().getWorld().playSound(altar.getAltarMiddlePoint(), Sound.valueOf("ENTITY_ENDER_DRAGON_DEATH"), 1, 2);
					}
					startBlockAnimation(altar);
					this.cancel();
				}
				y += 0.05;
			}
		}.runTaskTimer(DragonSimulator.getInstance(), 0, 0);
	}
	
	public static void startBlockAnimation(Altar altar) {

		altar.log("Initializing Block animation...");
		Location loc = altar.getAltarMiddlePoint().clone();
		loc.add(1, 1, 0);
		new BukkitRunnable() {
			
			int i = 1;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				loc.getBlock().setType(Material.AIR);
				loc.getWorld().getBlockAt(loc.clone().add(0, -1, 0)).setType(Material.AIR);
				loc.getWorld().getBlockAt(loc.add(0, 0, 0)).setType(Material.STAINED_GLASS);
				loc.getBlock().setData((byte) 10);
				loc.getWorld().getBlockAt(loc.add(0, 1, 0)).setType(Material.SEA_LANTERN);
				if(i > 30) {
					loc.getBlock().setType(Material.AIR);
					loc.getWorld().getBlockAt(loc.clone().add(0, -1, 0)).setType(Material.AIR);
					Bukkit.getScheduler().runTaskLater(DragonSimulator.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							startEggAnimation(altar.getAltar(), loc);							
						}
					}, 10);
					this.cancel();
				}
				i++;
			}
		}.runTaskTimer(DragonSimulator.getInstance(), 0, 3);
		
	}

	public static void startEggAnimation(Altar altar, Location loc) {

		createExplosion(loc, 10, altar);
		altar.log("Initializing Egg animation...");
		new BukkitRunnable() {
			
			int i = 0;
			
			@Override
			public void run() {
				try {
					loc.clone().add(0, i, 0).getWorld().playEffect(loc.clone().add(0, i, 0), Effect.EXPLOSION_HUGE, 2);
				} catch(IllegalArgumentException | NoSuchFieldError exc) {
				}
				if(i >= 4) {
					spawnDragon(altar, loc);
					this.cancel();
				}
				i++;
			}
		}.runTaskTimer(DragonSimulator.getInstance(), 2, 2);
	}
	
	@SuppressWarnings("deprecation")
	public static void createExplosion(Location location, int radius, Altar a) {
		a.log("Initializing Explosion...");
        List<Block> blocks = new ArrayList<>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                	if(location.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR)) continue;
                	blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        a.clearEggBlockMap();
        for(Block block : blocks) {
            float x = -2.0F + (float)(Math.random() * 5.0D);

            float y = -3.0F + (float)(Math.random() * 6.0D);

            float z = -2.0F + (float)(Math.random() * 5.0D);
            
            HashMap<Material, Byte> mats = new HashMap<Material, Byte>();
            mats.put(block.getType(), block.getData());
            a.getEggBlocks().put(block.getLocation(), mats);
            
            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
            block.setType(Material.AIR);
            fallingBlock.setVelocity(new Vector(x, y, z));
            fallingBlock.setDropItem(false);
            }
    }
	
	public static void spawnDragon(Altar altar, Location loc) {
		ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0,10,0), EntityType.ARMOR_STAND);
		as.setCustomName("Altar: " + altar.getUUID().toString());
		as.setGravity(false);
		as.setMarker(true);
		altar.setTarget(as);
		EnderDragon ed = (EnderDragon) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
		altar.setDragon(ed);
		
		EnderDragonType type = newEnderDragonType();
		setHealthForDragonType(type, ed);
		altar.setType(type);
		
		ArmorStand follower = DragonManager.newArmorStand("follower", ed.getLocation(), altar);
		follower.setVisible(false);
		altar.setFollower(follower);
		
		new EntityTargetEvent(ed, altar.getTarget(), TargetReason.CUSTOM);
		
		altar.log("Spawning new Dragon: " + ed.getCustomName());
		DragonManager.startDragonTeleporter(altar.getDragonLocs(), altar);
	}

	private static void setHealthForDragonType(EnderDragonType type, EnderDragon ed) {
		switch (type) {
		case OLD:
			ed.setMaxHealth(15000000);
			ed.setHealth(15000000);
			ed.setCustomName("§cOld Dragon");
			return;
		case PROTECTOR:
			ed.setMaxHealth(9000000);
			ed.setHealth(9000000);
			ed.setCustomName("§cProtector Dragon");
			return;
		case WISE:
			ed.setMaxHealth(9000000);
			ed.setHealth(9000000);
			ed.setCustomName("§cWise Dragon");
			return;
		case UNSTABLE:
			ed.setMaxHealth(9000000);
			ed.setHealth(9000000);
			ed.setCustomName("§cUnstable Dragon");
			return;
		case YOUNG:
			ed.setMaxHealth(7500000);
			ed.setHealth(7500000);
			ed.setCustomName("§cYoung Dragon");
			return;
		case STRONG:
			ed.setMaxHealth(9000000);
			ed.setHealth(9000000);
			ed.setCustomName("§cStrong Dragon");
			return;
		case SUPERIOR:
			ed.setMaxHealth(12000000);
			ed.setHealth(12000000);
			ed.setCustomName("§cSuperior Dragon");
			return;
		default:
			break;
		}
	}

	private static EnderDragonType newEnderDragonType() {
		int random = new Random().nextInt(100) + 1;
		if(random <= 16) return EnderDragonType.PROTECTOR;
		if(random <= 32) return EnderDragonType.OLD;
		if(random <= 48) return EnderDragonType.WISE;
		if(random <= 64) return EnderDragonType.UNSTABLE;
		if(random <= 80) return EnderDragonType.YOUNG;
		if(random <= 96) return EnderDragonType.STRONG;
		return EnderDragonType.SUPERIOR;
	}
}
