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

package com.guntherdw.bukkit.tweakcraft.Listeners;

import com.guntherdw.bukkit.tweakcraft.Chat.ChatHandler;
import com.guntherdw.bukkit.tweakcraft.Chat.ChatMode;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.Mail;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerData;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerHistoryInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerInfo;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerOptions;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PunishEntry;
import com.guntherdw.bukkit.tweakcraft.Exceptions.ChatModeException;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Worlds.IWorld;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author GuntherDW
 */
public class TweakcraftPlayerListener extends PlayerListener {

    //private final Logger log = Logger.getLogger("Minecraft");
    private final TweakcraftUtils plugin;
    private List<String> invisplayers;
    private Map<String, String> nicks;
    private List<PlayerInfo> playerinfo = new ArrayList<PlayerInfo>();
    private List<PlayerOptions> playeroptions = new ArrayList<PlayerOptions>();
    private List<String> nomount = new ArrayList<String>();

    public List<String> getNomount() {
        return nomount;
    }

    public void removeNoMountPersistence(String playername) {
        if(plugin.getConfigHandler().enablePersistence) {
            List<PlayerOptions> po = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", playername).ieq("optionname", "nomount").findList();
            if(po == null || po.isEmpty())
                return;
            plugin.getDatabase().delete(po);
        }
    }

    public void addNoMountPersistence(String playername) {
        if(plugin.getConfigHandler().enablePersistence) {
            List<PlayerOptions> popts = plugin.getDatabase().find(PlayerOptions.class).where().ieq("name", playername).ieq("optionname", "nomount").findList();
            
            if(popts != null && !popts.isEmpty())
                removeNoMountPersistence(playername);

            PlayerOptions po = new PlayerOptions();
            po.setName(playername);
            po.setOptionname("nomount");
            
            plugin.getDatabase().save(po);
        }
    }

    public TweakcraftPlayerListener(TweakcraftUtils instance) {
        plugin = instance;
        invisplayers = new ArrayList<String>();
        nicks = new HashMap<String, String>();
    }

    public void setNick(String player, String nick) {
        nicks.put(player, nick);
        if(plugin.getConfigHandler().enablePersistence) {
            PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", player).findUnique();
            if(pi==null) {
                pi = new PlayerInfo();
                pi.setName(player);
            }
            pi.setNick(nick);
            plugin.getDatabase().save(pi);
        }
    }

    public boolean removeNick(String player) {
        if(nicks.containsKey(player)) {
            nicks.remove(player);
            if(plugin.getConfigHandler().enablePersistence) {
                PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", player).findUnique();
                if(pi==null) {
                    pi = new PlayerInfo();
                    pi.setName(player);
                }
                pi.setNick((String)null);
                plugin.getDatabase().save(pi);
            }
            return true;
        } else {
            return false;
        }
    }

    public String getNick(String player) {
        if(nicks.containsKey(player)) {
            return nicks.get(player);
        } else {
            return null;
        }
    }

    public Player findPlayerByNick(String nick) {

        String p = null;
        String n = null;

        for(String part : nicks.keySet()) {
            n = nicks.get(part);
            if(n.toLowerCase().contains(nick.toLowerCase())) {
                p = part;
            }
        }

        if(p!=null) {
            return plugin.getServer().getPlayer(p);
        }
        return null;
    }

    public List<Player> findPlayersByNick(String nick) {

        String n = null;
        List<Player> playerlijst = new ArrayList<Player>();
        for(String part : nicks.keySet()) {
            n = nicks.get(part);
            if(n.toLowerCase().contains(nick.toLowerCase())) {
                Player player = plugin.getServer().getPlayer(part);
                if(player!=null) playerlijst.add(player);
            }
        }
        return playerlijst;
    }

    public String findPlayerNameByNick(String nick) {

        String p = null;
        String n = null;

        for(String part : nicks.keySet()) {
            n = nicks.get(part);
            if(n.toLowerCase().contains(nick.toLowerCase())) {
                p = part;
            }
        }

        if(p!=null) {
            return p;
        }
        return null;
    }

