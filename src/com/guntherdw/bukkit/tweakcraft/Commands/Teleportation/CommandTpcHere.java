package com.guntherdw.bukkit.tweakcraft.Commands.Teleportation;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.request.TpcHereRequest;

/**
 * @author Meaglin
 *
 */
public class CommandTpcHere implements iCommand {

	@Override
	public boolean executeCommand(CommandSender sender, String command,
			String[] args, TweakcraftUtils plugin) throws PermissionsException,
			CommandSenderException, CommandUsageException, CommandException {
		
		if(!(sender instanceof Player))
			 throw new CommandSenderException("You're the console, where do you think you're going?");
		
		Player player = (Player) sender;
		
        if (!plugin.check(player, "tpchere"))
            throw new PermissionsException(command);
        
        if (args.length < 1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need a name to tpc here!");
		
		Player target = plugin.getServer().getPlayer(args[0]);
		
		if(target == null)
			throw new CommandUsageException(ChatColor.YELLOW + "No player found with name " + args[0] + " !");
		
		if (plugin.getDonottplist().contains(player.getName()) && !plugin.check(player, "forcetp")) {
            player.sendMessage(ChatColor.RED + "You can't tp when you don't allow others to tp to you!");
            return true;
		}
		boolean refusetp = plugin.getDonottplist().contains(target.getName());
		boolean override = false;
        if (player.isOp() || plugin.check(player, "forcetp")) {
            override = true;
        } else {
            override = false;
        }
        
        if (refusetp && !override) {
            player.sendMessage(ChatColor.RED + "You don't have the correct permission to tpc here " + target.getDisplayName() + ChatColor.RED + "!");
            target.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " tried to tpchere you!");
            return true;
        }

        if(!player.getWorld().getName().equals(target.getWorld().getName())) {
            if(!plugin.check(target, "worlds."+player.getWorld().getName()+".tp"))
                player.sendMessage(ChatColor.RED + "Your target doesn't have to permissions to tp to your world!");
            return true;
        }
        
        plugin.getRequestHandler().registerRequest(player, target, new TpcHereRequest(player));
        player.sendMessage(ChatColor.GREEN + "TpcHere request sent to " + target.getDisplayName() + ChatColor.GREEN + "!");
		return true;
	}

	@Override
	public String getPermissionSuffix() {
		return "tpchere";
	}

}
