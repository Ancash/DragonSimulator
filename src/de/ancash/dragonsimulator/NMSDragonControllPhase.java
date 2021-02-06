package de.ancash.dragonsimulator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;

import net.minecraft.server.v1_16_R3.EntityEnderDragon;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class NMSDragonControllPhase {

	public static EnderDragon setDragonControllPhase(Location loc) {
		try {
			Object craftWorld = getCraftWorld(loc.getWorld());
			Object nmsWorld = Utils.getNMSClass("World").cast(craftWorld.getClass().getDeclaredMethod("getHandle").invoke(craftWorld));
			Object entityEnderDragon = null;
			net.minecraft.server.v1_16_R3.EntityEnderDragon dt = new EntityEnderDragon(EntityTypes.ENDER_DRAGON, (net.minecraft.server.v1_16_R3.World) Utils.getNMSClass("World").cast(craftWorld.getClass().getDeclaredMethod("getHandle").invoke(craftWorld)));
			
			try {
				entityEnderDragon = Utils.getNMSClass("EntityEnderDragon").getClass().getDeclaredConstructor(nmsWorld.getClass()).newInstance(nmsWorld);
			} catch (NoSuchMethodException | InstantiationException nsm) {
				try {
					//Object enderDragonType = Utils.getNMSClass("EntityTypes");
					dt.getClass().getSuperclass();
					for(Field f : dt.getClass().getSuperclass().getDeclaredFields()) {
						f.setAccessible(true);
						System.out.println(f.getName());
					}
					
 					entityEnderDragon = Utils.getNMSClass("EntityEnderDragon").getClass().getDeclaredConstructor(Utils.getNMSClass("EntityTypes").getClass().getSuperclass(), ((net.minecraft.server.v1_16_R3.World) nmsWorld).getClass()).newInstance((net.minecraft.server.v1_16_R3.World) nmsWorld);
				} catch (NoSuchMethodException | InstantiationException nsm2) {
					nsm2.printStackTrace();
				}
			}
			System.out.println(entityEnderDragon);
			Method cT = entityEnderDragon.getClass().getDeclaredMethod("cT");
			Object dragonControllerPhase = null;
			for(Object e : Utils.getNMSClass("DragonControllerPhase").getClass().getEnumConstants()) {
				if(e.toString().toLowerCase().equals("a")) {
					dragonControllerPhase = e;
					break;
				}
			}
			Method a = cT.getClass().getDeclaredMethod("a", dragonControllerPhase.getClass());
			a.invoke(cT.invoke(entityEnderDragon), dragonControllerPhase);
			//ragon.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), w.random.nextFloat() * 360.0F, 0.0F);
			entityEnderDragon.getClass().getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityEnderDragon, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			nmsWorld.getClass().getDeclaredMethod("addEntity", Utils.getNMSClass("Entity").getClass()).invoke(nmsWorld, entityEnderDragon);
			return (EnderDragon) entityEnderDragon;
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
		//dragon.cT().a(DragonControllerPhase.a);
	}
	
	public static Object getCraftWorld(World w) {
		return Utils.getCraftBukkitClass("CraftWorld").cast(w);
	}
	
	public static Object getNMSWorld(World w) {
		Object craftWorld = getCraftWorld(w);
		Method m = null;
		try {
			m = craftWorld.getClass().getMethod("getHandle");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		Object nmsWorld = null;
		try {
			nmsWorld = Utils.getNMSClass("World").cast(m.invoke(craftWorld));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return nmsWorld;
	}
}