    public boolean nickTaken(String nick) {
        return nicks.values().contains(nick);
    }

    public boolean nickTakenCheck(String playername, String nick) {
        if(nicks.values().contains(nick)) {
            if(getNick(playername).equals(nick))
                return false;
            else
                return true;
        }
        return false;
    }

    public boolean nickTakenPersistance(String playername, String nick) {
        if(plugin.getConfigHandler().enablePersistence) {
            PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("nick", nick).findUnique();
            if(pi!=null)
            {
                if(!pi.getName().equals(playername)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getInvisplayers() {
        return invisplayers;
    }

    public void reloadInfo() {
        playerinfo = plugin.getDatabase().find(PlayerInfo.class).findList();
        nicks.clear();
        for(PlayerInfo pi : playerinfo) {
            if(pi.getNick()!=null) {
                if(plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("[TweakcraftUtils] Setting "+pi.getName()+"'s nick to "+pi.getNick());
                nicks.put(pi.getName(), pi.getNick());
            }
        }
        playeroptions = plugin.getDatabase().find(PlayerOptions.class).findList();
        nomount.clear();
        for(PlayerOptions po : playeroptions) {
            if(po.getOptionname().equals("nomount")) {
                if(plugin.getConfigHandler().enableDebug)
                    plugin.getLogger().info("[TweakcraftUtils] Setting "+po.getName()+"'s no-ride option!");
                nomount.add(po.getName());
            }
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();
        String name = player.getName();
        plugin.getPlayerData(player).update(plugin);
        
        char[] chars = message.toCharArray();
        boolean changed = false;
        for(int i = 0; i < chars.length;i++) {
        	if(chars[i] == '^' && chars.length > (i+1)) {
        		switch(chars[i + 1]) {
        			case '0': case '1': case '2': case '3':
        			case '4': case '5': case '6': case '7':
        			case '8': case '9': case 'a': case 'b':
        			case 'c': case 'd': case 'e': case 'f':
        			case 'A': case 'B': case 'C': case 'D':
        			case 'E': case 'F':
        				chars[i] = '\u00A7';
        				changed = true;
        				i++;
        			break;
        		}
        	}
        }
        if(changed) {
        	message = new String(chars);
        	event.setMessage(message);
        }
        
        
        ChatHandler ch = plugin.getChathandler();
        ChatMode cm = ch.getPlayerChatMode(player);

        PlayerData data = plugin.getPlayerData(player);
        if(data.isMuted()) {
        	player.sendMessage(ChatColor.GOLD + data.muteToString());
        	plugin.getLogger().info("[TweakcraftUtils] Muted player message : <" + event.getPlayer().getName() + "> " + event.getMessage());
        	event.setCancelled(true);
        	return;
        }

        if(plugin.getConfigHandler().enableSpamControl) {
            int counter = 0;
            counter = ch.getAntiSpam().checkSpam(player, message);

            if(counter > (plugin.getConfigHandler().spamMaxMessages-1)) {
                
                long until = plugin.getConfigHandler().spamMuteMinutes * 60000 ;
                data.setMutetime(until + System.currentTimeMillis());
                PunishEntry entry = new PunishEntry();
                entry.set("AUTOMUTE", "ANTISPAM", data.getName(), until, "Spamming");
                plugin.getDatabase().save(entry);
                
                player.sendMessage(ChatColor.RED + "You are now auto-muted" + (until > 0 ? " for " + PlayerData.formatRemaining((int) (until/1000)) : "") + " with reason: Spamming!");
                plugin.getLogger().info("[TweakcraftUtils] ANTISPAM has muted " + data.getName() + " for " + PlayerData.formatRemaining((int) (until/1000)) + " with reason: Spamming!");
                
                String msg = plugin.getConfigHandler().spamMuteMessage.trim();
                if(!msg.equals("")) {

                    msg = msg.replace("{name}", name);
                    msg = msg.replace("{displayname}", player.getDisplayName());
                    msg = msg.replace("{mins}", Integer.toString(plugin.getConfigHandler().spamMuteMinutes));
                    msg = msg.replace("&&", "{orly}");
                    msg = msg.replace("&", "\u00A7");
                    msg = msg.replace("{orly}", "&");

                    plugin.getServer().broadcastMessage(msg);
                }
                event.setCancelled(true);
                return;
            }
        }
        
        if(plugin.getMailConcepts().containsKey(player.getEntityId())) {
            Mail mail = plugin.getMailConcepts().get(player.getEntityId());
            mail.setMessage(mail.getMessage() + "\n" + message);
            player.sendMessage(ChatColor.GOLD + "Line added to concept mail.");
            event.setCancelled(true);
        }

        if (cm != null) {
            if (!message.startsWith(plugin.getChathandler().getBypassChar())) {
                cm.sendMessage(player, message);
                event.setCancelled(true);
            } else {
                message = message.substring(1);
                event.setMessage(message);
                if(!(message.length()>0)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else if(cm == null && getInvisplayers().contains(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Are you insane? You're invisible, set a chatmode!");
            event.setCancelled(true);
        }
        

        if(!event.isCancelled() && cm==null) {
            // Log nicks!
            if(getNick(name)!=null) {
                // plugin.getLogger().info("[TweakcraftUtils] "+getNick(name)+" is "+name);
                //event.setCancelled(true);
                //plugin.getLogger().info("("+player.getName()+")  <"+player.getDisplayName()+"> "+message);
                /* if(plugin.getConfigHandler().enableIRC && plugin.getCraftIRC()!=null) {
                   plugin.getCraftIRC().sendMessageToTag();
               } */
                event.setMessage(message);
                event.setFormat(ChatColor.WHITE + "<%1$s" + ChatColor.WHITE + "> %2$s");
                //plugin.getServer().broadcastMessage(ChatColor.WHITE+"<"+player.getDisplayName()+ChatColor.WHITE+"> "+message);
            }
        }
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // if(event.isBedSpawn())
        if(!plugin.getConfigHandler().enableRespawnHook) return;

        Player p = event.getPlayer();
        String fromworld = event.getPlayer().getWorld().getName();
        boolean isnether = fromworld.endsWith("_nether");
        if(isnether) fromworld = fromworld.substring(0, fromworld.length()-7); // MINUS _nether
        World w = plugin.getServer().getWorld(fromworld);
        if(w!=null) {
            if(event.isBedSpawn()) {
                if(!event.getRespawnLocation().getWorld().getName().equals(w.getName())) {
                    p.sendMessage(ChatColor.AQUA+"Your bed was in another world, sending you to spawn!");
                    event.setRespawnLocation(w.getSpawnLocation());
                }
            } else {
                if(!event.getRespawnLocation().getWorld().getName().equals(w.getName())) {
                    p.sendMessage(ChatColor.AQUA+"Your respawn place was in another world, sending you to spawn!");
                    event.setRespawnLocation(w.getSpawnLocation()); // Removed isNether() check
                }

            }
        }
        if(plugin.getConfigHandler().enableRespawnHeal)
            p.setHealth(20);
    }

    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(event.isCancelled()) return;
        if (event.getFrom().getWorld() != event.getTo().getWorld()) { // The world is different, make a check!
            Player player = event.getPlayer();
            if(plugin.getConfigHandler().enablemod_InfDura) {
                plugin.sendToolDuraMode(player, event.getTo().getWorld());
            }
            if (!plugin.check(player, "worlds." + event.getTo().getWorld().getName())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You don't have access to this world!");
            }
        }
        // if(event)
    }


    public void onPlayerLogin(PlayerLoginEvent event) {
    	PlayerData data = plugin.getPlayerData(event.getPlayer());
    	
    	if(data.isBanned()) {
    		event.disallow(PlayerLoginEvent.Result.KICK_BANNED, data.banToString());
    		return;
    	}
    }


    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	PlayerData data = plugin.getPlayerData(player);
    	PlayerData.onLogin(plugin, player, data);
        for (String m : plugin.getMOTD()) {
        	player.sendMessage(m);
        }

        List<Mail> unread = data.findUnreadMail(plugin);
        if(unread.size() != 0) {
            player.sendMessage(ChatColor.GOLD + "You have " + unread.size() + " unread mail's, '/mail inbox' for more info.");
        }

        event.setJoinMessage(ChatColor.YELLOW + player.getDisplayName() + ChatColor.YELLOW + " joined the game.");

        if(getInvisplayers().contains(event.getPlayer().getName())) { // Invisible players do not send out a "joined" message
            event.setJoinMessage(null);
            player.sendMessage(ChatColor.AQUA + "You has joined STEALTHILY!");
            for(Player play : plugin.getServer().getOnlinePlayers()) {
                if(plugin.check(play, "tpinvis")) {
                    play.sendMessage(ChatColor.AQUA+"Stealth join : "+event.getPlayer().getDisplayName());
                }
            }
        }

        if(plugin.getConfigHandler().enableCUI) {
            plugin.sendCUIHandShake(player);
            plugin.sendCUIChatMode(player);
        }

        if(plugin.getConfigHandler().enablemod_InfDura) {
            if(!plugin.getMod_InfDuraplayers().contains(player)) {
                plugin.sendmod_InfDuraHandshake(player);
            }
            plugin.sendmod_InfDuraHandshake(player);
        }
    }

    public void onPlayerQuit(PlayerQuitEvent event) {

        String name = event.getPlayer().getName();

        
        if (plugin.getConfigHandler().enableSeenConfig) {
            Calendar cal = Calendar.getInstance();
            if(!plugin.getConfigHandler().enablePersistence) {
                String time = String.valueOf(cal.getTime().getTime());
                plugin.getConfigHandler().getSeenconfig().setProperty(name.toLowerCase(), time);
                plugin.getConfigHandler().getSeenconfig().save();
            } else {
                if(plugin.getConfigHandler().useTweakBotSeen) {
                    PlayerHistoryInfo phi = plugin.getDatabase().find(PlayerHistoryInfo.class).where().ieq("nickname", name).findUnique();
                    if(phi==null) {
                        phi = new PlayerHistoryInfo();
                        phi.setNickname(name);
                    }
                    phi.setDate(cal.getTime());
                    phi.setText("");
                    phi.setAct("QUIT");
                    phi.setChannel("gameserver");
                    plugin.getDatabase().save(phi);
                } else {
                    PlayerInfo pi = plugin.getDatabase().find(PlayerInfo.class).where().ieq("name", name).findUnique();
                    if(pi==null) {
                        pi = new PlayerInfo();
                        pi.setName(name);
                    }
                    pi.setLastseen(cal.getTime().getTime());
                    plugin.getDatabase().save(pi);
                }
            }
            if(plugin.getConfigHandler().enableDebug)
                plugin.getLogger().info("[TweakcraftUtils] Stored " + name + "'s logout!");
        }
        plugin.getChathandler().removePlayer(event.getPlayer());
        try {
            plugin.getChathandler().setPlayerchatmode(name, null);
        } catch (ChatModeException e) {
            plugin.getLogger().severe("[TweakcraftUtils] Error setting ChatMode to null after the logout!");
        }



        event.setQuitMessage(ChatColor.YELLOW + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " has left the game.");

        if(getInvisplayers().contains(name)) { // Invisible players do not send out a "left" message
            event.setQuitMessage(null);
            /* if (plugin.getCraftIRC() != null) {
                plugin.getCraftIRC().sendMessageToTag("STEALTH QUIT : " +name ,"mchatadmin");
            } */
            for(Player play : plugin.getServer().getOnlinePlayers())
            {
                if(plugin.check(play, "tpinvis"))
                {
                    play.sendMessage(ChatColor.AQUA+"Stealth quit : "+event.getPlayer().getDisplayName());
                }
            }
        }
        
        PlayerData data = plugin.getPlayerData(event.getPlayer());
        PlayerData.onLogout(plugin, event.getPlayer(), data);
    }

    /**
     *  I still don't get why my event.getItem or i keeps on nulling out, if anyone can help, please do
     */
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        String playername = player.getName();
        if(plugin.getConfigHandler().getLsbindmap().containsKey(playername)) {
            Map<Integer, Boolean> bind = plugin.getConfigHandler().getLsbindmap().get(playername);
            for(Integer i : bind.keySet()) {
                if(i == null) {
                    event.getPlayer().sendMessage(ChatColor.RED+"[TweakcraftUtils] onPlayerInteract Null error!");
                    plugin.getLogger().info("[TweakcraftUtils] "+playername+" triggered a i == null event!");
                } else {

                    if(event.isCancelled()) {
                        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            int blockid = event.getClickedBlock().getTypeId();
                            if(blockid == Material.CHEST.getId()
                                    ||blockid == Material.FURNACE.getId()
                                    ||blockid == Material.DIODE_BLOCK_OFF.getId()
                                    ||blockid == Material.DIODE_BLOCK_ON.getId()
                                    ||blockid == Material.DISPENSER.getId())
                                return;
                        }
                    }

                    if((event.getItem()==null && i.intValue()==0)
                            || (event.getItem() != null && event.getItem().getTypeId() == i.intValue())) {
                        Action a = event.getAction();
                        if((!bind.get(i) && (a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)))
                                || ( bind.get(i) && (a.equals(Action. LEFT_CLICK_AIR) || a.equals(Action. LEFT_CLICK_BLOCK)))) {
                            Location target = null;
                            if(plugin.getConfigHandler().getLockdowns().containsKey(playername)) {
                                target = plugin.getConfigHandler().getLockdowns().get(playername).getTarget();
                            } else {
                                Location loc = player.getTargetBlock(null, 200).getLocation();
                                loc.setY(loc.getY()+1);
                                target = loc.clone();
                            }
                            if(target!=null) {
                                target.getWorld().strikeLightning(target);
                            }
                        }
                    }
                }
            }
        }
        if( (event.getItem() != null && event.getItem().getType() == Material.DIAMOND_SWORD)
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            // player.sendMessage("Getting Vehicle!");
            for(LivingEntity ent : player.getWorld().getLivingEntities()) { // There is no easy way, meh
                Entity passenger = ent.getPassenger();
                if(passenger!=null)
                    if(passenger.equals(player)) {
                        // player.sendMessage("Dropping you!");
                        ent.eject();
                    }
            }
            /* if(player.getVehicle() != null) {
                player.sendMessage("Checking if instanceOf LivingEntity!");
                if(player.getVehicle() instanceof LivingEntity) {
                    player.sendMessage("It's a livingEntity!!");
                    player.getVehicle().setPassenger(null);
                }
            } */
        }
    }

    public void onPlayerKick(PlayerKickEvent event) {
        if(event.isCancelled()) return;

        Player p = event.getPlayer();
        String nick = getNick(p.getName());
        if(invisplayers.contains(p.getName())) {
            event.setLeaveMessage(null);
        } else if(nick != null) {
            event.setLeaveMessage(ChatColor.YELLOW + nick + " left the game.");
        }
    }

    public void reloadInvisTable() {
        List<String> lijst = plugin.getConfiguration().getStringList("invisible-playerlist", null);
        this.invisplayers.clear();
        if(lijst != null)
        {
            this.invisplayers.addAll(lijst);
        }
        if(plugin.getConfigHandler().enableDebug)
            for(String s : lijst)
                plugin.getLogger().info("[TweakcraftUtils] Adding "+s+" to the invisble playerlist!");
    }


    public void onPlayerPortal(PlayerPortalEvent event) {
        if(event.isCancelled()) return;
        String fromworld = event.getFrom().getWorld().getName();
        boolean isnether = fromworld.endsWith("_nether");
        if(isnether) fromworld = fromworld.substring(0, fromworld.length()-7); // MINUS _nether
        IWorld w = plugin.getworldManager().getWorld(fromworld);

        if(w!=null) {

            if(!w.isNetherEnabled()) return;

            org.bukkit.Location to = event.getFrom();
            // System.out.println("from : "+event.getFrom());
            if(isnether)  { to.setWorld(w.getBukkitWorld()); to.setX(Math.floor(to.getX()*8)); to.setZ(Math.floor(to.getZ()*8)); }
            else          { to.setWorld(w.getNetherWorld()); to.setX(Math.floor(to.getX()/8)); to.setZ(Math.floor(to.getZ()/8));}

            // TweakWorld tw = plugin.getworldManager().getWorld()

            TravelAgent agent = event.getPortalTravelAgent();
            event.setTo(to);
            event.useTravelAgent(true);

            // System.out.println("to   : "+to);

            int radius = w.getPortalSearchWidth();

            // System.out.println("Searching in a "+radius+" radius!");
            agent.setSearchRadius(radius);
            // System.out.println("After travelagent : "+event.getTo());
        }
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if(plugin.getConfigHandler().enabletamertool) {
            if(entity instanceof Wolf) {
                if(player.getItemInHand() != null &&
                        player.getItemInHand().getTypeId() == plugin.getConfigHandler().tamertoolid) {

                    if(plugin.getTamerTool().getTamers().containsKey(player)) {
                        event.setCancelled(true);
                        plugin.getTamerTool().handleEvent(player, (Wolf) entity);
                    }
                }
            }
        }
        if(player.getItemInHand() == null &&
                entity.getPassenger().equals(player)) {
            entity.eject();
        } else
        if(player.getItemInHand() != null
                && player.getItemInHand().getType() == Material.SADDLE) {
            if(entity.isEmpty()) {
                boolean allowed = true;
                if(nomount.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED+"You don't allow others to sit on you either!");
                } else {
                    if(entity instanceof Player) {
                        allowed = plugin.check(player, "mount.player");
                        if(allowed) { // TODO: Make extra checks for a "do-not-mount" option
                            allowed = !nomount.contains(((Player)entity).getName());
                        }
                    } else if (entity instanceof Pig) {
                        Pig pig = (Pig) entity;
                        if(!plugin.getConfigHandler().paySaddle && !pig.hasSaddle()) {
                            ItemStack holding = player.getItemInHand();
                            if(holding!=null) {
                                if(holding.getType() == Material.SADDLE) {
                                    holding.setAmount(holding.getAmount()+1);
                                    player.setItemInHand(holding);
                                }
                                else {
                                    player.getInventory().addItem(new ItemStack(Material.SADDLE, 1));
                                }
                            } else {
                                holding = new ItemStack(Material.SADDLE, 1);
                                player.setItemInHand(holding);
                            }

                        }
                    } else if(entity instanceof Wolf) {
                        Wolf w = (Wolf) entity;
                        if(plugin.getConfigHandler().enableAutoTame)
                            if(!w.isTamed() && plugin.check(player, "mount.autotame")) {
                                w.setOwner(player);
                                w.setAngry(false);
                                w.setSitting(false);
                                w.setHealth(20);
                                // w.sett
                            }
                    } else {
                        allowed = plugin.check(player, "mount.other");
                    }



                    if(allowed) {
                        if(plugin.getConfigHandler().paySaddle && !(entity instanceof Pig))
                        {
                            ItemStack holding = player.getItemInHand();
                            holding.setAmount(holding.getAmount()-1);
                            player.setItemInHand(holding);
                        }
                        // boolean nakedpig = entity instanceof Pig && !((Pig)entity).hasSaddle();
                        /**
                         * Minecraft handles pig saddle events just fine on its own,
                         * no need to meddle with it here.
                         */
                        if(!(entity instanceof Pig)) entity.setPassenger(player);
                    }
                    else
                        player.sendMessage(ChatColor.RED+"You are not allowed to do that!");
                }
            } else if(!(entity instanceof Pig)
                    && !entity.isEmpty()
                    && entity.getPassenger().equals(player)) {

                entity.eject();
            }
        }
    }
}