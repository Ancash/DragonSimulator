package de.ancash.dragonsimulator;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetAltarCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if(sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if(p.hasPermission("dragonsimulator.placealtar") || p.isOp()) {
				
				try {
					Altar.createNewAltar(p.getLocation());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		return false;
	}

}
