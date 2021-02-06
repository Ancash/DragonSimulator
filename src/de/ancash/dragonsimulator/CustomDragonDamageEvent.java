package de.ancash.dragonsimulator;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomDragonDamageEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private EnderDragon dragon;
	private double damage;
	private Player attacker;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public CustomDragonDamageEvent(Player attacker, EnderDragon dragon, double damage) {
		this.dragon = dragon;
		this.damage = damage;
		this.attacker = attacker;
	}
	
	public Player getDamager() {
		return this.attacker;
	}
	
	public double getDamage() {
		return this.damage;
	}
	
	public boolean isValid() {
		for(Entity entity : dragon.getNearbyEntities(2, 2, 2)) {
			if(entity.getCustomName() != null) {
				if(entity.getCustomName().equals("follower")) {
					Altar alt = Altar.getAltar((ArmorStand) entity);
					if(alt == null) continue;
					return true;
				}
			}
		}
		return false;
	}
	
	public EnderDragon getDragon() {
		return this.dragon;
	}
}
