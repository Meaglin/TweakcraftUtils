/**
 * 
 */
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
import com.guntherdw.bukkit.tweakcraft.request.TpcRequest;

/**
 * @author Meaglin
 *
 */
public class CommandTpc implements iCommand {

	/* (non-Javadoc)
	 * @see com.guntherdw.bukkit.tweakcraft.Commands.iCommand#executeCommand(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[], com.guntherdw.bukkit.tweakcraft.TweakcraftUtils)
	 */
	@Override
	public boolean executeCommand(CommandSender sender, String command,
			String[] args, TweakcraftUtils plugin) throws PermissionsException,
			CommandSenderException, CommandUsageException, CommandException {
		if(!(sender instanceof Player))
			 throw new CommandSenderException("You're the console, where do you think you're going?");
		
		Player player = (Player) sender;
		
       if (!plugin.check(player, "tpc"))
           throw new PermissionsException(command);
       
       if (args.length < 1)
           throw new CommandUsageException(ChatColor.YELLOW + "I need a name to tp you to!");
		
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
           player.sendMessage(ChatColor.RED + "You don't have the correct permission to tp to" + target.getDisplayName() + ChatColor.RED + "!");
           target.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " tried to tpc to you!");
           return true;
       }

       if(!player.getWorld().getName().equals(target.getWorld().getName())) {
           if(!plugin.check(player, "worlds."+target.getWorld().getName()+".tp"))
               player.sendMessage(ChatColor.RED + "You don't have permission to TP to someone in that world!");
           return true;
       }
       
       plugin.getRequestHandler().registerRequest(player, target, new TpcRequest(player));
       player.sendMessage(ChatColor.GREEN + "Tpc request sent to " + target.getDisplayName() + ChatColor.GREEN + "!");
       return true;
	}

	/* (non-Javadoc)
	 * @see com.guntherdw.bukkit.tweakcraft.Commands.iCommand#getPermissionSuffix()
	 */
	@Override
	public String getPermissionSuffix() {
		return "tpc";
	}

}
