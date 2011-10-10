package com.guntherdw.bukkit.tweakcraft.Commands.Custom;

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

public class CommandWarn implements iCommand {
	
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
		    throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
		if (sender instanceof Player)
		    if (!plugin.check((Player) sender, "warn"))
		        throw new PermissionsException(command);
		
		if (args.length < 1)
		    throw new CommandUsageException(ChatColor.YELLOW + "I need at least 1 name to warn!");
		
		PlayerData data = plugin.getPlayerData(args[0]);
		
		if(data == null)
			throw new CommandUsageException("Cannot find player with name " + args[0] + "!");
		
		
		String reason = "no reason";
		if(args.length > 1) {
			reason = "";
			for(int i = 1;i < args.length;i++) {
				reason += " " + args[i];
			}
			if(!reason.equals(""))reason = reason.substring(1);
		}
		
		PunishEntry entry = new PunishEntry();
		entry.set("WARN", sender.getName(), data.getName(), 0, reason);
		plugin.getDatabase().save(entry);
		plugin.getLogger().info("[TweakcraftUtils] " + sender.getName() + " is warning " + data.getName() + " with reason: " + reason);
		
		Player player = plugin.getServer().getPlayer(data.getName());
		if(player != null) {
			player.sendMessage(ChatColor.RED + "[WARN]" + ChatColor.GOLD + reason);
		}
		sender.sendMessage(ChatColor.GREEN + "Warned " + data.getName() + " with message " + reason + "!");
		/*
		BanHandler handler = plugin.getBanhandler();
		if (args.length > 0) {
		    String target = args[0].toLowerCase();
		    if (handler.isBanned(target)) {
		        sender.sendMessage(ChatColor.YELLOW + "Unbanning player!");
		        handler.unBan(target);
		        handler.saveBans();
		    } else {
		        sender.sendMessage(ChatColor.YELLOW + "That player isn't banned!");
		    }
		
		}
		*/
		return true;
	}
		
	@Override
	public String getPermissionSuffix() {
		return "warn";
	}

}
