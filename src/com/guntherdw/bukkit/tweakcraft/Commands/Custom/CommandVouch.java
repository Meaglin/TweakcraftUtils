package com.guntherdw.bukkit.tweakcraft.Commands.Custom;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ensifera.animosity.craftirc.RelayedMessage;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerData;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.Vouch;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;

public class CommandVouch implements iCommand{

	@Override
	public boolean executeCommand(CommandSender sender, String command,
			String[] args, TweakcraftUtils plugin) throws PermissionsException,
			CommandSenderException, CommandUsageException, CommandException {
		
		if(!(sender instanceof Player))
			throw new CommandSenderException("Sod off!");
		
		Player player = (Player)sender;
		
		if(!plugin.check(player, "vouch"))
			throw new PermissionsException(command);
		
		if(args.length == 0) 
			throw new CommandUsageException("No playername specified, /vouch [playername]");
		
    	List<PlayerData> list = plugin.getDatabase().find(PlayerData.class).where().or(plugin.getDatabase().getExpressionFactory().like("name", args[0] + "%"), plugin.getDatabase().getExpressionFactory().like("displayname", "%" + args[0] + "%")).findList();

    	if(list.size() == 0)
    	    throw new CommandException("No player found with name: " + args[0] + "!");

    	if(list.size() > 1)
    		throw new CommandException("Too many players found with name: " + args[0] + "!");
		
    	PlayerData target = list.get(0);
    	if(target.isOnline()) target = plugin.getPlayerData(target.getName());
    	
    	if(target.findVouch(plugin) != null) 
    		throw new CommandException("Player is already vouched.");

    	target.update(plugin, false);
    	Vouch vouch = new Vouch(plugin.getPlayerData(player), target);
    	if(target.getLastrank().equalsIgnoreCase("default")) {
    		plugin.getP().setGroup("world", target.getName(), "user");
    		target.setLastrank("user");    		
    	}
    	target.update(plugin, false); // Just in case, if the player is online ;)
    	plugin.getDatabase().save(vouch);
    	plugin.getDatabase().update(target);
    	for(Player p : plugin.getServer().getOnlinePlayers()) {
    		p.sendMessage(ChatColor.RED + "[" + ChatColor.GREEN + "Broadcast" + ChatColor.RED + "] " + ChatColor.GREEN + player.getDisplayName() + " has just vouched " + target.getDisplayname() + ", congrats!");
    	}
    	if(plugin.getConfigHandler().enableIRC && plugin.getCraftIRC()!=null) {
            if(plugin.getConfigHandler().GIRCenabled) {
                RelayedMessage rm = plugin.getCraftIRC().newMsgToTag(plugin.getEndPoint(), plugin.getConfigHandler().GIRCtag, "generic");
                rm.setField("message", ChatColor.RED + "[" + ChatColor.GREEN + "Broadcast" + ChatColor.RED + "] " + ChatColor.GREEN + player.getDisplayName() + " has just vouched " + target.getDisplayname() + ", congrats!");
                rm.post();
            }
        }
		return true;
	}

	@Override
	public String getPermissionSuffix() {
		return "vouch";
	}
	
}
