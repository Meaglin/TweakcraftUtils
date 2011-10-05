/*
 * Copyright (c) 2011 GuntherDW
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.guntherdw.bukkit.tweakcraft.Commands.Essentials;

import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerData;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PunishEntry;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author GuntherDW
 */
public class CommandUnPunish implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "punish"))
                throw new PermissionsException(command);
        
        if (args.length < 1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need at least 1 name to unpunish!");
        
        PlayerData data = plugin.getPlayerData(args[0]);
        
        if(data == null)
        	throw new CommandUsageException("Cannot find player with name " + args[0] + "!");
        
        if(!data.isDemoted()) 
        	throw new CommandUsageException("Player " + data.getName() + " is not punnished!");
        
        
        String reason = "no reason";
        if(args.length > 1) {
        	reason = "";
        	for(int i = 1;i < args.length;i++) {
        		reason += " " + args[i];
        	}
        	if(!reason.equals(""))reason = reason.substring(1);
        }
        
        PunishEntry entry = new PunishEntry();
        entry.set("UNPUNISH", sender.getName(), data.getName(), data.getDemotetime() - System.currentTimeMillis(), reason);
        plugin.getDatabase().save(entry);
        plugin.getPermissionHandler().getUserObject("world", data.getName()).removeParent(plugin.getPermissionHandler().getGroupObject("world", "outcast"));
		plugin.getPermissionHandler().getUserObject("world", data.getName()).addParent(plugin.getPermissionHandler().getGroupObject("world", data.getOldrank()));
		data.setDemotetime(0);
		data.setLastrank(data.getOldrank());
		data.setOldrank("");        
        
        plugin.getDatabase().update(data);
        plugin.getLogger().info("[TweakcraftUtils] " + sender.getName() + " is unpunnishing " + data.getName() + " with reason: " + reason);
        
        Player player = plugin.getServer().getPlayer(data.getName());
        if(player != null) {
        	player.sendMessage(ChatColor.GREEN + "You are not outcast anymore!");
        }
        sender.sendMessage(ChatColor.GREEN + data.getName() + " is not outcast anymore!");
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
        return "punish";
    }
}
