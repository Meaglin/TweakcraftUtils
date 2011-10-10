package com.guntherdw.bukkit.tweakcraft.Commands.Custom;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;

public class CommandAccept implements iCommand {

	@Override
	public boolean executeCommand(CommandSender sender, String command,
			String[] args, TweakcraftUtils plugin) throws PermissionsException,
			CommandSenderException, CommandUsageException, CommandException {
		if(!(sender instanceof Player))
			 throw new CommandSenderException("You're the console, where do you think you're going?");
		
		Player player = (Player) sender;
		
		if (!plugin.check(player, "request"))
			throw new PermissionsException(command);
      
		plugin.getRequestHandler().onAccept(player);
		
		return true;
	}

	@Override
	public String getPermissionSuffix() {
		return "request";
	}

}
