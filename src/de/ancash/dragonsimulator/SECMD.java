package de.ancash.dragonsimulator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SECMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(p.hasPermission("dragonsimulator.se")) {
				p.getInventory().addItem(Altar.eyeOfEnder.clone());
			}
		}
		return false;
	}

}
