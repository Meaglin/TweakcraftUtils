package com.guntherdw.bukkit.tweakcraft.Commands.Custom;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;

public class CommandSurvival implements iCommand {

	@Override
	public boolean executeCommand(CommandSender sender, String command,
			String[] args, TweakcraftUtils plugin) throws PermissionsException,
			CommandSenderException, CommandUsageException, CommandException {
		
		if(sender instanceof Player && !plugin.check((Player)sender, "survival")) throw new PermissionsException("");
		if(args.length < 1) throw new CommandUsageException("No player defined!");
		
		Player player = plugin.getServer().getPlayer(args[0]);
		if(player == null) throw new CommandException("No player found with name " + args[0] + "!");
		
		if(plugin.getP().getGroup(player, "survival") == null) {
			plugin.getP().addPermission(player, "world", "tweakcraftutils.worlds.survival");
			plugin.getP().addPermission(player, "world", "tweakcraftutils.worlds.survival.world");
			plugin.getP().addGroup(player, "survival", plugin.getP().getGroup(player, "world"));
			sender.sendMessage(ChatColor.GOLD + "Given player " + player.getDisplayName() + ChatColor.GOLD + " survival access!");
		} else {
			plugin.getP().removePermission(player, "world", "tweakcraftutils.worlds.survival");
			plugin.getP().removePermission(player, "world", "tweakcraftutils.worlds.survival.world");
			plugin.getP().setGroup(player, "survival", null);
			sender.sendMessage(ChatColor.GOLD + "Removed players " + player.getDisplayName() + ChatColor.GOLD + " survival access!");
		}
		/*
		User normal = plugin.getPermissionHandler().getUserObject("world", player.getName());
		
		if(plugin.getPermissionHandler().getGroupObject("survival", normal.getPrimaryGroup().getName()) == null)
			throw new CommandUsageException("Invalid Player, No valid primary group.");
		
		try {
			User user = plugin.getPermissionHandler().safeGetUser("survival", player.getName());
			if(user.getPrimaryGroup() != null) {
				for(Entry e : user.getParents())
					if(e instanceof Group) 
						user.removeParent((Group)e);
				normal.removePermission("tweakcraftutils.worlds.survival");
				normal.removePermission("tweakcraftutils.worlds.survival.world");
				sender.sendMessage(ChatColor.GOLD + "Removed players " + player.getDisplayName() + ChatColor.GOLD + " survival access!");
			} else {
				normal.addPermission("tweakcraftutils.worlds.survival");
				normal.addPermission("tweakcraftutils.worlds.survival.world");
				user.addParent(plugin.getPermissionHandler().getGroupObject("survival", normal.getPrimaryGroup().getName()));
				sender.sendMessage(ChatColor.GOLD + "Given player " + player.getDisplayName() + ChatColor.GOLD + " survival access!");
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new CommandException(e.getMessage());
		}
		*/
		return true;
	}

	@Override
	public String getPermissionSuffix() {
		return "survival";
	}

}
