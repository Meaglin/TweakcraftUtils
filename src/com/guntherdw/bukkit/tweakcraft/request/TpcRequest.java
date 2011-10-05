package com.guntherdw.bukkit.tweakcraft.request;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

public class TpcRequest extends Request {
	
	private String requester;
	
	public TpcRequest(Player requester) {
		super();
		this.requester = requester.getName();
	}

	public void onRequest(TweakcraftUtils plugin, Player requester, Player target) {
		target.sendMessage(ChatColor.GOLD + "Player " + requester.getDisplayName() + 
				ChatColor.GOLD + " wants to teleport you to him /accept to accept or /decline to decline.");
	}
	
	@Override
	public void onAccept(TweakcraftUtils plugin, Player player) {
		Player requester = plugin.getServer().getPlayer(this.requester);
		if(requester == null) {
			player.sendMessage(Request.HAS_EXPIRED);
			return;
		}
		
		if (plugin.getDonottplist().contains(player.getName()) && !plugin.check(player, "forcetp")) {
			requester.sendMessage(ChatColor.RED + "You can't tp when you don't allow others to tp to you!");
            return;
		}
		boolean refusetp = plugin.getDonottplist().contains(requester.getName());
		boolean override = false;
        if (player.isOp() || plugin.check(player, "forcetp")) {
            override = true;
        } else {
            override = false;
        }
        
        if (refusetp && !override) {
        	player.sendMessage(ChatColor.RED + "You don't have the correct permission to tp to " + requester.getDisplayName() + ChatColor.RED + "!");
        	requester.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " tried to tp to you!");
            return;
        }
		
        if(!player.getWorld().getName().equals(requester.getWorld().getName())) {
            if(!plugin.check(requester, "worlds."+player.getWorld().getName()+".tp"))
               requester.sendMessage(ChatColor.RED + "You don't have permission to TP to someone in that world!");
            return;
        }
        
		player.sendMessage(ChatColor.GOLD + "Request accepted.");
		requester.sendMessage(player.getDisplayName() + ChatColor.GOLD +  " has accepted your Tpc request.");
		requester.sendMessage(ChatColor.GOLD + "Teleporting you to " + player.getDisplayName() + ChatColor.GOLD +  " .");
		plugin.getTelehistory().addHistory(requester.getName(), requester.getLocation());
		requester.teleport(player);
	}

	@Override
	public void onDecline(TweakcraftUtils plugin, Player player) {
		Player requester = plugin.getServer().getPlayer(this.requester);
		if(requester == null) {
			player.sendMessage(Request.HAS_EXPIRED);
			return;
		}
		requester.sendMessage(player.getDisplayName() + ChatColor.RED +  " has denied your Tpc request.");
		player.sendMessage(ChatColor.GOLD + "Tpc request declined.");
	}

}
