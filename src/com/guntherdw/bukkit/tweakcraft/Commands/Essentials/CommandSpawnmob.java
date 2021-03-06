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

import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;
import com.guntherdw.bukkit.tweakcraft.Tools.ArgumentParser;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author GuntherDW
 */
public class CommandSpawnmob implements iCommand {
    public boolean executeCommand(CommandSender sender, String command, String[] realargs, TweakcraftUtils plugin)
            throws PermissionsException, CommandSenderException, CommandUsageException, CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.check(player, "spawnmob"))
                throw new PermissionsException(command);

            Location loc = player.getTargetBlock(null, 200).getLocation();
            ArgumentParser ap = new ArgumentParser(realargs);
            String[] args = ap.getNormalArgs();
            Random rnd = new Random();

            int slimesize = ap.getInteger("s", -1);
            int health = ap.getInteger("h", -1);
            boolean powered = ap.getBoolean("p", false);
            boolean shoven = ap.getBoolean("sh", false);
            String sheepcolor = ap.getString("sc", null);

            loc.setY(loc.getY() + 1); // Do not spawn them into the ground, silly!
            String mobName;
            Integer amount = 1;
            String victim = null;
            CreatureType type = null;
            CreatureType rider = null;
            Player victimplayer = player;
            LivingEntity lent = null;
            List<CreatureType> riders = null;

            if (args.length > 0) // only a mobname!
            {
                mobName = "";
                amount = 1;
                if (args[0].length() > 2)
                    mobName = args[0].substring(0, 1).toUpperCase() + args[0].substring(1, args[0].length());
                type = CreatureType.fromName(mobName);
                if (type == null) {
                    sender.sendMessage("Tried : "+mobName);
                    throw new CommandUsageException("Can't find that creature!");
                }
                if (args.length > 1) // amount
                {
                    try {
                        amount = Integer.parseInt(args[1]);
                        if(amount>100) {
                            amount=100; // This should be more than enough before your client starts to lag!
                        }
                    } catch (NumberFormatException e) {
                        throw new CommandUsageException("I need an amount, not a string!");
                    }
                }
                if(args.length > 2) // Riders and/or player!
                {
                    riders = new ArrayList<CreatureType>();
                    String tmpRiderString;
                    for(int x = 2; x < args.length ; x++) {

                        if(x==args.length-1) {
                            // Check to see if the last argument contains a playername
                            victim = plugin.findPlayer(args[args.length-1]);
                            victimplayer = plugin.getServer().getPlayer(victim);
                            /* if (victimplayer == null) {
                               throw new CommandUsageException("Can't find that player!");
                            } */
                            if(victimplayer!=null) {

                                loc = victimplayer.getLocation();
                            }
                        }
                        if(x<args.length-1 ||
                           (x==args.length-1 && victimplayer == null))
                        {
                            tmpRiderString = args[x];
                            if (args[x].length() > 2)
                                tmpRiderString = args[x].substring(0, 1).toUpperCase() + args[x].substring(1, args[x].length());
                            rider = CreatureType.fromName(tmpRiderString);
                            if(rider == null)
                            {
                                sender.sendMessage(ChatColor.YELLOW + "Didn't find one of the specified extra riders!");
                                // throw new CommandUsageException("Can't find rider creature!");
                            } else {
                                riders.add(rider);
                            }
                        }
                    }

                }

                // We're finally here
                // Creature crea = new
                if(victimplayer == null)
                    victimplayer = player;
                
                
                LivingEntity rid = null;
                for (int x = 0; x < amount; x++) {
                    lent = victimplayer.getWorld().spawnCreature(loc, type);
                    if(health>0)
                        lent.setHealth(health);
                    
                    if(lent instanceof Slime && slimesize > 0)
                        ((Slime)lent).setSize(slimesize);

                    if(lent instanceof Creeper && powered)
                        ((Creeper)lent).setPowered(powered);
                    
                    if(lent instanceof Sheep && shoven || sheepcolor!=null) {
                        ((Sheep)lent).setSheared(shoven);

                        if(sheepcolor!=null) {
                            DyeColor dc = null;
                            if(sheepcolor.equalsIgnoreCase("random")) {
                                dc = DyeColor.getByData((byte) rnd.nextInt(16));
                            } else {
                                dc = DyeColor.valueOf(sheepcolor.toUpperCase());
                            }
                            if(dc!=null)((Sheep)lent).setColor(dc);
                        }
                    }
                    
                    if(riders != null && riders.size()!=0) {
                        for(CreatureType t : riders) {
                            rid = victimplayer.getWorld().spawnCreature(loc, t);
                            if(lent != null) lent.setPassenger(rid);
                            lent = rid; // This makes the currently added mob the new "to-ride-along" mob
                        }
                    }
                }
                
            }
        } else {
            throw new CommandSenderException("What were you trying to do? :3");
        }
        return true;
    }

    @Override
    public String getPermissionSuffix() {
        return "spawnmob";
    }
}
