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

package com.guntherdw.bukkit.tweakcraft.Tools;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * @author GuntherDW
 */
public class PermissionsResolver {

    public enum PermissionResolvingMode { BUKKIT, NIJIPERMS, PERMISSIONSEX };

    public TweakcraftUtils plugin;
    private PermissionHandler permshandler = null;
    private PermissionManager permsManager = null;
    private PermissionResolvingMode resolvmode = null;


    public PermissionsResolver(TweakcraftUtils instance) {
        this.plugin = instance;
    }
    
    public String getUserPrefix(String world, Player player) {
        return this.getUserPrefix(world, player.getName());
    }

    public String getUserPrefix(String world, String player) {
        String prefix = null;

        if     (resolvmode == PermissionResolvingMode.BUKKIT)    prefix = "\u00A7f";
        else if(resolvmode == PermissionResolvingMode.PERMISSIONSEX) {
            PermissionUser puser = permsManager.getUser(player);
            if(puser!=null) prefix = puser.getPrefix(world);
        }
        else if(resolvmode == PermissionResolvingMode.NIJIPERMS) prefix = permshandler.getUserPrefix(world, player);

        return prefix!=null?prefix.replace("&", "\u00A7"):null;

    }

    public String getUserSuffix(String world, Player player) {
        return this.getUserSuffix(world, player.getName());
    }

    public String getUserSuffix(String world, String player) {
        String suffix = null;

        if     (resolvmode == PermissionResolvingMode.BUKKIT)    suffix = "\u00A7f";
        else if(resolvmode == PermissionResolvingMode.PERMISSIONSEX) {
            PermissionUser puser = permsManager.getUser(player);
            if(puser!=null) suffix = puser.getSuffix(world);
        }
        else if(resolvmode == PermissionResolvingMode.NIJIPERMS) suffix = permshandler.getUserSuffix(world, player);

        return suffix!=null?suffix.replace("&", "\u00A7"):null;
    }

    public boolean inGroup(String group, Player player) {
        if(this.resolvmode == PermissionResolvingMode.BUKKIT) {
            return player.isOp();
        } else {
            return this.inGroup(player.getWorld().getName(), group, player);
        }
    }

    public boolean inGroup(String world, String group, Player player) {
        if(this.resolvmode == PermissionResolvingMode.PERMISSIONSEX) {
            PermissionUser puser = permsManager.getUser(player);
            return puser!=null?puser.inGroup(group, world):false;
        } else {
            return permshandler.inGroup(player.getWorld().getName(), player.getName(), group);
        }
    }

    public boolean hasPermission(String world, Player player, String permissionbit) {
        if(this.resolvmode == PermissionResolvingMode.BUKKIT) return player.hasPermission(permissionbit);
        else
            if(this.resolvmode == PermissionResolvingMode.PERMISSIONSEX)
                              return permsManager.has(player, permissionbit, world);
            else              return permshandler.has(world, player.getName(), permissionbit);
    }
    


    /* NijiPermissions or BukkitPerms? */
    public void setMode(PermissionResolvingMode mode) {
        resolvmode = mode;
        if(resolvmode.equals(PermissionResolvingMode.NIJIPERMS)) this.registerNijiPerms();
        else if(resolvmode.equals(PermissionResolvingMode.PERMISSIONSEX)) this.registerermissionsEx();
    }

    public PermissionResolvingMode getMode() {
        return resolvmode;
    }
    
    private void registerermissionsEx() {
        /* Plugin plg = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
        if(this.permsManager==null)
            if(plg!=null)
                permsManager = ((PermissionsEx)plg).getPermissionManager(); */
        if(this.permsManager==null)
            permsManager = PermissionsEx.getPermissionManager();
    }
    
    private void registerNijiPerms() {
        Plugin plg = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if(this.permshandler==null)
            if(plg!=null)
                permshandler = ((Permissions)plg).getHandler();
    }

}
