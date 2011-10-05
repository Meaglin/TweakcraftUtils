package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerData;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PunishEntry;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;

public class CommandPunish implements iCommand {
	
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
    	throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
    	
    	if (sender instanceof Player)
    		if (!plugin.check((Player) sender, "punish"))
    			throw new PermissionsException(command);

		if (args.length < 1)
		    throw new CommandUsageException(ChatColor.YELLOW + "I need at least 1 name to punish!");
		
		PlayerData data = plugin.getPlayerData(args[0]);
		
		if(data == null)
			throw new CommandUsageException("Cannot find player with name " + args[0] + "!");
		
		if(data.isDemoted()) 
			throw new CommandUsageException("Player " + data.getName() + " is already punnished!");
		
		if(args.length > 1 && args.length < 3)
			throw new CommandUsageException("Usage: /punish [player] <sec|min|hours|days> <amount> <reason>");
		
		long time = -1;
		if(args.length > 1) {
			int factor = 1;
			String arg = args[1].toLowerCase();
			if(arg.startsWith("sec")) factor = 1;
			else if(arg.startsWith("min")) factor = 60;
			else if(arg.startsWith("hour")) factor = 60 * 60;
			else if(arg.startsWith("day")) factor = 60 * 60 * 24;
			else {
				throw new CommandUsageException("Usage: /punish [player] <sec|min|hours|days> <amount> <reason>");
			}
			int amount = 0;
			try {
				amount = Integer.parseInt(args[2]);
			} catch(NumberFormatException e) {
				throw new CommandUsageException("Usage: /punish [player] <sec|min|hours|days> <amount> <reason>");
			}
			//if(amount <= 0)
			//	throw new CommandUsageException("Invalid time amount!");
			
			time = amount * factor * 1000;
			if(time < 0) time = -1;
		}
		String reason = "no reason";
		if(args.length > 3) {
			reason = "";
			for(int i = 3;i < args.length;i++) {
				reason += " " + args[i];
			}
			if(!reason.equals(""))reason = reason.substring(1);
		}
		
		if(time != -1){
			data.setDemotetime(time + System.currentTimeMillis());
			sender.sendMessage(ChatColor.GREEN + "Player " + data.getName() + " is now outcast for " + PlayerData.formatRemaining((int) (time/1000)) + " with reason: " + reason + " !");
			plugin.getLogger().info("[TweakcraftUtils] " + sender.getName() + " has outcast " + data.getName() + " for " + PlayerData.formatRemaining((int) (time/1000)) + " with reason: " + reason + " !");
		}
		else {
			data.setDemotetime(-1);
			sender.sendMessage(ChatColor.GREEN + "Player " + data.getName() + " is now Permanently outcast with reason: " + reason + " !");
			plugin.getLogger().info("[TweakcraftUtils] " + sender.getName() + " has outcast " + data.getName() + " with reason: " + reason + " !");
		}
		data.setOldrank(data.getLastrank());
		plugin.getPermissionHandler().getUserObject("world", data.getName()).removeParent(plugin.getPermissionHandler().getGroupObject("world", data.getLastrank()));
		plugin.getPermissionHandler().getUserObject("world", data.getName()).addParent(plugin.getPermissionHandler().getGroupObject("world", "outcast"));
		Player player = plugin.getServer().getPlayer(data.getName());
		if(player != null) {
			player.sendMessage(ChatColor.RED + "You are now outcast" + (time > 0 ? " for " + PlayerData.formatRemaining((int) (time/1000)) : "") + " with reason: " + reason + "!");
		}
		plugin.getDatabase().update(data);
		PunishEntry entry = new PunishEntry();
		entry.set("PUNISH", sender.getName(), data.getName(), time, reason);
		plugin.getDatabase().save(entry);
		
		return true;
    }

	@Override
	public String getPermissionSuffix() {
		return "punish";
	}

}
