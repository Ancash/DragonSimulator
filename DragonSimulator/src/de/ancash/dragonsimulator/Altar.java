package de.ancash.dragonsimulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Altar implements Listener{
	
	private FileWriter fw;
	
	public static ArrayList<Altar> altars = new ArrayList<Altar>();
	public static ItemStack eyeOfEnder = new ItemStack(Material.SKULL_ITEM, 1 , (byte) 3);
	public static ItemStack rotatedEyeOfEnder = new ItemStack(Material.SKULL_ITEM,1 ,(byte)3);
	
	private HashMap<Location, HashMap<Material, Byte>> egg = new HashMap<Location, HashMap<Material,Byte>>();
	private Location middlePoint;
	private UUID uuid;
	private ArrayList<Location> portalFrames;
	private HashMap<Player, Integer> placedEyes = new HashMap<Player, Integer>();
	private boolean canPlace = true;
	private ArmorStand target;
	private ArrayList<Location> dragonLocs = new ArrayList<Location>();
	private EnderDragon ed;
	private ArmorStand follower;
	private ArrayList<Player> lightningStrike = new ArrayList<Player>();
	private HashMap<Player, Double> damager = new HashMap<Player, Double>();
	private Player finalHit;
	private boolean staying;
	private Location stayLoc;
	private HashMap<Player, Integer> weights = new HashMap<Player, Integer>();
	private EnderDragonType type;
	private Location lastLoc;
	
	public void log(String msg) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
	    Date date = new Date();  
		try {
			fw.write("[" + formatter.format(date) + " INFO]: " + msg + "\n");
		} catch (IOException | NullPointerException e) {}
	}
	
	public void newLogger() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");  
	    Date date = new Date();  
		try {
			fw = new FileWriter(new File("plugins/DragonSimulator/logs/" + formatter.format(date) + ".log"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setLastLoc(Location loc) {
		this.lastLoc = loc;
	}
	
	public Location getLastLoc() {
		return this.lastLoc;
	}
	
	public EnderDragonType getType() {
		return this.type;
	}

	public void setType(EnderDragonType type) {
		this.type = type;
	}
	
	public ArrayList<Player> getWeightsOrdered(){
		ArrayList<Player> ordered = new ArrayList<Player>();
		ArrayList<Integer> allWeights = new ArrayList<Integer>();
		HashMap<Integer, Player> reverse = new HashMap<Integer, Player>();
		for(Player player : weights.keySet()) {
			reverse.put(weights.get(player), player);
			allWeights.add(weights.get(player));
		}
		Collections.sort(allWeights);
		ArrayList<Integer> reverseWeight = new ArrayList<Integer>();
		for(int i = allWeights.size()-1; i>=0; i--) {
			reverseWeight.add(allWeights.get(i));
		}
		for(int i : reverseWeight) {
			ordered.add(reverse.get(i));
		}
		return ordered;
	}
	
	public HashMap<Player, Integer> getWeights(){
		return this.weights;
	}
	
	public int getWeight(Player p) {
		return weights.containsKey(p) ? weights.get(p) : 0;
	}
	
	public HashMap<Player, Double> getDamager(){
		return this.damager;
	}
	
	public void addWeight(Player player, int weight) {
		this.weights.put(player, this.weights.containsKey(player) ? this.weights.get(player) + weight : weight);
	}
	
	public Location getStayLoc() {
		return this.stayLoc;
	}
	
	public void setStayLoc(Location loc) {
		this.stayLoc = loc;
	}
	
	public Player getTopDamagerInRange() {
		Player p = null;
		for(Player all : damager.keySet()) {
			if(p == null) {
				p = all;
				continue;
			}
			if(damager.get(all) > damager.get(p) && this.getEnderDragon().getLocation().distance(p.getLocation()) < 50) p = all;
		}
		return p;
	}
	
	public void setStaying(boolean bol) {
		this.staying = bol;
	}
	
	public boolean isStaying() {
		return this.staying;
	}
	
	public void setFinalHitPlayer(Player player) {
		this.finalHit = player;
	}
	
	public Player getFinalHitPlayer() {
		return this.finalHit;
	}
	
	public boolean addPlayerDamage(double damage, Player player) {
		this.damager.put(player, this.damager.containsKey(player) ? this.damager.get(player) + damage : damage);
		if(this.getEnderDragon().isDead()) return true;
		return false;
	}
	
	public double getPlayerDamage(Player player) {
		return this.damager.containsKey(player) ? this.damager.get(player) : 0;
	}
	
	public void addPlayerToLightningStrike(Player p) {
		this.lightningStrike.add(p);
	}
	
	public void removePlayerFromLightningStrike(Player p) {
		this.lightningStrike.remove(p);
	}
	
	public boolean hasLightningStrike(Player p) {
		return this.lightningStrike.contains(p);
	}
	
	public void setDragon(EnderDragon ed) {
		this.ed = ed;
	}
	
	public EnderDragon getEnderDragon() {
		return this.ed;
	}
	
	public void setFollower(ArmorStand follower) {
		this.follower = follower;
	}
	
	public ArmorStand getFollower() {
		return this.follower;
	}
	
	public void setTarget(ArmorStand target) {
		this.target = target;
	}
	
	public ArmorStand getTarget() {
		return this.target;
	}
	
	public ArrayList<Location> getDragonLocs() {
		return this.dragonLocs;
	}

	public void addDragonLocation(Location loc) {
		this.dragonLocs.add(loc);
	}
	
	public void clearEggBlockMap() {
		this.egg.clear();
	}
	
	public HashMap<Location, HashMap<Material, Byte>> getEggBlocks(){
		return this.egg;
	}
	
	boolean canPlace() {
		return this.canPlace;
	}
	
	public Altar getAltar() {
		return this;
	}
	
	public void setCanPlace(boolean bol) {
		this.canPlace = bol;
	}
	
	public void addPlacedEyes(Player p) {
		this.placedEyes.put(p, placedEyes.containsKey(p) ? placedEyes.get(p) + 1 : 1);
		int eyes = 0;
		for(Player player : this.placedEyes.keySet()) {
			eyes = eyes + this.placedEyes.get(player);
		}
		if(eyes == 8) {
			for(Player player : this.placedEyes.keySet()) {
				this.weights.put(player, placedEyes.get(player) * 100 <= 400 ? placedEyes.get(player) * 100 : 400);
			}
			this.canPlace = false;
			this.newLogger();
			Bukkit.getScheduler().runTaskLater(DragonSimulator.getInstance(), new Runnable() {
				
				@Override
				public void run() {

					Animation.startAnimation(getAltar());
					
				}
			}, 0);
		}
	}

	public void removePlacedEyes(Player p) {
		if(this.placedEyes.containsKey(p)) {
			if(placedEyes.get(p) > 1) {
				placedEyes.put(p, placedEyes.get(p) - 1);
			} else {
				placedEyes.remove(p);
			}
		}
	}
	public HashMap<Player, Integer> getPlacedEyes() {
		return this.placedEyes;
	}
	
	public void setPortalFrames(ArrayList<Location> locs) {
		this.portalFrames = locs;
	}
	public ArrayList<Location> getPortalFrames() {
		return this.portalFrames;
	}
	
	public static Altar getAltar(ArmorStand follower) {
		for(Altar a : altars) {
			if(a.getFollower() == null) continue;
			if(a.getFollower().equals(follower)) {
				return a;
			}
		}
		return null;
	}
	
	public static Altar getAltar(UUID uuid) {
		for(Altar a : altars) {
			if(a.getUUID().equals(uuid)) {
				return a;
			}
		}
		return null;
	}
	
	public static Altar getAltar(Location loc) {
		
		for(Altar a : altars) {
			if(a.getPortalFrames().contains(loc)) {
				return a;
			}
		}
		
		return null;
		
	}
	
	public Location getAltarMiddlePoint() {
		return this.middlePoint;
	}
	
	public void setAltarMiddlepoint(Location middlePoint) {
		this.middlePoint = middlePoint;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public static void loadAltars(FileConfiguration file) {
		
		rotatedEyeOfEnder = Utils.setTexture(rotatedEyeOfEnder, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOGU3ZjJkYjhlYWE4OGEwNDFjODlkNGMzNTNkMDY2Y2M0ZWRlZjc3ZWRjZjVlMDhiYjVkM2JhYWQifX19=\\");		
		eyeOfEnder = Utils.setTexture(eyeOfEnder, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFhOGZjOGRlNjQxN2I0OGQ0OGM4MGI0NDNjZjUzMjZlM2Q5ZGE0ZGJlOWIyNWZjZDQ5NTQ5ZDk2MTY4ZmMwIn19fQ==");
		
		ItemMeta eoem = eyeOfEnder.getItemMeta();
		eoem.setDisplayName("§5Summoning Eye");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Use this at the §5Ender Altar");
		lore.add("§7to summon Ender Dragons!");
		lore.add(" ");
		lore.add("§5§lEPIC");
		eoem.setLore(lore);
		eyeOfEnder.setItemMeta(eoem);
		
		for(String path : file.getKeys(false)) {
			
			Altar a = new Altar();
			
			Location loc = (Location) file.get(path + ".middlepoint");
			a.setAltarMiddlepoint(loc.clone());
			UUID uuid = UUID.fromString(file.getString(path + ".uuid"));
			a.setUUID(uuid);
			
			for(int i = 1; i<7; i++) {
				double x,y,z;
				x = file.getDouble(path + ".locs." + i + ".x");
				y = file.getDouble(path + ".locs." + i + ".y");
				z = file.getDouble(path + ".locs." + i + ".z");
				if(x == 0 || z == 0) continue;
				Location dragonLoc = new Location(loc.getWorld(), x, y, z);
				a.addDragonLocation(dragonLoc);
			}
			
			ArrayList<Location> portals = Buildings.placeAltar(loc.clone());
			
			PortalFrames(portals);
			a.setPortalFrames(portals);
			altars.add(a);
		}
		
	}
	
	public static void PortalFrames(ArrayList<Location> locs) {
		for(Location frame : locs) {
			frame.getWorld().getBlockAt(frame).setType(Material.ENDER_PORTAL_FRAME);
		}
	}
	
	public static void createNewAltar(Location loc) throws IOException {
		
		String uuid = UUID.randomUUID().toString();
		DragonSimulator.getCfg().set("altar.uuid", uuid);
		DragonSimulator.getCfg().set("altar..middlepoint", loc);
		DragonSimulator.getCfg().save(DragonSimulator.getCfgFile());
		
		ArrayList<Location> portals = new ArrayList<Location>();
		
		portals.add(loc.getWorld().getBlockAt((int) loc.getX() + 2, (int) loc.getY(), (int) loc.getZ() + 1).getLocation());
		portals.add(loc.getWorld().getBlockAt((int) loc.getX() + 2, (int) loc.getY(), (int) loc.getZ() - 1).getLocation());
		portals.add(loc.getWorld().getBlockAt((int) loc.getX() - 2, (int) loc.getY(), (int) loc.getZ() + 1).getLocation());
		portals.add(loc.getWorld().getBlockAt((int) loc.getX() - 2, (int) loc.getY(), (int) loc.getZ() - 1).getLocation());
		portals.add(loc.getWorld().getBlockAt((int) loc.getX() + 1, (int) loc.getY(), (int) loc.getZ() + 2).getLocation());
		portals.add(loc.getWorld().getBlockAt((int) loc.getX() + 1, (int) loc.getY(), (int) loc.getZ() - 2).getLocation());
		portals.add(loc.getWorld().getBlockAt((int) loc.getX() - 1, (int) loc.getY(), (int) loc.getZ() + 2).getLocation());
		portals.add(loc.getWorld().getBlockAt((int) loc.getX() - 1, (int) loc.getY(), (int) loc.getZ() - 2).getLocation());
		
		PortalFrames(portals);
		
		Altar a = new Altar();
		a.setUUID(UUID.fromString(uuid));
		a.setPortalFrames(portals);
		a.setAltarMiddlepoint(loc.clone());
		altars.add(a);
		
		
		loc.getWorld().getBlockAt((int) loc.getX() + 2, (int) loc.getY(), (int) loc.getZ()).setType(Material.ENDER_STONE);
		loc.getWorld().getBlockAt((int) loc.getX() - 2, (int) loc.getY(), (int) loc.getZ()).setType(Material.ENDER_STONE);
		loc.getWorld().getBlockAt((int) loc.getX(), (int) loc.getY(), (int) loc.getZ() + 2).setType(Material.ENDER_STONE);
		loc.getWorld().getBlockAt((int) loc.getX(), (int) loc.getY(), (int) loc.getZ() - 2).setType(Material.ENDER_STONE);
		
		loc.getWorld().getBlockAt((int) loc.getX() + 2, (int) loc.getY(), (int) loc.getZ() - 2).setType(Material.ENDER_STONE);
		loc.getWorld().getBlockAt((int) loc.getX() - 2, (int) loc.getY(), (int) loc.getZ() + 2).setType(Material.ENDER_STONE);
		loc.getWorld().getBlockAt((int) loc.getX() + 2, (int) loc.getY(), (int) loc.getZ() + 2).setType(Material.ENDER_STONE);
		loc.getWorld().getBlockAt((int) loc.getX() - 2, (int) loc.getY(), (int) loc.getZ() - 2).setType(Material.ENDER_STONE);
	}

	public Player getFirstDamager() {
		double damage = 0;
		Player first = null;
		for(Player player : damager.keySet()) {
			if(damager.get(player) > damage) {
				damage = damager.get(player);
				first = player;
			}
		}
		return first;
	}
	
	public Player getSecondDamager() {
		Player first = getFirstDamager();
		Player second = null;
		for(Player player : damager.keySet()) {
			if(player.equals(first)) continue;
			if(second == null) {
				second = player;
				continue;
			}
			if(damager.get(player) > damager.get(second)) {
				
				second = player;
				
			}
		}
		return second;
	}
	
	public Player getThirdDamager() {
		Player first = getFirstDamager();
		Player second = getSecondDamager();
		Player third = null;
		for(Player player : damager.keySet()) {
			if(player.equals(first) || player.equals(second)) continue;
			if(third == null) {
				third = player;
				continue;
			}
			if(damager.get(third) > damager.get(player)) {
				third = player;
			}
		}
		return third;
	}

	public Integer getPlayerDamagePosition(Player player) {
		ArrayList<Player> beforPlayer = new ArrayList<Player>();
		if(!damager.containsKey(player)) return damager.size();
		for(Player p : damager.keySet()) {
			if(p.equals(player)) continue;
			if(damager.get(p)  > damager.get(player)) beforPlayer.add(p);
		}
		return beforPlayer.size() != 0 ? (beforPlayer.size() + 1) : 1;
	}
	
	@EventHandler
	public void onPortalCreateEvent(EntityCreatePortalEvent e) {
		if(e.getPortalType().equals(PortalType.ENDER)) e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockDestroy(EntityExplodeEvent e) {
		if(e.getEntity() instanceof EnderDragon) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFallingBlockHitGround(EntityChangeBlockEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockDestroy(EntityChangeBlockEvent e) {
		if(e.getEntity().getType().equals(EntityType.ENDER_DRAGON)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		if(e.getEntity() instanceof EnderDragon) {
			for(Entity entity : e.getEntity().getNearbyEntities(2, 2, 2)) {
				if(entity.getCustomName() != null) {
					if(entity.getCustomName().equals("follower")) {
						Altar a = getAltar((ArmorStand) entity);
						if(a == null) continue;
						e.setTarget(a.getTarget());
						break;
					}
				}
			}
		}
	}

	public void clearDamager() {
		this.damager.clear();
	}

	public void closeLogger() {
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
