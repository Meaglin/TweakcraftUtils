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

package com.guntherdw.bukkit.tweakcraft.Chat;

import com.guntherdw.bukkit.tweakcraft.Configuration.ConfigurationHandler;
import com.guntherdw.bukkit.tweakcraft.Packages.LocalPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class AntiSpam {

    private ChatHandler ch;
    private Map<String, LocalPlayer> playerlist;
    private long watchtime;

    public AntiSpam(ChatHandler instance, ConfigurationHandler config) {
        this.ch = instance;
        this.playerlist = new HashMap<String, LocalPlayer>();
        this.watchtime = config.spamCheckTime;
    }

    public int checkSpam(Player player, String message) {
        if(!ch.getTCUtilsInstance().check(player, "spam")) {
            LocalPlayer lp = null;
            String playername = player.getName().toLowerCase();
            if(!playerlist.containsKey(playername)) {
                lp = new LocalPlayer(playername);
            } else {
                lp = playerlist.get(playername);
            }

            long currenttime = System.currentTimeMillis();
            int oldspamcounter = lp.getSpamcounter();
            if(lp.getLastmessagetime() > currenttime-watchtime) {
                lp.setSpamcounter(oldspamcounter+1);
            } else {
                lp.setSpamcounter(0);
            }
            lp.setLastmessagetime(currenttime);

            
            /* if(lp.getSpamcounter()!=0) {
                this.playerlist.put(playername, lp);
            } else {
                if(this.playerlist.containsKey(playername))
                    this.playerlist.remove(playername);
            } */

            this.playerlist.put(playername, lp);
            
            return lp.getSpamcounter();
        }
        return 0;
    }

}
