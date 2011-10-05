package com.guntherdw.bukkit.tweakcraft.request;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

public abstract class Request {
	
	private long timeout;
	public static final long DEFAULT_TIMEOUT = 120000; // 2 minutes
	public static final String HAS_EXPIRED = ChatColor.RED + "The request has expired.";
	public Request() {
		timeout = System.currentTimeMillis() + getTimeout();
	}
	
	
	public abstract void onRequest(TweakcraftUtils plugin, Player requester, Player target);
	public abstract void onAccept(TweakcraftUtils plugin, Player player);
	public abstract void onDecline(TweakcraftUtils plugin, Player player);
	
	public long getTimeout() {
		return DEFAULT_TIMEOUT;
	}
	
	public boolean isExpired() {
		return timeout < System.currentTimeMillis();
	}
}
