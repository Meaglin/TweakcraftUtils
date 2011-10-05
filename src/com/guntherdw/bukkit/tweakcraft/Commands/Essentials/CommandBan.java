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
public class CommandBan implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player)
            if (!plugin.check((Player) sender, "ban"))
                throw new PermissionsException(command);
        
        if (args.length < 1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need at least 1 name to ban!");
        
        PlayerData data = plugin.getPlayerData(args[0]);
        
        if(data == null)
        	throw new CommandUsageException("Cannot find player with name " + args[0] + "!");
        
        if(data.isBanned()) 
        	throw new CommandUsageException("Player " + data.getName() + " is already banned!");
        
        if(args.length > 1 && args.length < 3)
        	throw new CommandUsageException("Usage: /ban [player] <sec|min|hours|days> <amount> <reason>");
        
        long time = -1;
        if(args.length > 1) {
        	int factor = 1;
        	String arg = args[1].toLowerCase();
        	if(arg.startsWith("sec")) factor = 1;
        	else if(arg.startsWith("min")) factor = 60;
        	else if(arg.startsWith("hour")) factor = 60 * 60;
        	else if(arg.startsWith("day")) factor = 60 * 60 * 24;
        	else {
        		throw new CommandUsageException("Usage: /ban [player] <sec|min|hours|days> <amount> <reason>");
        	}
        	int amount = 0;
        	try {
        		amount = Integer.parseInt(args[2]);
        	} catch(NumberFormatException e) {
        		throw new CommandUsageException("Usage: /ban [player] <sec|min|hours|days> <amount> <reason>");
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
        	data.setBantime(time + System.currentTimeMillis());
        	sender.sendMessage(ChatColor.GREEN + "Player " + data.getName() + " is now banned for " + PlayerData.formatRemaining((int) (time/1000)) + " with reason: " + reason + " !");
        	plugin.getLogger().info("[TweakcraftUtils] " + sender.getName() + " has banned " + data.getName() + " for " + PlayerData.formatRemaining((int) (time/1000)) + " with reason: " + reason + " !");
        }
        else {
        	data.setBantime(-1);
        	sender.sendMessage(ChatColor.GREEN + "Player " + data.getName() + " is now Permanently banned with reason: " + reason + " !");
        	plugin.getLogger().info("[TweakcraftUtils] " + sender.getName() + " has banned " + data.getName() + " with reason: " + reason + " !");
        }
        Player player = plugin.getServer().getPlayer(data.getName());
        if(player != null) {
        	player.kickPlayer("You are now banned" + (time > 0 ? " for " + PlayerData.formatRemaining((int) (time/1000)) : "") + " with reason: " + reason + "!");
        }
        plugin.getDatabase().update(data);
        PunishEntry entry = new PunishEntry();
        entry.set("BAN", sender.getName(), data.getName(), time, reason);
        plugin.getDatabase().save(entry);
        
        /* BanHandler handler = plugin.getBanhandler();
        if (args.length < 1)
            throw new CommandUsageException(ChatColor.YELLOW + "I need at least 1 name to ban!");
        if (handler.isBanned(args[0])) {
            sender.sendMessage(ChatColor.YELLOW + "This player is already banned!");
        } else {
            String reason = "";
            String playername = args[0];
            String duration = null;
            Long dura = null;
            String toFull = null;
            if (args.length > 1) {
                boolean skipfirst = false;
                if(args[1].startsWith("t:")) {
                    duration = args[1].substring(2);
                    dura = TimeTool.calcTime(duration);
                    toFull = TimeTool.getDurationFull(duration);
                    duration = duration.substring(0, duration.length()-1);
                }
                if(dura!=null) skipfirst=true;
                for (int x = skipfirst?2:1; x < args.length; x++) {
                    reason += args[x] + " ";
                }
                if (reason.length() > 1)
                    reason = reason.substring(0, reason.length() - 1);
            }
            if(dura!=null&&!plugin.getConfigHandler().enablePersistence) {
                throw new CommandUsageException("ERROR: For timed bans to work, persistence HAS to be enabled!");
            }

            handler.banPlayer(playername.toLowerCase(), reason, dura);
            sender.sendMessage(ChatColor.YELLOW + "Banning " + playername + ChatColor.YELLOW+ (dura!=null?" for "+duration+" "+toFull+"!":""));

            Player player = plugin.getServer().getPlayer(playername);
            if (player != null) {
                sender.sendMessage(ChatColor.YELLOW + "Kickbanning " + player.getName());
                player.kickPlayer(reason);
            }
            plugin.getLogger().info("[TweakcraftUtils] Banning " + playername + "!");
            handler.saveBans();
        } */
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "ban";
    }
}
