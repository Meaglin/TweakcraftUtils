package com.guntherdw.bukkit.tweakcraft.Tools;

import org.bukkit.entity.Player;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

public class ZonePermissionsResolver extends PermissionsResolver {

	public ZonePermissionsResolver(TweakcraftUtils instance) {
		super(instance);
	}

	@Override
    public String getUserPrefix(String world, String player) {
        String prefix = null;
        prefix = plugin.getP().getPrefix(world, player);
        return prefix!=null?prefix.replace("&", "\u00A7"):null;

    }

    public String getUserSuffix(String world, Player player) {
        return this.getUserSuffix(world, player.getName());
    }

    public String getUserSuffix(String world, String player) {
        String suffix = null;
        suffix = plugin.getP().getSuffix(world, player);
        return suffix!=null?suffix.replace("&", "\u00A7"):null;
    }

    public boolean inGroup(String group, Player player) {
        return this.inGroup(player.getWorld().getName(), group, player);
    }

    public boolean inGroup(String world, String group, Player player) {
        return plugin.getP().inGroup(player, world, group);
    }

    public boolean hasPermission(String world, Player player, String permissionbit) {
        return plugin.getP().canUse(player, world, permissionbit);
    }
}
