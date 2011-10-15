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

package com.guntherdw.bukkit.tweakcraft.Commands.General;

import java.util.List;

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerData;
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
public class CommandWhois implements iCommand {
    @Override
    public boolean executeCommand(CommandSender sender, String command, String[] args, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        // First search for a nick
        boolean getIP = true;
        boolean getInfo = true;
        if(sender instanceof Player) {
            if(!plugin.check((Player)sender, "whois"))
                throw new PermissionsException(command);
            if(!plugin.check((Player)sender, "whois.ip"))
                getIP = false;
            if(!plugin.check((Player)sender, "whois.info"))
            	getInfo = false;
        }


        if (args.length == 1 ) {
        	List<PlayerData> list = plugin.getDatabase().find(PlayerData.class).where().or(plugin.getDatabase().getExpressionFactory().like("name", args[0] + "%"), plugin.getDatabase().getExpressionFactory().like("displayname", "%" + args[0] + "%")).findList();
        	if(list.size() == 0)
        		throw new CommandException("No players found with name: " + args[0] + "!");
        	if(list.size() > 1)
        		throw new CommandException("Too many players found with name: " + args[0] + "!");
        	
        	PlayerData data = list.get(0);
        	sender.sendMessage(ChatColor.YELLOW + "Player: " + ChatColor.WHITE + data.getName() + ChatColor.YELLOW + "(" + data.getDisplayname() + ChatColor.YELLOW + ")");
        	sender.sendMessage(ChatColor.YELLOW + "Rank: " + data.getLastrank() + "");
        	if(data.isOnline()) {
        		Player player = plugin.getServer().getPlayerExact(data.getName());
        		if(data.getName().equals(sender.getName())){
        			getInfo = true;
        			getIP = true;
        		}
        		data.update(plugin, false);
        		sender.sendMessage(ChatColor.YELLOW + "Online: " + ChatColor.GREEN + "true");
        		if(getIP) {
        			sender.sendMessage(ChatColor.YELLOW + "IP: " + player.getAddress().getAddress().getHostName());
        		}
        	} else {
        		sender.sendMessage(ChatColor.YELLOW + "LastSeen: " + PlayerData.formatRemaining((int) (-1 * (data.getLastlogin() - System.currentTimeMillis()) / 1000 )) + " ago.");
        	}
        	sender.sendMessage(ChatColor.YELLOW + "Onlinetime: " + PlayerData.formatRemaining((int) ((data.getOnlinetime())/1000)) + ".");
        	if(getInfo) {
        		if(data.getBantime() != 0) {
        			sender.sendMessage(ChatColor.YELLOW + "Ban: " + (data.getBantime() == -1 ? "Permanent" : PlayerData.formatRemaining((int) ((data.getBantime() - System.currentTimeMillis())/1000))));
        		}
	        	if(data.getMutetime() != 0) {
	        		sender.sendMessage(ChatColor.YELLOW + "Mute: " + (data.getMutetime() == -1 ? "Permanent" : PlayerData.formatRemaining((int) ((data.getMutetime() - System.currentTimeMillis())/1000))));
	        	}
	        	if(data.getDemotetime() != 0) {
	        		sender.sendMessage(ChatColor.YELLOW + "Punish: " + (data.getDemotetime() == -1 ? "Permanent" : PlayerData.formatRemaining((int) ((data.getDemotetime() - System.currentTimeMillis())/1000))));        				
	        	}
        	}
        	/*
            Player nick = plugin.getPlayerListener().findPlayerByNick(args[0]);
            // sender.sendMessage(nick.toString());
            String gname = null;
            // Group  g  = null;
            Player sp = findPlayer(args[0], plugin);
            Player who = nick==null?sp:nick;
            String groups = ""; 

            String playername = null;
            boolean online = (who!=null);

            if(who==null) { // Is it an offline player? Check permissions
                /* if(plugin.getPermissionHandler()!=null) {
                    // Check for nicks
                    String pname = args[0];
                    String findnick = plugin.getPlayerListener().findPlayerNameByNick(pname);
                    if(findnick != null) pname = findnick;


                    PermissionHandler handler = plugin.getPermissionHandler();
                    String wname = plugin.getServer().getWorlds().get(0).getName();
                    User user = plugin.getPermissionHandler().getUserObject(wname, pname);
                    // groups = plugin.getPermissionHandler().getPrimaryGroup(wname, args[0]);
                    if(user!=null) {
                        playername = user.getName();
                        groups = plugin.getPermissionHandler().getPrimaryGroup(wname, pname);
                    }
                }
            } else {
                playername = who.getName();
                if(plugin.getPermissionHandler()!=null) {

                    String wname = plugin.getServer().getWorlds().get(0).getName();
                    groups = plugin.getPermissionHandler().getPrimaryGroup(wname, playername);
                }
            }

            if(playername!=null) {
                sender.sendMessage(ChatColor.YELLOW+"Playername : "+playername+" "+(nick!=null?"("+plugin.getNickWithColors(who.getName())+ChatColor.YELLOW+")":""));
                // String group = plugin.getPermissionHandler.getG(who.getWorld().getName(), who.getName());
                sender.sendMessage(ChatColor.YELLOW+"Groups : "+groups);
                if(!getIP && online) {
                    if(((Player)sender).getName().equalsIgnoreCase(who.getName()))
                        getIP = true;
                }
                if(getIP && online)
                    sender.sendMessage(ChatColor.YELLOW + "IP: " + who.getAddress().getAddress().getHostName());
            } else {
                throw new CommandException("Can't find player!");
            }*/
        } else {
            throw new CommandUsageException("I need a player, /whois [playername]!");
        } 
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "whois";
    }

    public Player findPlayer(String playername, TweakcraftUtils plugin) {
        for(Player p : plugin.getServer().matchPlayer(playername)) {
            if(p.getName().toLowerCase().contains(playername.toLowerCase())) {
                return p;
            }
        }
        return null;
    }
}
