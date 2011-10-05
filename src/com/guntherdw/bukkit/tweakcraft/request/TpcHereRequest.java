package com.guntherdw.bukkit.tweakcraft.request;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

public class TpcHereRequest extends Request {

	private String requester;
	
	public TpcHereRequest(Player requester) {
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
		
		if (plugin.getDonottplist().contains(requester.getName()) && !plugin.check(requester, "forcetp")) {
			requester.sendMessage(ChatColor.RED + "You can't tp when you don't allow others to tp to you!");
            return;
		}
		boolean refusetp = plugin.getDonottplist().contains(player.getName());
		boolean override = false;
        if (requester.isOp() || plugin.check(requester, "forcetp")) {
            override = true;
        } else {
            override = false;
        }
        
        if (refusetp && !override) {
        	requester.sendMessage(ChatColor.RED + "You don't have the correct permission to tp to " + player.getDisplayName() + ChatColor.RED + "!");
        	player.sendMessage(player.getDisplayName() + ChatColor.YELLOW + " tried to tpchere you!");
            return;
        }
        
        if(!player.getWorld().getName().equals(player.getWorld().getName())) {
            if(!plugin.check(player, "worlds."+player.getWorld().getName()+".tp"))
            	requester.sendMessage(ChatColor.RED + "Your target doesn't have to permissions to tp to your world!");
            return;
        }
		
		requester.sendMessage(player.getDisplayName() + ChatColor.GOLD +  " has accepted your TpcHere request.");
		player.sendMessage(ChatColor.GOLD + "Teleporting you to " + requester.getDisplayName() + ChatColor.GOLD +  " .");
		plugin.getTelehistory().addHistory(player.getName(), player.getLocation());
		player.teleport(requester);
	}

	@Override
	public void onDecline(TweakcraftUtils plugin, Player player) {
		Player requester = plugin.getServer().getPlayer(this.requester);
		if(requester == null) {
			player.sendMessage(Request.HAS_EXPIRED);
			return;
		}
		requester.sendMessage(player.getDisplayName() + ChatColor.RED +  " has denied your TpcHere request.");
		player.sendMessage(ChatColor.GOLD + "TpcHere request declined.");
	}

}
