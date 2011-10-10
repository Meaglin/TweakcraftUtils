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

package com.guntherdw.bukkit.tweakcraft.Commands.Custom;

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
public class CommandUnMute implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "mute"))
                throw new PermissionsException(command);
        
        if (args.length < 1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need at least 1 name to unmute!");
        
        PlayerData data = plugin.getPlayerData(args[0]);
        
        if(data == null)
        	throw new CommandUsageException("Cannot find player with name " + args[0] + "!");
        
        if(!data.isMuted()) 
        	throw new CommandUsageException("Player " + data.getName() + " is not muted!");
        
        
        String reason = "no reason";
        if(args.length > 1) {
        	reason = "";
        	for(int i = 1;i < args.length;i++) {
        		reason += " " + args[i];
        	}
        	if(!reason.equals(""))reason = reason.substring(1);
        }
        
        PunishEntry entry = new PunishEntry();
        entry.set("UNMUTE", sender.getName(), data.getName(), data.getMutetime() - System.currentTimeMillis(), reason);
        plugin.getDatabase().save(entry);
        data.setMutetime(0);
        plugin.getDatabase().update(data);
        plugin.getLogger().info("[TweakcraftUtils] " + sender.getName() + " is unmuting " + data.getName() + " with reason: " + reason);
        Player player = plugin.getServer().getPlayer(data.getName());
        if(player != null) {
        	player.sendMessage(ChatColor.GREEN + "You are now unmuted!");
        }
        sender.sendMessage(ChatColor.GREEN + data.getName() + " is now unmuted!");
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "mute";
    }
}
