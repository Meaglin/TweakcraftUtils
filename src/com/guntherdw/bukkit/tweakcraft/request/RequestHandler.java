package com.guntherdw.bukkit.tweakcraft.request;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

public class RequestHandler {
	private HashMap<Integer, Request> requests = new HashMap<Integer, Request> ();
	private TweakcraftUtils plugin;
	public RequestHandler(TweakcraftUtils plugin) {
		this.plugin = plugin;
	}
	
	public void registerRequest(Player requester, Player player, Request request) {
		requests.put(player.getEntityId(), request);
		request.onRequest(plugin, requester, player);
	}
	
	public void unregisterRequest(Player player) {
		requests.remove(player.getEntityId());
	}
	
	public void onAccept(Player player) {
		Request request = requests.get(player.getEntityId());
		if(request == null) {
			player.sendMessage(ChatColor.GOLD + "No requests to accept.");
			return;
		}
		
		if(request.isExpired()) {
			player.sendMessage(Request.HAS_EXPIRED);
			return;
		}
		
		request.onAccept(plugin, player);
	}
	
	public void onDecline(Player player) {
		Request request = requests.get(player.getEntityId());
		if(request == null) {
			player.sendMessage(ChatColor.GOLD + "No requests to decline.");
			return;
		}
		
		if(request.isExpired()) {
			player.sendMessage(Request.HAS_EXPIRED);
			return;
		}
		
		request.onDecline(plugin, player);
	}
}
